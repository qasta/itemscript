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

import org.itemscript.core.ItemscriptSystem;
import org.itemscript.core.JsonSystem;
import org.itemscript.core.connectors.GetCallback;
import org.itemscript.core.connectors.PutCallback;
import org.itemscript.core.connectors.RemoveCallback;
import org.itemscript.core.exceptions.ItemscriptError;
import org.itemscript.core.values.JsonContainer;
import org.itemscript.core.values.JsonValue;
import org.itemscript.core.values.PutResponse;
import org.itemscript.core.values.RemoveResponse;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;

/**
 * An EntryPoint class for GWT that creates a static JsonSystem instance.
 * 
 * @author Jacob Davies<br/><a href="mailto:jacob@itemscript.org">jacob@itemscript.org</a>
 *
 */
public final class GwtSystem implements EntryPoint {
    private static boolean trace = false;
    private static final String VALUE_KEY = "value";
    /**
     * A single, static instance of a JsonSystem configured with a GwtConfig.
     */
    public static JsonSystem SYSTEM;

    private static native void add(JavaScriptObject array, boolean value) /*-{
                                                                          array.push(value);
                                                                          }-*/;

    private static native void add(JavaScriptObject array, double value) /*-{
                                                                         array.push(value);
                                                                         }-*/;

    private static native void add(JavaScriptObject array, JavaScriptObject value) /*-{
                                                                                   array.push(value);
                                                                                   }-*/;

    private static native void add(JavaScriptObject array, String value) /*-{
                                                                         array.push(value);
                                                                         }-*/;

    private static native void addNull(JavaScriptObject array) /*-{
                                                               array.push(null);
                                                               }-*/;

    private static native void callOnError(JavaScriptObject callback, JavaScriptObject exception) /*-{
                                                                                                  callback.onError(exception);
                                                                                                  }-*/;

    public static void callOnError(JavaScriptObject callback, Throwable e) {
        callOnError(callback, convertException(e));
    }

    public static native void callOnSuccess(JavaScriptObject callback, JavaScriptObject value) /*-{
                                                                                        callback.onSuccess(value);
                                                                                        }-*/;

    /**
     * Create a JavaScriptObject wrapper around a converted JsonValue, where the JS
     * equivalent of the JsonValue will be stored in the wrapper object under the key
     * "value".
     * 
     * @param value The value to convert and wrap.
     * @return A JavaScriptObject.
     */
    public static JavaScriptObject convertAndWrapValue(JsonValue value) {
        JavaScriptObject wrapper = createJsObject();
        if (value == null) {
            putNull(wrapper, VALUE_KEY);
        } else {
            if (value.isContainer()) {
                put(wrapper, VALUE_KEY, convertContainer(value.asContainer()));
            } else if (value.isString()) {
                put(wrapper, VALUE_KEY, value.stringValue());
            } else if (value.isNumber()) {
                put(wrapper, VALUE_KEY, value.doubleValue());
            } else if (value.isBoolean()) {
                put(wrapper, VALUE_KEY, value.booleanValue());
            } else {
                putNull(wrapper, VALUE_KEY);
            }
        }
        return wrapper;
    }

    private static JavaScriptObject convertContainer(JsonContainer container) {
        if (container == null) { return null; }
        if (container.isObject()) {
            JavaScriptObject object = createJsObject();
            for (String key : container.asObject()
                    .keySet()) {
                JsonValue value = container.asObject()
                        .get(key);
                if (value.isContainer()) {
                    put(object, key, convertContainer(value.asContainer()));
                } else if (value.isString()) {
                    put(object, key, value.stringValue());
                } else if (value.isNumber()) {
                    put(object, key, value.doubleValue());
                } else if (value.isBoolean()) {
                    put(object, key, value.booleanValue());
                } else {
                    putNull(object, key);
                }
            }
            return object;
        } else if (container.isArray()) {
            JavaScriptObject array = createJsArray();
            for (int i = 0; i < container.asArray()
                    .size(); ++i) {
                JsonValue value = container.asArray()
                        .get(i);
                if (value.isContainer()) {
                    add(array, convertContainer(value.asContainer()));
                } else if (value.isString()) {
                    add(array, value.stringValue());
                } else if (value.isNumber()) {
                    add(array, value.doubleValue());
                } else if (value.isBoolean()) {
                    add(array, value.booleanValue());
                } else {
                    addNull(array);
                }
            }
            return array;
        } else {
            throw ItemscriptError.internalError(container, "convertContainer.unknown.value.type",
                    container.toCompactJsonString());
        }
    }

