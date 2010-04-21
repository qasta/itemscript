
package org.itemscript.core.gwt;

import org.itemscript.core.HasSystem;
import org.itemscript.core.JsonSystem;
import org.itemscript.core.connectors.AsyncGetConnector;
import org.itemscript.core.connectors.GetCallback;
import org.itemscript.core.exceptions.ItemscriptError;
import org.itemscript.core.url.Url;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.jsonp.client.JsonpRequestBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class JsonpConnector implements AsyncGetConnector, HasSystem {
    private final JsonSystem system;

    public JsonpConnector(JsonSystem system) {
        this.system = system;
    }

    @Override
    public void get(final Url url, final GetCallback callback) {
        // Split the URL.
        String remainder = url.remainder();
        int firstSemiColon = remainder.indexOf(';');
        int firstComma = remainder.indexOf(',');
        if (firstComma == -1) { throw ItemscriptError.internalError(this, "get.invalid.url", url + ""); }
        String callbackParam;
        String errorCallbackParam = null;
        // Is there a semicolon in the first section?
        if (firstSemiColon != -1 && firstSemiColon < firstComma) {
            callbackParam = remainder.substring(0, firstSemiColon);
            errorCallbackParam = remainder.substring(firstSemiColon + 1, firstComma);
        } else {
            callbackParam = remainder.substring(0, firstComma);
        }
        final String jsonpUrl = remainder.substring(firstComma + 1);
        JsonpRequestBuilder builder = new JsonpRequestBuilder();
        builder.setCallbackParam(callbackParam);
        if (errorCallbackParam != null) {
            builder.setFailureCallbackParam(errorCallbackParam);
        }
        builder.requestObject(jsonpUrl, new AsyncCallback<JavaScriptObject>() {
            @Override
            public void onFailure(Throwable e) {
                callback.onError(e);
            }

            public void onSuccess(JavaScriptObject result) {
                callback.onSuccess(system().createItem(url + "", system.createObject(),
                        GwtJsonParser.convertObject(system, result))
                        .value());
            }
        });
    }

    @Override
    public JsonSystem system() {
        return system;
    }
}
