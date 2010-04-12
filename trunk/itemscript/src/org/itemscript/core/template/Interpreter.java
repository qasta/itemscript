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
import org.itemscript.core.Params;
import org.itemscript.core.exceptions.ItemscriptError;
import org.itemscript.core.url.Url;
import org.itemscript.core.util.GeneralUtil;
import org.itemscript.core.values.JsonArray;
import org.itemscript.core.values.JsonValue;

/**
 * @author Jacob Davies<br/><a href="mailto:jacob@itemscript.org">jacob@itemscript.org</a>
 */
class Interpreter implements HasSystem {
    private final String baseUrl;
    private final JsonSystem system;
    private StringBuffer out;

    public Interpreter(JsonSystem system, String baseUrl) {
        this.system = system;
        this.baseUrl = baseUrl;
    }

    private String coerceToString(JsonValue value) {
        if (value == null || value.isNull()) {
            return "";
        } else if (value.isString()) {
            return value.stringValue();
        } else if (value.isNumber()) {
            return value.doubleValue() + "";
        } else if (value.isBoolean()) {
            return value.booleanValue() + "";
        } else {
            throw ItemscriptError.internalError(this, "coerceToString.value.could.not.be.converted.to.a.string",
                    value.toCompactJsonString());
        }
    }

    private String getFromContext(JsonValue context, String field) {
        if (context.isContainer()) {
            return coerceToString(context.asContainer()
                    .getValue(field));
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

    private JsonValue getValue(Segment segment, JsonValue context, String field) {
        JsonValue innerContext;
        if (field.length() == 0) {
            innerContext = context;
        } else {
            if (!context.isContainer()) { throw ItemscriptError.internalError(this,
                    "getInnerContext.had.field.but.context.was.not.container", new Params().p("segment", segment)
                            .p("context", context.toCompactJsonString())); }
            innerContext = context.asContainer()
                    .getValue(field);
        }
        return innerContext;
    }

    public String interpret(List<Element> elements, JsonValue context) {
        this.out = new StringBuffer();
        interpretElements(elements, context);
        return out.toString();
    }

    private String interpretBareFieldValue(String value, JsonValue context) {
        return getFromContext(context, Url.decode(value));
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

    private String interpretFieldValue(String value, JsonValue context) {
        String field = Url.decode(value.substring(1));
        if (field.length() == 0) {
            return coerceToString(context);
        } else {
            return getFromContext(context, field);
        }
    }

    private void interpretForeach(Foreach foreach, JsonValue context) {
        JsonValue innerContext = getValue(foreach, context, foreach.field());
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

    private String interpretFunction(String first, JsonValue context) {
        String rest = first.substring(1);
        if (rest.length() == 0) { throw ItemscriptError.internalError(this,
                "interpretFunction.no.function.specified", first); }
        // FIXME - this should really look up functions somewhere, but...
        if (rest.equals("b64id")) {
            return system().util()
                    .generateB64id();
        } else {
            throw ItemscriptError.internalError(this, "interpretFunction.unknown.function", first);
        }
    }

    private void interpretIf(If ifSegment, JsonValue context) {
        JsonValue value = getValue(ifSegment, context, ifSegment.field());
        if (isTrueValue(value)) {
            // Interpret the true section in the current context;
            interpretElements(ifSegment.trueContents(), context);
        } else {
            // Interpret the false section in the current context;
            interpretElements(ifSegment.falseContents(), context);
        }
    }

    private String interpretLiteralValue(String value, JsonValue context) {
        // Trim the leading "&", decode & return the rest.
        return Url.decode(value.substring(1));
    }

    private void interpretSection(Section section, JsonValue context) {
        JsonValue innerContext = getValue(section, context, section.field());
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

    private void interpretTag(Tag tag, JsonValue context) {
        if (tag.size() == 0) {
            // If the tag was empty, we're referring to the context itself.
            out.append(coerceToString(context));
        } else {
            String first = tag.get(0);
            String value;
            char firstChar = first.charAt(0);
            if (Character.isLetter(firstChar) || Character.isDigit(firstChar)) {
                // If it starts with a letter or digit, un-encode it and treat it as a field name in the
                // context.
                value = interpretBareFieldValue(first, context);
            } else if (firstChar == Template.FIELD_CHAR) {
                // If it starts with a ":" treat the rest of the value as a field name.
                value = interpretFieldValue(first, context);
            } else if (firstChar == Template.LOAD_CHAR) {
                // If it starts with an "@" sign treat the rest of the value as a URL.
                value = interpretUrlValue(first, context);
            } else if (firstChar == Template.COMMENT_CHAR) {
                // If it starts with a "#" treat it as a comment.
                return;
            } else if (firstChar == Template.LITERAL_CHAR) {
                // If it starts with a "&" treat it as an encoded string literal.
                value = interpretLiteralValue(first, context);
            } else if (firstChar == Template.FUNCTION_CHAR) {
                // If it starts with a "!" treat it as a function.
                value = interpretFunction(first, context);
            } else if (firstChar == Template.OPEN_PARAN_CHAR) {
                value = Template.OPEN_TAG_CHAR + "";
            } else if (firstChar == Template.CLOSE_PARAN_CHAR) {
                value = Template.CLOSE_TAG_CHAR + "";
            } else {
                throw ItemscriptError.internalError(this, "interpretTag.couldnt.handle.tag", tag + "");
            }
            // Silently ignore null values.
            if (value != null) {
                if (tag.size() > 1) {
                    String next = tag.get(1);
                    if (next.equals(Template.URL_PARAM) || next.equals(Template.URI_PARAM)) {
                        value = Url.encode(value);
                    } else if (next.equals(Template.HTML_PARAM)) {
                        value = GeneralUtil.htmlEncode(value);
                    } else {
                        throw ItemscriptError.internalError(this, "interpretTag.unknown.parameter", tag + "");
                    }
                    if (tag.size() > 2) { throw ItemscriptError.internalError(this,
                            "interpretTag.unknown.parameter", tag + ""); }
                }
                out.append(value);
            }
        }
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

    private String interpretUrlValue(String value, JsonValue context) {
        // Trim the leading "@", treat the rest as a URL and get it.
        return coerceToString(getFromUrl(value.substring(1), context));
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