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
import org.itemscript.core.values.JsonValue;

/**
 * A template that can be used to produce text output from a JSON value.
 * 
 * @author Jacob Davies<br/><a href="mailto:jacob@itemscript.org">jacob@itemscript.org</a>
 */
public class Template implements HasSystem {
    private final JsonSystem system;
    private final List<Token> tokens;
    private final List<Element> elements;
    public static final char OPEN_TAG_CHAR = '{';
    public static final char CLOSE_TAG_CHAR = '}';
    public static final char COMMENT_CHAR = '#';
    public static final char LOAD_CHAR = '@';
    public static final char FIELD_CHAR = ':';
    public static final char LITERAL_CHAR = '&';
    public static final char FUNCTION_CHAR = '!';
    public static final char DIRECTIVE_CHAR = '.';
    public static final String END_DIRECTIVE = "end";
    public static final String IF_DIRECTIVE = "if";
    public static final String ELSE_DIRECTIVE = "else";
    public static final String FOREACH_DIRECTIVE = "foreach";
    public static final String JOIN_DIRECTIVE = "join";
    public static final String SECTION_DIRECTIVE = "section";
    public static final String OR_DIRECTIVE = "or";
    public static final String URL_PARAM = "url";
    public static final String HTML_PARAM = "html";
    public static final Object URI_PARAM = "uri";

    /**
     * Take a string and percent-encode the characters <code>{</code>
     * and <code>}</code> so it can safely be included in a template tag. You should only use this if
     * you know that the string is already URL-encoded or otherwise safe for use in a tag. 
     *<p>
     * If what you need to encode is a raw String, use {@link #escapeForTag}
     * 
     * @param uri The string to encode.
     * @return The string with braces encoded.
     */
    public static String encodeBraces(String uri) {
        return uri.replaceAll(OPEN_TAG_CHAR + "", "%7B")
                .replaceAll(CLOSE_TAG_CHAR + "", "%7D");
    }

    /**
     * Take a raw string and encode it for use in a template tag.
     * 
     * @param string The string to encode.
     * @return The encoded string.
     */
    public static String encodeForTag(String string) {
        return encodeBraces(Url.encode(string));
    }

    /**F
     * Create a new Template from the value at the given URL. The value must resolve to a JsonString or JsonArray of JsonStrings.
     * 
     * @param system The associated JsonSystem.
     * @param textUrl The URL to load the template text from.
     * @return A new Template.
     */
    public static Template getTemplate(JsonSystem system, String textUrl) {
        return new Template(system, system.getString(textUrl));
    }

    /**
     * Interpret the given text as a template with the given context and return the result.
     * <p>
     * If you're going to use the same template more than once, it's better to create a Template
     * object and save it, to avoid having to re-parse the template text.
     * 
     * @param system The associated JsonSystem.
     * @param text The template text.
     * @param context The context to interpret the text in.
     * @return The result of interpreting the template.
     */
    public static String interpret(JsonSystem system, String text, JsonValue context) {
        return new Template(system, text).interpret(context);
    }

    private static String textFromJsonValue(JsonValue value) {
        if (value.isString()) {
            return value.stringValue();
        } else if (value.isArray()) {
            StringBuffer sb = new StringBuffer();
            JsonArray array = value.asArray();
            for (int i = 0; i < array.size(); ++i) {
                JsonValue entry = array.get(i);
                if (!entry.isString()) { throw new ItemscriptError(
                        "error.itemscript.Template.textFromJsonValue.entry.was.not.string",
                        entry.toCompactJsonString()); }
                sb.append(entry.stringValue());
                sb.append("\n");
            }
            return sb.toString();
        }
        throw new ItemscriptError("error.itemscript.Template.textFromJsonValue.value.was.not.string.or.array",
                value.toCompactJsonString());
    }

    /**
     * Create a new Template from the given JsonValue. If the value is a JsonString the string value will be used
     * as the template. If the value is a JsonArray whose values are all JsonStrings, each will be treated as a line
     * in the template. If the value is any other type, an error will be thrown.
     * 
     * @param system The associated JsonSystem.
     * @param value The template value.
     */
    public Template(JsonSystem system, JsonValue value) {
        this(system, textFromJsonValue(value));
    }

    /**
     * Create a new Template from the given text. A compiled Template can be used repeatedly. 
     * 
     * @param system The associated JsonSystem.
     * @param text The template text.
     */
    public Template(JsonSystem system, String text) {
        this.system = system;
        this.tokens = new Scanner().scan(text);
        this.elements = new Analyzer().analyze(tokens);
    }

    /**
     * Interpret this template using the given context.
     * 
     * @param context The context to interpret the template in.
     * @return The result of interpreting the template.
     */
    public String interpret(JsonValue context) {
        return interpret(context, null);
    }

    /**
     * Interpret this template using the given context and base URL.
     * 
     * @param context The context to interpret the template in.
     * @param baseUrl The base URL to use while interpreting.
     * @return The result of interpreting the template.
     */
    public String interpret(JsonValue context, String baseUrl) {
        if (context == null) { throw ItemscriptError.internalError(this, "interpret.context.was.null"); }
        return new Interpreter(system, baseUrl).interpret(elements, context);
    }

    @Override
    public JsonSystem system() {
        return system;
    }
}