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

package examples.org.itemscript.gwt.client;

import org.itemscript.core.JsonSystem;
import org.itemscript.core.connectors.GetCallback;
import org.itemscript.core.connectors.PutCallback;
import org.itemscript.core.connectors.RemoveCallback;
import org.itemscript.core.gwt.GwtSystem;
import org.itemscript.core.util.StaticJsonUtil;
import org.itemscript.core.values.JsonValue;
import org.itemscript.core.values.PutResponse;
import org.itemscript.core.values.RemoveResponse;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * A short GWT example.
 * 
 * @author Jacob Davies<br/><a href="mailto:jacob@itemscript.org">jacob@itemscript.org</a>
 */
public class GwtExample implements EntryPoint {
    @Override
    public void onModuleLoad() {
        final JsonSystem system = GwtSystem.SYSTEM;
        GwtSystem.SYSTEM.get(GWT.getHostPageBaseURL() + "cat.json", new GetCallback() {
            @Override
            public void onSuccess(JsonValue value) {
                GwtSystem.SYSTEM.put(GWT.getHostPageBaseURL() + "test/foo", value, new PutCallback() {
                    @Override
                    public void onError(Throwable e) {
                        throw new RuntimeException(e);
                    }

                    @Override
                    public void onSuccess(PutResponse putResponse) {
                        RootPanel.get()
                                .add(new HTML(StaticJsonUtil.toHtmlJson(putResponse.value())));
                    }
                });
            }

            public void onError(Throwable e) {
                throw new RuntimeException(e);
            }
        });
        GwtSystem.SYSTEM.remove(GWT.getHostPageBaseURL() + "test/foo", new RemoveCallback() {
            @Override
            public void onError(Throwable e) {
                throw new RuntimeException(e);
            }

            @Override
            public void onSuccess(RemoveResponse removeResponse) {}
        });
    }
}