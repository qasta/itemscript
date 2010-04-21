/*
 * Copyright © 2010, Data Base Architects, Inc. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright notice,
 *       this list of conditions and the following disclaimer in the documentation
 *       and/or other materials provided with the distribution.
 *     * Neither the names of Kalinda Software, DBA Software, Data Base Architects, Itemscript
 *       nor the names of its contributors may be used to endorse or promote products derived
 *       from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * 
 * Author: Jacob Davies
 */

package org.itemscript.core.template;

import java.util.List;

import org.itemscript.core.HasSystem;
import org.itemscript.core.JsonSystem;
import org.itemscript.core.exceptions.ItemscriptError;
import org.itemscript.core.url.Url;
import org.itemscript.core.values.JsonArray;
import org.itemscript.core.values.JsonObject;
import org.itemscript.core.values.JsonString;
import org.itemscript.core.values.JsonValue;

/**
 * @author Jacob Davies<br/><a href="mailto:jacob@itemscript.org">jacob@itemscript.org</a>
 */
class Interpreter implements HasSystem {
    private final String baseUrl;
    private final JsonSystem system;
    private StringBuffer out;
    private final JsonObject functions;

    public Interpreter(JsonSystem system, String baseUrl) {
        this.system = system;
        this.baseUrl = baseUrl;
        JsonObject functionsObject = system.getObject("mem:/itemscript/template/functions");
        if (functionsObject == null) {
            functionsObject = system.createObject("mem:/itemscript/template/functions")
                    .value()
                    .asObject();
            initFunctions(functionsObject);
        }
        this.functions = functionsObject;
    }

    private void initFunctions(JsonObject functions) {
        functions.putNative("html", new HtmlEscapeFunction(system()));
        functions.putNative("url", new UrlEncodeFunction(system()));
        functions.putNative("uri", new UrlEncodeFunction(system()));
        functions.putNative("b64id", new B64idFunction(system()));
        functions.putNative("dataUrl", new DataUrlFunction(system()));
        functions.putNative("left", new LeftBraceFunction(system()));
        functions.putNative("right", new RightBraceFunction(system()));
        functions.putNative("uuid", new UuidFunction(system()));
        functions.putNative("prettyHtml", new PrettyHtmlFunction(system()));
    }

    public static String coerceToString(JsonValue value) {
        if (value == null || value.isNull()) {
            return "";
        } else if (value.isString()) {
            return value.stringValue();
        } else if (value.isNumber()) {
            return value.doubleValue() + "";
        } else if (value.isBoolean()) {
            return value.booleanValue() + "";
        } else {
            throw new ItemscriptError(
                    "error.itemscript.Template.coerceToString.value.could.not.be.converted.to.a.string",
                    value.toCompactJsonString());
        }
    }

    private JsonValue getFromContext(JsonValue context, String field) {
        if (context.isContainer()) {
            return context.asContainer()
                    .getValue(field);
        } else {
            throw ItemscriptError.internalError(this, "getFromContext.context.was.not.a.container",
                    context.toCompactJsonString());
        }
    }

    private JsonValue getFromUrl(String url, JsonValue context) {
        JsonValue value;
        if (context.item() != null) {
            // Treat as relative to the context's item().
            value = context.item()
                    .get(url);
        } else {
            // Treat as relative to the base URL, if set.
            if (baseUrl != null) {
                value = system().get(system().util()
                        .createRelativeUrl(baseUrl, url) + "");
            } else {
                // If there's no base URL and the URL starts with a "#" sign, it's an error.
                if (url.startsWith(Template.COMMENT_CHAR + "")) { throw ItemscriptError.internalError(this,
                        "getFromUrl.had.fragment-only.url.but.no.item.or.baseUrl", url); }
                // Otherwise treat it as relative to the system default URL.
                value = system().get(url);
            }
        }
        return value;
    }

    private JsonValue getValue(Segment segment, JsonValue context, List<String> fieldTokens) {
        JsonValue innerContext;
        if (fieldTokens.size() == 0) {
            innerContext = context;
        } else {
            return interpretTokenSequence(context, fieldTokens);
        }
        return innerContext;
    }

    public String interpret(List<Element> elements, JsonValue context) {
        this.out = new StringBuffer();
        interpretElements(elements, context);
        return out.toString();
    }

    private void interpretElements(List<Element> elements, JsonValue context) {
        for (int i = 0; i < elements.size(); ++i) {
            Element element = elements.get(i);
            if (element.isToken()) {
                interpretToken(element.asToken(), context);
            } else {
                interpretSegment(element.asSegment(), context);
            }
        }
    }

    private JsonValue interpretFieldToken(String value, JsonValue context) {
        String field = Url.decode(value.substring(1));
        if (field.length() == 0) {
            return context;
        } else {
            return getFromContext(context, field);
        }
    }

