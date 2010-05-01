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

package org.itemscript.core.gwt;

import java.util.Map;

import org.itemscript.core.Params;
import org.itemscript.core.exceptions.ItemscriptError;
import org.itemscript.core.template.Template;
import org.itemscript.core.values.JsonValue;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * A class to assist in the use of the Template class from GWT and JavaScript.
 */
public class GwtTemplate {
    static native void exportCalls() /*-{
                                     $wnd.itemscript.template = {};
                                     $wnd.itemscript.template.interpretGwt = $entry(@org.itemscript.core.gwt.GwtTemplate::interpretGwt(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;));
                                     $wnd.itemscript.template.interpret = function(text, context) {
                                     return $wnd.itemscript.template.interpretGwt(text, {"value" : context});
                                     };
                                     $wnd.itemscript.template.getAndInterpretGwt = $entry(@org.itemscript.core.gwt.GwtTemplate::getAndInterpretGwt(Ljava/lang/String;Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;));
                                     $wnd.itemscript.template.getAndInterpret = function(textUrl, contextUrl, callback) {
                                     $wnd.itemscript.template.getAndInterpretGwt(textUrl, contextUrl, {
                                     "onSuccess" : function(value) {
                                     callback["onSuccess"](value["value"]);
                                     },
                                     "onError" : function(message) {
                                     callback["onError"](message);
                                     }
                                     });
                                     };
                                     }-*/;

    public static void getAndInterpretGwt(String textUrl, String contextUrl, final JavaScriptObject callback) {
        MultipleGet.get(GwtSystem.SYSTEM, new Params().p("text", GwtSystem.hostPageRelative(textUrl))
                .p("context", GwtSystem.hostPageRelative(contextUrl)), new MultipleGetCallback() {
            @Override
            public void onError(Map<String, JsonValue> responses, Map<String, Throwable> errors) {
                GwtSystem.callOnError(callback, ItemscriptError.internalError(this, "onError", errors + ""));
            }

            @Override
            public void onSuccess(Map<String, JsonValue> responses) {
                GwtSystem.callOnSuccess(callback, GwtSystem.convertAndWrapValue(Template.create(GwtSystem.SYSTEM,
                        responses.get("text"))
                        .interpretToValue(responses.get("context"))));
            }
        });
    }

    public static String interpretGwt(String text, JavaScriptObject context) {
        return Template.create(GwtSystem.SYSTEM, text)
                .interpretToString(GwtJsonParser.convert(GwtSystem.SYSTEM, context));
    }
}