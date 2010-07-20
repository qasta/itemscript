/*
 * Copyright � 2010, Data Base Architects, Inc. All rights reserved.
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

package org.itemscript.template;

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
    public static final char CONSTANT_CHAR = '*';

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
    
    /**
     * Sets the user-friendly error message retrieved from the returned errorObject after it's been
     * validated. Only deals with error messages seen by the user at runtime.
     * 
     * @param returnedObject
     * @return the error message if the validate failed, a null string otherwise
     */
    public static String setErrorMessage(JsonObject returnedObject) {
    	String displayError = null;
    	if (returnedObject.get("valid").booleanValue() == false) {
    		String incorrectValue = returnedObject.get("incorrectValue").stringValue();
        	String correctValue = returnedObject.get("correctValue").stringValue();
        	String value = returnedObject.get("value").stringValue();
    		String errorMessage = returnedObject.get("message").stringValue();
    		
    		String[] split = errorMessage.split("\\s+");
    		int begin = split[0].indexOf("value");
    		if (begin >= 0) {
	    		String error = split[0].substring(begin);
	    		
	    		//General Type errors
	    		if (error.equals("value.was.null")) {
	    			displayError = "Value cannot be null.";
	    		}
	    		if (error.equals("value.was.not.null")) {
	    			displayError = "Value '" + value + "' was not null.";
	    		}
	    		if (error.equals("value.was.not.object")) {
	    			displayError = "Value '" + value + "' was not an object.";
	    		}
	    		if (error.equals("value.was.not.string")) {
	    			displayError = "Value '" + value + "' was not a string.";
	    		}
	    		if (error.equals("value.was.not.array")) {
	    			displayError = "Value '" + value + "' was not an array.";
	    		}
	    		if (error.equals("value.was.not.boolean")) {
	        		displayError = "Value '" + value + "' was not a boolean.";
	    		}
	    		if (error.equals("value.was.not.number")) {
	    			displayError = "Value '" + value + "' was not a number.";
	    		}
	    		if (error.equals("value.was.not.proper.decimal")) {
	    			displayError = "Value '" + value + "' was not a decimal.";
	    		}
	    		if (error.equals("value.could.not.be.parsed.into.long")) {
	    			displayError = "Value '" + value + "' was not a long.";
	    		}
	    		if (error.equals("value.had.fractional.digits")) {
	    			displayError = "Value '" + value + "' was not an integer.";
	    		}
	    		
	    		//AnyType errors
	    		if (error.equals("value.was.not.of.specified.type")) {
	    			displayError = "Your value '" + value +
	    				"' was not one of the types specified.";
	    		}
	    			    		
	    		//ArrayType errors
	    		if (error.equals("value.array.is.the.wrong.size")) {
	    			displayError = "Your array has the wrong number of items." +
    					" Your size is " + incorrectValue + "." +
    					" The correct size is " + correctValue + ".";
	    		}
	    		if (error.equals("value.array.size.is.bigger.than.max")) {
	    			displayError = "Your array has too many items." +
	    				" Your size is " + incorrectValue + "." +
	    				" The maximum size is " + correctValue + ".";
	    		}
	    		if (error.equals("value.array.size.is.smaller.than.min")) {
	    			displayError = "Your array does not have enough items." +
	    				" Your size is " + incorrectValue + "." +
	    				" The minimum size is " + correctValue + ".";
	    		}
	    		
	    		//BinaryType errors
	    		if (error.equals("value.could.not.be.parse.as.base.64")) {
	    			displayError = "Your value '" + value +
	    				"' could not be parsed as base64.";
	    		}
	    		if (error.equals("value.illegal.character.in.base.64.encoded.data")) {
	    			displayError = "Your value '" + value +
	    				"' contains an illegal character that cannot be parsed into base64.";
	    		}
	    		if (error.equals("value.has.too.many.bytes")) {
	    			displayError = "Your value has more than the max number of bytes allowed." +
	    				" Your byte size is " + incorrectValue + "." +
	    				" The maximum byte size is " + correctValue + ".";
	    		}
	    		
	    		//BooleanType errors
	    		if (error.equals("value.does.not.equal.required.boolean.value")) {
	    			displayError = "Your value does not match the required boolean value.";
	    		}
	    		
	    		//DecimalType errors
	    		if (error.equals("value.could.not.be.parsed.into.double")) {
	    			displayError = "Your value '" + value + "' could not be parsed into a Java Double.";
	    		}
	    		if (error.equals("value.has.wrong.number.of.fraction.digits")) {
	    			displayError = "Your value '" + value + "' has " + incorrectValue + " fractional digits." +
	    				" The maximum number of fractional digits is " + correctValue + ".";
	    		}
	    		
	    		//ObjectType errors
	        	String keyValue = returnedObject.get("keyValue").stringValue();
	    		if (error.equals("value.extra.instance.keys.did.not.all.match.wildcard.type")) {
	    			displayError = "Some of your key-values failed to match the wildcard type.";
	    		}
	    		if (error.equals("value.missing.value.for.key")) {
	    			displayError = "Missing value for key: " + keyValue + ".";
	    		}
	    		if (error.equals("value.instance.key.was.empty")) {
	    			displayError = "Cannot have an empty key in your instance object.";
	    		}
	    		
	    		//StringType errors
	    		if (error.equals("value.does.not.equal.equals")) {
	    			displayError = "Your value " + value + " does not equal the specified value.";
	    		}
	    		if (error.equals("value.does.not.equal.is.length")) {
	    			displayError = "Your value is the wrong length." +
	    				" Your value is " + incorrectValue + " characters long." +
	    				" The correct length is " + correctValue + " characters.";
	    		}
	    		if (error.equals("value.longer.than.max.length")) {
	    			displayError = "Your value is too long." +
	    				" Your value is " + incorrectValue + " characters long." +
	    				" The max length is " + correctValue + " characters.";
	    		}
	    		if (error.equals("value.shorter.than.min.length")) {
	    			displayError = "Your value is too short." +
	    				" Your value is " + incorrectValue + " characters long." +
	    				" The minimum length is " + correctValue + " characters.";
	    		}
	    		if (error.equals("value.does.not.match.reg.ex.pattern")) {
	    			displayError = "Your value '" + value + "' did not match the regular expression pattern.";    			;
	    		}
	    		if (error.equals("value.did.not.match.any.pattern")) {
	    			displayError = "Your value '" + value + "' did not match any of the patterns that were specified.";
	    		}
	    		
	    		//Numericality errors (applies to decimal, long, number, integer)
	    		if (error.equals("value.is.not.equal.to.equal.to")) {
	    			displayError = "Your number '" + value + "' is not equal to the specified value.";
	    		}
	    		if (error.equals("value.is.less.than.or.equal.to.min")) {
	    			displayError = "Your number must be greater than the minimum value provided." +
	    				" Your number is '" + incorrectValue + "'." +
	    				" The minimum value is '" + correctValue + "'.";
	    		}
	    		if (error.equals("value.is.less.than.min")) {
	    			displayError = "Your number must be greater than or equal to the minimum value provided." +
	    				" Your number is '" + incorrectValue + "'." +
	    				" The minimum value is '" + correctValue + "'.";
	    		}
	    		if (error.equals("value.is.greater.than.or.equal.to.max")) {
	    			displayError = "Your number must be less than the maximum value provided." +
	    				" Your number is '" + incorrectValue + "'." +
	    				" The maximum value is '" + correctValue + "'.";
	    		}
	    		if (error.equals("value.is.greater.than.max")) {
	    			displayError = "Your number must be less than or equal to the maximum value provided." +
	    				" Your number is '" + incorrectValue + "'." +
	    				" The maximum value is '" + correctValue + "'.";
	    		}
	    		if (error.equals("value.cannot.test.parity.value.is.not.an.integer")) {
	    			displayError = "Your number '" + value + "' cannot be tested for even/odd because it is not an integer.";
	    		}
	    		if (error.equals("value.is.not.even")) {
	    			displayError = "Your number '" + value + "' is not even." ;
	    		}
	    		if (error.equals("value.is.not.odd")) {
	    			displayError = "Your number '" + value + "' is not odd.";
	    		}
	    		
	    		//Inclusion + Exclusion errors
	    		if (error.equals("value.did.not.match.a.valid.choice")) {
	    			displayError = "Your value '" + value + "' did not match any of the choices in your array.";
	    		}
	    		if (error.equals("value.matched.an.invalid.choice")) {
	    			displayError = "Your value '" + value + "' matched one of the invalid choices in your array.";
	    		}
    		} else {
    			throw new ItemscriptError(
                        "error.itemscript.Template.error.message.parsed.wrong", split[0]);
    		}
    	}
    	return displayError;
    }
}