    static JavaScriptObject convertException(Throwable e) {
        JavaScriptObject object = createJsObject();
        if (e instanceof ItemscriptError) {
            ItemscriptError err = (ItemscriptError) e;
            put(object, "key", err.key());
            if (err.singleParam() != null) {
                put(object, "singleParam", err.singleParam());
            }
            if (err.params() != null) {
                JavaScriptObject paramsObject = createJsObject();
                for (String key : err.params()
                        .keySet()) {
                    put(paramsObject, key, err.params()
                            .get(key));
                }
                put(object, "params", paramsObject);
            }
        }
        put(object, "message", e.getMessage());
        return object;
    }

    private static JavaScriptObject convertPutResponse(PutResponse putResponse) {
        JavaScriptObject object = createJsObject();
        put(object, "url", putResponse.url());
        put(object, "meta", convertContainer(putResponse.meta()));
        putNull(object, "value"); // TODO: For now we always set the value to null...
        return object;
    }

    private static JavaScriptObject convertRemoveResponse(RemoveResponse remove) {
        JavaScriptObject object = createJsObject();
        put(object, "meta", convertContainer(remove.meta()));
        return object;
    }

    /**
     * Copy a value from one URL to another.
     * 
     * @param fromUrl The URL to copy from.
     * @param toUrl The URL to copy to.
     * @return The response from the put operation.
     */
    public static JavaScriptObject copy(String fromUrl, String toUrl) {
        return convertPutResponse(SYSTEM.copy(fromUrl, toUrl));
    }

    /**
     * Copy a value from one URL to another, asychronously.
     * 
     * @param fromUrl The URL to copy from.
     * @param toUrl The URL to copy to.
     * @param callback The callback to call when the put operation completes.
     */
    public static void copyAsync(String fromUrl, String toUrl, final JavaScriptObject callback) {
        SYSTEM.copy(fromUrl, toUrl, new PutCallback() {
            //@Override
            public void onError(Throwable e) {
                if (callback != null) {
                    callOnError(callback, e);
                }
            }

            //@Override
            public void onSuccess(PutResponse putResponse) {
                if (callback != null) {
                    callOnSuccess(callback, convertPutResponse(putResponse));
                }
            }
        });
    }

    private static native JavaScriptObject createJsArray() /*-{
                                                           return [];
                                                           }-*/;

    private static native JavaScriptObject createJsObject() /*-{
                                                            return {};
                                                            }-*/;

    private static native JavaScriptObject createString(String value) /*-{
                                                                      return value;
                                                                      }-*/;

    private static native void exportCalls() /*-{
                                             $wnd.itemscript = {};
                                             $wnd.itemscript.getGwt = $entry(@org.itemscript.core.gwt.GwtSystem::get(Ljava/lang/String;));
                                             $wnd.itemscript.get = function(url) {
                                             return $wnd.itemscript.getGwt(url)["value"];
                                             };
                                             $wnd.itemscript.getAsyncGwt = 
                                             $entry(@org.itemscript.core.gwt.GwtSystem::getAsync(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;));
                                             $wnd.itemscript.getAsync = function(url, callback) {
                                             $wnd.itemscript.getAsyncGwt(url, {
                                             "onSuccess" : function(value) {
                                             callback["onSuccess"](value["value"]);
                                             },
                                             "onError" : function(error) {
                                             callback["onError"](error);
                                             }
                                             });
                                             };
                                             $wnd.itemscript.putGwt = $entry(@org.itemscript.core.gwt.GwtSystem::put(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;));
                                             $wnd.itemscript.put = function(url, value) {
                                             return $wnd.itemscript.putGwt(url, {"value" : value});
                                             };
                                             $wnd.itemscript.putAsyncGwt = $entry(@org.itemscript.core.gwt.GwtSystem::putAsync(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;));
                                             $wnd.itemscript.putAsync = function(url, value, callback) {
                                             $wnd.itemscript.putAsyncGwt(url, {"value" : value}, callback);
                                             };
                                             $wnd.itemscript.remove = $entry(@org.itemscript.core.gwt.GwtSystem::remove(Ljava/lang/String;));
                                             $wnd.itemscript.removeAsync = $entry(@org.itemscript.core.gwt.GwtSystem::removeAsync(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;));
                                             $wnd.itemscript.copy = $entry(@org.itemscript.core.gwt.GwtSystem::copy(Ljava/lang/String;Ljava/lang/String;));
                                             $wnd.itemscript.copyAsync = $entry(@org.itemscript.core.gwt.GwtSystem::copyAsync(Ljava/lang/String;Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;));
                                             $wnd.itemscript.setTrace = $entry(@org.itemscript.core.gwt.GwtSystem::setTrace(Z));
                                             }-*/;

