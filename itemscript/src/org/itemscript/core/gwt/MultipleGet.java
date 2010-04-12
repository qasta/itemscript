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

import java.util.HashMap;
import java.util.Map;

import org.itemscript.core.HasSystem;
import org.itemscript.core.JsonSystem;
import org.itemscript.core.connectors.GetCallback;
import org.itemscript.core.values.JsonValue;

/**
 * A class to help with asychronously loading multiple resources.
 * <p>
 * Often you need to load more than one resource and have a callback that is only called when all of them have been
 * successfully loaded. Doing this with a set of individual asychronous get requests can be awkward. This class lets
 * you supply a set of URLs to be requested and a single callback that is notified when all of them have been successfully
 * loaded. 
 * 
 * @author Jacob Davies<br/><a href="mailto:jacob@itemscript.org">jacob@itemscript.org</a>
 */
public class MultipleGet implements HasSystem {
    private final Map<String, String> urls;
    private final Map<String, JsonValue> responses = new HashMap<String, JsonValue>();
    private final Map<String, Throwable> errors = new HashMap<String, Throwable>();
    private final JsonSystem system;
    private final MultipleGetCallback callback;

    @Override
    public JsonSystem system() {
        return system;
    }

    /**
     * Get all of the supplied URLs, calling the relevant method on the callback when they have all been received.
     *  
     * @param system The associated JsonSystem.
     * @param urls A Map whose keys are identifiers used to distinguish the responses, and whose values are URLs to be retrieved.
     * @param callback A callback to be called when all responses have been received.
     * @return The MultipleGet object for this request, which can be tested for completion.
     */
    public static MultipleGet get(JsonSystem system, Map<String, String> urls, final MultipleGetCallback callback) {
        MultipleGet multipleGet = new MultipleGet(system, urls, callback);
        multipleGet.start();
        return multipleGet;
    }

    /**
     * Load all of the supplied URLs into the system at the supplied location, calling the relevant method on the callback when they have
     * all been received. Values are loaded into the system immediately after being received, so even if some responses give errors, the ones
     * that succeeded will still be loaded.
     * <p>
     * Note: the destination URLs must be ones that can be accessed synchronously, for instance <code>mem:</code> URLs.
     * 
     * @param system The associated JsonSystem.
     * @param urls A Map whose keys are the URLs to load the responses at, and whose values are URLs to be retrieved.
     * @param callback A callbak to be called when all responses have been received.
     * @return The MultipleGet object for this request, which can be tested for completion.
     */
    public static MultipleGet load(final JsonSystem system, Map<String, String> urls,
            final MultipleGetCallback callback) {
        MultipleGet multipleGet = new MultipleGet(system, urls, new MultipleGetCallback() {
            @Override
            public void onError(Map<String, JsonValue> responses, Map<String, Throwable> errors) {
                callback.onError(responses, errors);
            }

            @Override
            public void onIntermediateError(Map<String, JsonValue> responses, Map<String, Throwable> errors,
                    String key, Throwable e) {
                onIntermediateError(responses, errors, key, e);
            }

            @Override
            public void onIntermediateSuccess(Map<String, JsonValue> responses, String key, JsonValue value) {
                system.put(key, value);
                onIntermediateSuccess(responses, key, value);
            }

            @Override
            public void onSuccess(Map<String, JsonValue> responses) {
                callback.onSuccess(responses);
            }
        });
        multipleGet.start();
        return multipleGet;
    }

    private MultipleGet(JsonSystem system, Map<String, String> urls, final MultipleGetCallback callback) {
        this.system = system;
        this.urls = urls;
        this.callback = callback;
    }

    private void start() {
        for (final String key : urls.keySet()) {
            final String url = urls.get(key);
            system.get(url, new GetCallback() {
                @Override
                public void onSuccess(JsonValue value) {
                    responses.put(key, value);
                    callback.onIntermediateSuccess(responses, key, value);
                    testForComplete();
                }

                @Override
                public void onError(Throwable e) {
                    // Put a null value in responses to keep the count right.
                    responses.put(key, null);
                    errors.put(key, e);
                    callback.onIntermediateError(responses, errors, key, e);
                    testForComplete();
                }
            });
        }
    }

    /**
     * Test whether all the responses in this MultipleGet have completed (successfully or in error).
     * 
     * @return True if all responses have completed, false otherwise.
     */
    public boolean isComplete() {
        return urls.size() == responses.size();
    }

    private void testForComplete() {
        // If there aren't as many responses as URLs, we aren't done yet, so don't call anything even if we had an error.
        if (isComplete()) {
            onComplete();
        }
    }

    private void onComplete() {
        // If any of the responses are null, there was an error.
        // If there were any errors, call onError; otherwise call onSuccess.
        if (errors.size() > 0) {
            callback.onError(responses, errors);
        } else {
            callback.onSuccess(responses);
        }
    }
}