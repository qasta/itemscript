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

import org.itemscript.core.exceptions.ItemscriptError;
import org.itemscript.core.values.JsonValue;

/**
 * Subclass this to supply a callback to {@link MultipleGet}.
 * <p>
 * Most uses will only need you to implement {@link #onSuccess}.
 * 
 * @author Jacob Davies<br/><a href="mailto:jacob@itemscript.org">jacob@itemscript.org</a>
 */
public abstract class MultipleGetCallback {
    /**
     * Called when all responsese have been received if any of them generated errors. The default implementation throws a new exception.
     * 
     * @param responses A map of the successful responses with the original keys from the request as keys and the responses as values. Responses that generated
     * an error will be present with a Java null value.
     * @param errors A map of the errors generated with the original keys from the request as keys and the errors as values.
     */
    public void onError(Map<String, JsonValue> responses, Map<String, Throwable> errors) {
        throw ItemscriptError.internalError(this, "onError", errors + "");
    }

    /**
     * Called when an individual response generates an error. The default implementation does nothing.
     * 
     * @param responses The map of responses received so far.
     * @param errors The map of errors generated so far.
     * @param key The key for the response that just generated an error.
     * @param e The error that was generated.
     */
    public void onIntermediateError(Map<String, JsonValue> responses, Map<String, Throwable> errors, String key,
            Throwable e) {}

    /**
     * Called when an individual response is successfully received. The default implementation does nothing.
     * 
     * @param responses The map of responses received so far.
     * @param key The key for the response that just returned.
     * @param value The value returned for this individual response.
     */
    public void onIntermediateSuccess(Map<String, JsonValue> responses, String key, JsonValue value) {}

    /**
     * Called when all responses have been received and all were successful.
     * 
     * @param responses A map with the original keys from the request as keys and the responses as values.
     */
    public abstract void onSuccess(Map<String, JsonValue> responses);
}