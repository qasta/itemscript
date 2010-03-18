
package org.itemscript.core.gwt;

import org.itemscript.core.HasSystem;
import org.itemscript.core.JsonSystem;
import org.itemscript.core.connectors.SyncGetConnector;
import org.itemscript.core.url.Url;
import org.itemscript.core.values.JsonValue;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * A connector class that allows you to access values in JavaScript objects defined in the host page.
 * <p>
 * Only the path portion of supplied URLs will be used to distinguish between cookies.
 * <p>
 * Note: This is experimental and might change or go away if it proves not to be useful.
 * There are some potential problems with multiple browser windows attempting to access
 * the same cookie that have not been addressed.
 * 
 * @author Jacob Davies<br/><a href="mailto:jacob@itemscript.org">jacob@itemscript.org</a>
 */
public class GwtJavaScriptConnector implements SyncGetConnector, HasSystem {
    private final JsonSystem system;

    /**
     * Create a new GwtCookieConnector.
     * 
     * @param system The associated JsonSystem.
     */
    public GwtJavaScriptConnector(JsonSystem system) {
        this.system = system;
    }

    private native JavaScriptObject get(String name) /*-{
                                                     return { "value" : $wnd[name] };
                                                     }-*/;

    @Override
    public JsonValue get(Url url) {
        JsonValue value = GwtJsonParser.convert(system(), get(url.remainder()));
        return system().createItem(url + "", value)
                .value();
    }

    @Override
    public JsonSystem system() {
        return system;
    }
}