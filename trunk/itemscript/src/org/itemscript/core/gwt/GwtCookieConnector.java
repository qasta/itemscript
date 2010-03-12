
package org.itemscript.core.gwt;

import java.util.Date;

import org.itemscript.core.HasSystem;
import org.itemscript.core.JsonSystem;
import org.itemscript.core.connectors.SyncGetConnector;
import org.itemscript.core.connectors.SyncPutConnector;
import org.itemscript.core.url.Url;
import org.itemscript.core.values.ItemscriptPutResponse;
import org.itemscript.core.values.ItemscriptRemoveResponse;
import org.itemscript.core.values.JsonValue;
import org.itemscript.core.values.PutResponse;
import org.itemscript.core.values.RemoveResponse;

import com.google.gwt.user.client.Cookies;

/**
 * A connector class that allows you to store values in browser cookies.
 * <p>
 * Only the path portion of supplied URLs will be used to distinguish between cookies.
 * <p>
 * Note: This is experimental and might change or go away if it proves not to be useful.
 * There are some potential problems with multiple browser windows attempting to access
 * the same cookie that have not been addressed.
 * 
 * @author Jacob Davies<br/><a href="mailto:jacob@itemscript.org">jacob@itemscript.org</a>
 */
public class GwtCookieConnector implements SyncGetConnector, SyncPutConnector, HasSystem {
    private final JsonSystem system;
    public static final long TEN_YEARS_MILLIS = 315569259747l;

    /**
     * Create a new GwtCookieConnector.
     * 
     * @param system The associated JsonSystem.
     */
    public GwtCookieConnector(JsonSystem system) {
        this.system = system;
    }

    @Override
    public JsonValue get(Url url) {
        JsonValue value = system().parse(Cookies.getCookie(url.pathString()));
        system().createItem(url + "", value);
        return value;
    }

    @Override
    public PutResponse put(Url url, JsonValue value) {
        Cookies.setCookie(url.pathString(), value.toCompactJsonString(), new Date(new Date().getTime()
                + TEN_YEARS_MILLIS));
        JsonValue setValue = value.copy();
        system().createItem(url + "", setValue);
        return new ItemscriptPutResponse(url + "", null, null);
    }

    @Override
    public RemoveResponse remove(Url url) {
        Cookies.removeCookie(url.pathString());
        return new ItemscriptRemoveResponse(null);
    }

    @Override
    public JsonSystem system() {
        return system;
    }
}