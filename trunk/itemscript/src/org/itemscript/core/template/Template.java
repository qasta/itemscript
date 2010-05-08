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

import org.itemscript.core.HasSystem;
import org.itemscript.core.JsonSystem;
import org.itemscript.core.exceptions.ItemscriptError;
import org.itemscript.core.util.StaticJsonUtil;
import org.itemscript.core.values.JsonObject;
import org.itemscript.core.values.JsonString;
import org.itemscript.core.values.JsonValue;

/**
 * A compiled template that can produce either text or a JSON value.
 * <p>
 * NOTE: At present all Template interfaces are still experimental and subject to change.
 * 
 * @author Jacob Davies<br/><a href="mailto:jacob@itemscript.org">jacob@itemscript.org</a>
 */
public class Template implements HasSystem, Element {
    public static final String TEMPLATE_URL = "mem:/itemscript/template";
    public static final String TEMPLATE_CACHE_URL = TEMPLATE_URL + "/cache";
    public static final char COMMA_CHAR = ',';
    public static final char OPEN_TAG_CHAR = '{';
    public static final char CLOSE_TAG_CHAR = '}';
    public static final char OPEN_ARG_CHAR = '(';
    public static final char CLOSE_ARG_CHAR = ')';
    public static final char QUOTE_CHAR = '\'';
    public static final char COMMENT_CHAR = '#';
    public static final char LOAD_CHAR = '@';
    public static final char FIELD_CHAR = ':';
    public static final char LITERAL_CHAR = '&';
    public static final char FUNCTION_CHAR = '!';
    public static final char DIRECTIVE_CHAR = '.';

    public static String coerceToString(JsonValue value) {
        if (value == null || value.isNull()) {
            return "";
        } else if (value.isString()) {
            return value.stringValue();
        } else if (value.isNumber()) {
            return value.toJsonString();
        } else if (value.isBoolean()) {
            return value.toJsonString();
        } else {
            throw new ItemscriptError(
                    "error.itemscript.Template.coerceToString.value.could.not.be.converted.to.a.string",
                    value.toCompactJsonString());
        }
    }

    /**
     * Create a new Template from the given JsonValue. If the value is a JsonString the string value will be used
     * as a text template. If the value is a JsonArray whose values are all JsonStrings, each will be treated as a line
     * in the template. If the value is a JsonObject, it will be treated as an object template. If it is a JsonNumber,
     * JsonBoolean, or JsonNull, it will be treated as a literal value that the resulting template will return.
     * 
     * @param system The associated JsonSystem.
     * @param value The template value.
     * @return The new Template
     */
    public static Template create(JsonSystem system, JsonValue value) {
        if (value.isArray()) {
            value = StaticJsonUtil.joinArrayOfStrings(value.asArray());
        }
        if (value.isString()) {
            JsonObject cache = getCache(system);
            String text = value.stringValue();
            if (cache.containsKey(text)) {
                return (Template) cache.getNative(text);
            } else {
                Template template = new Template(system, value);
                cache.putNative(text, template);
                return template;
            }
        } else {
            return new Template(system, value);
        }
    }

    /**
     * Create a new text Template from the given string.
     * 
     * @param system The associated JsonSystem.
     * @param text The template text.
     * @return The new Template.
     */
    public static Template create(JsonSystem system, String text) {
        JsonObject cache = getCache(system);
        if (cache.containsKey(text)) {
            return (Template) cache.getNative(text);
        } else {
            Template template = new Template(system, system.createString(text));
            cache.putNative(text, template);
            return template;
        }
    }

    private static JsonObject getCache(JsonSystem system) {
        JsonObject cache = system.getObject(Template.TEMPLATE_CACHE_URL);
        if (cache == null) {
            cache = system.createObject(Template.TEMPLATE_CACHE_URL)
                    .value()
                    .asObject();
        }
        return cache;
    }

    private final JsonSystem system;
    private final Element element;
    private final String text;
    private final JsonString textValue;

    private Template(JsonSystem system, JsonValue value) {
        this.system = system;
        // If the value is a single string with no { characters in it, we know it's a static string.
        if (value.isString() && value.stringValue()
                .indexOf(Template.OPEN_TAG_CHAR) == -1) {
            this.element = null;
            this.text = value.stringValue();
            this.textValue = system().createString(text);
        } else {
            this.element = new Analyzer(system).analyze(value);
            this.text = null;
            this.textValue = null;
        }
    }

    @Override
    public JsonValue interpret(TemplateExec templateExec, JsonValue context) {
        if (element != null) {
            return element.interpret(templateExec, context);
        } else {
            return textValue;
        }
    }

    /**
     * Interpret this template using the given context, returning the result as a TemplateResult. Use this when the
     * value of a template may not be a string or when access is required to the side-effects of the template execution.
     * <p>
     * If all you need is a string result, use {@link #interpretToString(JsonValue)}. If all you need is the JsonValue
     * result, use {@link #interpretToValue(JsonValue)}.
     * 
     * @param context The context to execute the template in.
     * @return The result of executing the template.
     */
    public TemplateResult interpretToResult(JsonValue context) {
        TemplateExec templateExec = new TemplateExec(system());
        JsonValue value = interpret(templateExec, context);
        return new TemplateResult(value, templateExec.accumulator()
                .lists());
    }

    /**
     * Interpret this template using the given context, returning the result as a string.
     * 
     * @param context The context to interpret the template in.
     * @return The result of interpreting the template, as a string.
     */
    public String interpretToString(JsonValue context) {
        if (element != null) {
            return coerceToString(interpretToValue(context));
        } else {
            return text;
        }
    }

    /**
     * Interpret this template using the given context, returning the result as a JsonValue.
     * 
     * @param context The context to interpret the template in.
     * @return The result of interpreting the template.
     */
    public JsonValue interpretToValue(JsonValue context) {
        if (element != null) {
            return interpretToResult(context).value();
        } else {
            return textValue;
        }
    }

    @Override
    public JsonSystem system() {
        return system;
    }

    @Override
    public String toString() {
        return "[Template element=" + element + "]";
    }
}