    private void interpretForeach(Foreach foreach, JsonValue context) {
        JsonValue innerContext = getValue(foreach, context, foreach.fieldTokens());
        if (innerContext != null && innerContext.isArray()) {
            JsonArray array = innerContext.asArray();
            boolean hasJoin = foreach.join()
                    .size() > 0;
            // For each element in the array, interpret the contents of the foreach in the context of that element.
            for (int i = 0; i < array.size(); ++i) {
                JsonValue eachContext = array.get(i);
                interpretElements(foreach.contents(), eachContext);
                // If we have a join section, interpret that for each element but the last in the surrounding context.
                if (hasJoin && i < (array.size() - 1)) {
                    interpretElements(foreach.join(), context);
                }
            }
        } else {
            // Silently ignore it if the field is missing or not an array.
        }
    }

    private JsonValue executeFunction(String token, JsonValue context, JsonValue input) {
        if (token.length() == 0) { throw ItemscriptError.internalError(this,
                "interpretFunction.no.function.specified", token); }
        Function function = (Function) functions.getNative(token);
        if (function != null) {
            return function.execute(context, input, null);
        } else {
            throw ItemscriptError.internalError(this, "interpretFunction.unknown.function", token);
        }
    }

    private void interpretIf(If ifSegment, JsonValue context) {
        JsonValue value = getValue(ifSegment, context, ifSegment.fieldTokens());
        if (isTrueValue(value)) {
            // Interpret the true section in the current context;
            interpretElements(ifSegment.trueContents(), context);
        } else {
            // Interpret the false section in the current context;
            interpretElements(ifSegment.falseContents(), context);
        }
    }

    private JsonString interpretLiteralToken(String value, JsonValue context) {
        // Trim the leading "&", decode & return the rest.
        return system().createString(Url.decode(value.substring(1)));
    }

    private void interpretSection(Section section, JsonValue context) {
        JsonValue innerContext = getValue(section, context, section.fieldTokens());
        if (innerContext != null) {
            // Interpret the regular section in the inner context;
            interpretElements(section.regularContents(), innerContext);
        } else {
            // Interpret the .or section in the current context;
            interpretElements(section.orContents(), context);
        }
    }

    private void interpretSegment(Segment segment, JsonValue context) {
        if (segment.isSection()) {
            interpretSection(segment.asSection(), context);
        } else if (segment.isForeach()) {
            interpretForeach(segment.asForeach(), context);
        } else if (segment.isIf()) {
            interpretIf(segment.asIf(), context);
        } else {
            throw ItemscriptError.internalError(this, "interpretSegment.unknown.segment.type", segment + "");
        }
    }

    private JsonValue interpretToken(String token, JsonValue context, JsonValue input) {
        if (token.length() == 0) { throw ItemscriptError.internalError(this,
                "interpretToken.token.was.zero.length"); }
        char c = token.charAt(0);
        if (c == Template.COMMENT_CHAR) {
            throw ItemscriptError.internalError(this, "interpretToken.unexpected.comment.token", token);
        } else if (c == Template.FIELD_CHAR) {
            return interpretFieldToken(token, context);
        } else if (c == Template.LOAD_CHAR) {
            return interpretLoadToken(token, context);
        } else if (c == Template.LITERAL_CHAR) {
            return interpretLiteralToken(token, context);
        } else if (Character.isLetter(c)) {
            return executeFunction(token, context, input);
        } else {
            throw ItemscriptError.internalError(this, "interpretToken.unknown.token", token);
        }
    }

    private void interpretTag(Tag tag, JsonValue context) {
        out.append(coerceToString(interpretTokenSequence(context, tag.contents())));
    }

    private JsonValue interpretTokenSequence(JsonValue context, List<String> tokens) {
        JsonValue output = system().createNull();
        for (int i = 0; i < tokens.size(); ++i) {
            String token = tokens.get(i);
            // If this token is a comment, stop interpreting the tag.
            if (token.length() > 0 && token.charAt(0) == Template.COMMENT_CHAR) {
                break;
            }
            output = interpretToken(token, context, output);
        }
        return output;
    }

    private void interpretToken(Token token, JsonValue context) {
        if (token.isDirective()) {
            // Should never happen...
            throw ItemscriptError.internalError(this, "interpretToken.encountered.directive.during.interpret",
                    token + "");
        }
        if (token.isTag()) {
            interpretTag(token.asTag(), context);
        } else {
            // If it's just text, append it.
            out.append(token.asText()
                    .text());
        }
    }

    private JsonValue interpretLoadToken(String value, JsonValue context) {
        // Trim the leading "@", treat the rest as a URL and get it.
        return getFromUrl(value.substring(1), context);
    }

    private boolean isTrueValue(JsonValue value) {
        boolean result = false;
        if (value != null) {
            if (value.isBoolean()) {
                result = value.booleanValue();
            } else if (value.isObject() || value.isArray()) {
                result = true;
            } else if (value.isNumber()) {
                if (value.doubleValue() != 0) {
                    result = true;
                }
            } else if (value.isString()) {
                if (value.stringValue()
                        .length() > 0) {
                    result = true;
                }
            }
        }
        return result;
    }

    public JsonSystem system() {
        return system;
    }
}