    /**
     * Get a value.
     * 
     * @param url The URL to get from.
     * @return The result of the get operation.
     */
    public static Object get(String url) {
        if (trace) {
            GWT.log("itemscript.get(\"" + url + "\")", null);
        }
        return convertAndWrapValue(SYSTEM.get(url));
    }

    /**
     * Get a value, asychronously.
     * 
     * @param url The URL to get from.
     * @param callback The callback to call when the get operation completes.
     */
    public static void getAsync(String url, final JavaScriptObject callback) {
        if (trace) {
            GWT.log("itemscript.getAsync(\"" + url + "\")", null);
        }
        SYSTEM.get(url, new GetCallback() {
            //@Override
            public void onError(Throwable e) {
                callOnError(callback, e);
            }

            //@Override
            public void onSuccess(JsonValue value) {
                JavaScriptObject convertedValue = convertAndWrapValue(value);
                callOnSuccess(callback, convertedValue);
            }
        });
    }

    /**
     * Convert a URL to one relative to the host page URL.
     * 
     * @param url The relative URL.
     * @return The result of treating the URL as relative to the host page URL.
     */
    public static String hostPageRelative(String url) {
        return GwtSystem.SYSTEM.util()
                .createRelativeUrl(GWT.getHostPageBaseURL(), url) + "";
    }

    private static native void put(JavaScriptObject object, String key, boolean value) /*-{
                                                                                       object[key] = value;
                                                                                       }-*/;

    private static native void put(JavaScriptObject object, String key, double value) /*-{
                                                                                      object[key] = value;
                                                                                      }-*/;;

    private static native void put(JavaScriptObject object, String key, JavaScriptObject value) /*-{
                                                                                                object[key] = value;
                                                                                                }-*/;

    private static native void put(JavaScriptObject object, String key, String value) /*-{
                                                                                      object[key] = value;
                                                                                      }-*/;

    /**
     * Put a value.
     * 
     * @param url The URL to put under.
     * @param value The value to put.
     * @return The response from the put operation.
     */
    public static JavaScriptObject put(String url, JavaScriptObject value) {
        if (trace) {
            GWT.log("itemscript.put(\"" + url + "\")", null);
        }
        return convertPutResponse(SYSTEM.put(url, GwtJsonParser.convert(SYSTEM, value)));
    }

    /**
     * Put a value, asychronously.
     * 
     * @param url The URL to put under.
     * @param value The value to put.
     * @param callback The callback to call when the put operation completes.
     */
    public static void putAsync(String url, JavaScriptObject value, final JavaScriptObject callback) {
        if (trace) {
            GWT.log("itemscript.putAsync(\"" + url + "\")", null);
        }
        SYSTEM.put(url, GwtJsonParser.convert(SYSTEM, value), new PutCallback() {
            //@Override
            public void onError(Throwable e) {
                if (callback != null) {
                    callOnError(callback, e);
                }
            }

            //@Override
            public void onSuccess(PutResponse putResponse) {
                if (callback != null) {
                    callOnSuccess(callback, convertPutResponse(putResponse));
                }
            }
        });
    }

    private static native void putNull(JavaScriptObject object, String key) /*-{
                                                                            object[key] = null;
                                                                            }-*/;

    public static JavaScriptObject remove(String url) {
        if (trace) {
            GWT.log("itemscript.remove(\"" + url + "\")", null);
        }
        return convertRemoveResponse(SYSTEM.remove(url));
    }

    public static void removeAsync(String url, final JavaScriptObject callback) {
        if (trace) {
            GWT.log("itemscript.removeAsync(\"" + url + "\")", null);
        }
        SYSTEM.remove(url, new RemoveCallback() {
            //@Override
            public void onError(Throwable e) {
                if (callback != null) {
                    callOnError(callback, e);
                }
            }

            //@Override
            public void onSuccess(RemoveResponse removeResponse) {
                if (callback != null) {
                    callOnSuccess(callback, convertRemoveResponse(removeResponse));
                }
            }
        });
    }

    /**
     * Set tracing for this application on or off.
     * 
     * @param traceOn Whether to have tracing on or off.
     */
    public static void setTrace(boolean traceOn) {
        trace = traceOn;
    }

    private static native String stringify(JavaScriptObject object) /*-{
                                                                    return JSON.stringify(object);
                                                                    }-*/;

    //@Override
    public void onModuleLoad() {
        SYSTEM = new ItemscriptSystem(new GwtConfig());
        exportCalls();
    }
}