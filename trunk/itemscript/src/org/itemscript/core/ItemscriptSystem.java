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

package org.itemscript.core;

import org.itemscript.core.config.JsonConfig;
import org.itemscript.core.connectors.AsyncGetConnector;
import org.itemscript.core.connectors.AsyncPutConnector;
import org.itemscript.core.connectors.Connector;
import org.itemscript.core.connectors.GetCallback;
import org.itemscript.core.connectors.MemConnector;
import org.itemscript.core.connectors.PutCallback;
import org.itemscript.core.connectors.RemoveCallback;
import org.itemscript.core.connectors.SyncBrowseConnector;
import org.itemscript.core.connectors.SyncGetConnector;
import org.itemscript.core.connectors.SyncPutConnector;
import org.itemscript.core.exceptions.ItemscriptError;
import org.itemscript.core.url.Query;
import org.itemscript.core.url.Url;
import org.itemscript.core.util.JsonAccessHelper;
import org.itemscript.core.values.ItemscriptContainer;
import org.itemscript.core.values.JsonArray;
import org.itemscript.core.values.JsonBoolean;
import org.itemscript.core.values.JsonCreator;
import org.itemscript.core.values.JsonItem;
import org.itemscript.core.values.JsonNative;
import org.itemscript.core.values.JsonNull;
import org.itemscript.core.values.JsonNumber;
import org.itemscript.core.values.JsonObject;
import org.itemscript.core.values.JsonString;
import org.itemscript.core.values.JsonValue;

/**
 * The implementation class for JsonSystem. In order to configure system- or platform- specific behavior,
 * you can supply a {@link JsonConfig} object, rather than reimplementing this class.
 * 
 * @author Jacob Davies<br/><a href="mailto:jacob@itemscript.org">jacob@itemscript.org</a>
 */
public final class ItemscriptSystem implements JsonSystem {
    private final JsonCreator factory;
    private final JsonConfig config;
    private final JsonObject connectors;

    /**
     * Create a new JsonSystem implementation using the supplied JsonConfig.
     * 
     * JsonConfig implementations will usually provide a static factory method for creating
     * a new system with a config of that type; for instance, {@link org.itemscript.standard.StandardConfig#createSystem}.
     * 
     * @param config The JsonConfig object used to set up this system.
     */
    public ItemscriptSystem(JsonConfig config) {
        this.config = config;
        factory = config.createJsonCreator(this);
        connectors = MemConnector.create(this);
        config.seedSystem(this);
    }

    @Override
    public void copy(String fromUrl, String toUrl) {
        copy(Url.create(fromUrl), Url.create(toUrl));
    }

    @Override
    public void copy(Url fromUrl, Url toUrl) {
        put(toUrl, get(fromUrl).copy());
    }

    @Override
    public JsonArray createArray() {
        return factory.createArray();
    }

    @Override
    public JsonBoolean createBoolean(Boolean value) {
        return factory().createBoolean(value);
    }

    @Override
    public JsonItem createItem(String url, JsonValue value) {
        return factory.createItem(url, value);
    }

    @Override
    public JsonNative createNative(Object nativeValue) {
        return factory.createNative(nativeValue);
    }

    @Override
    public JsonNull createNull() {
        return factory.createNull();
    }

    @Override
    public JsonNumber createNumber(Double value) {
        return factory().createNumber(value);
    }

    @Override
    public JsonNumber createNumber(Float value) {
        return factory().createNumber(value);
    }

    @Override
    public JsonNumber createNumber(Integer value) {
        return factory().createNumber(value);
    }

    @Override
    public JsonObject createObject() {
        return factory().createObject();
    }

    private Url createRootRelativeUrl(Url url) {
        return Url.createRelative(ROOT_URL, url);
    }

    @Override
    public JsonString createString(byte[] contents) {
        return factory().createString(contents);
    }

    @Override
    public JsonString createString(Long value) {
        return factory().createString(value);
    }

    @Override
    public JsonString createString(String value) {
        return factory().createString(value);
    }

    @Override
    public final JsonValue dereference(String key) {
        return JsonAccessHelper.dereference(getValue(key));
    }

    @Override
    public final void dereference(String key, final GetCallback callback) {
        get(key, new GetCallback() {
            @Override
            public void onSuccess(JsonValue value) {
                value.dereference(callback);
            }

            @Override
            public void onError(Throwable e) {
                callback.onError(e);
            }
        });
    }

    private JsonCreator factory() {
        return factory;
    }

    @Override
    public String generateUuid() {
        return config.generateUuid();
    }

    @Override
    public JsonValue get(String url) {
        return get(Url.create(url));
    }

    @Override
    public void get(final String url, final GetCallback callback) {
        get(Url.create(url), callback);
    }

    @Override
    public JsonValue get(Url url) {
        Url absoluteUrl = createRootRelativeUrl(url);
        Url withoutFragment = absoluteUrl.withoutFragment();
        JsonValue value;
        Connector connector = getConnector(withoutFragment);
        // If we have a query and the connector is an instance of a SyncBrowseConnector, use the methods
        // from that interface.
        if (withoutFragment.hasQuery() && connector instanceof SyncBrowseConnector) {
            Query query = withoutFragment.query();
            SyncBrowseConnector browseConnector = (SyncBrowseConnector) connector;
            if (query.isCountItemsQuery()) {
                value = browseConnector.countItems(withoutFragment);
            } else if (query.isPagedItemsQuery()) {
                value = browseConnector.pagedItems(withoutFragment);
            } else if (query.isPagedKeysQuery()) {
                value = browseConnector.pagedKeys(withoutFragment);
            } else if (query.isKeysQuery()) {
                value = browseConnector.getKeys(withoutFragment);
            } else {
                value = browseConnector.otherQuery(withoutFragment);
            }
        } else {
            // Otherwise just use the regular SyncGetConnector interface.
            value = ((SyncGetConnector) getConnector(withoutFragment)).get(withoutFragment);
        }
        if (absoluteUrl.hasFragment()) {
            if (absoluteUrl.fragment()
                    .size() == 0) { return value; }
            if (value.isContainer()) {
                return ((ItemscriptContainer) value).getByFragment(absoluteUrl.fragment());
            } else {
                throw ItemscriptError.internalError(this, "get.had.fragment.but.value.was.not.a.container",
                        new Params().p("url", absoluteUrl + "")
                                .p("value", value + ""));
            }
        } else {
            return value;
        }
    }

    @Override
    public void get(Url url, final GetCallback callback) {
        Url finalUrl = createRootRelativeUrl(url);
        Connector connector = getConnector(finalUrl);
        if (connector instanceof AsyncGetConnector) {
            ((AsyncGetConnector) connector).get(finalUrl, new GetCallback() {
                public void onError(Throwable e) {
                    callback.onError(e);
                }

                public void onSuccess(JsonValue value) {
                    callback.onSuccess(value);
                }
            });
        } else if (connector instanceof SyncGetConnector) {
            try {
                callback.onSuccess(get(url));
            } catch (ItemscriptError e) {
                callback.onError(e);
            }
        } else {
            throw ItemscriptError.internalError(this,
                    "get.connector.was.not.AsyncGetConnector.or.SyncGetConnector");
        }
    }

    @Override
    public JsonArray getArray(String url) {
        return JsonAccessHelper.asArray(get(url));
    }

    @Override
    public byte[] getBinary(String url) {
        return JsonAccessHelper.asBinary(get(url));
    }

    @Override
    public Boolean getBoolean(String url) {
        return JsonAccessHelper.asBoolean(get(url));
    }

    /**
     * Get the connector for the scheme of the supplied URL.
     * 
     * @param url The URL to find the scheme in.
     * @return The Connector object corresponding to that scheme.
     */
    private Connector getConnector(Url url) {
        JsonValue connectorValue = connectors.get(url.scheme());
        Connector connector = null;
        if (connectorValue != null) {
            connector = (Connector) connectorValue.nativeValue();
        }
        if (connector == null) { throw new ItemscriptError(
                "error.itemscript.JsonSystem.getValue.no.connector.found.for.scheme", new Params().p("scheme",
                        url.scheme())
                        .p("url", url + "")); }
        return connector;
    }

    @Override
    public Double getDouble(String url) {
        return JsonAccessHelper.asDouble(get(url));
    }

    @Override
    public Float getFloat(String url) {
        return JsonAccessHelper.asFloat(get(url));
    }

    @Override
    public Integer getInt(String url) {
        return JsonAccessHelper.asInt(get(url));
    }

    @Override
    public Long getLong(String url) {
        return JsonAccessHelper.asLong(get(url));
    }

    @Override
    public Object getNative(String url) {
        return JsonAccessHelper.asNative(getValue(url));
    }

    @Override
    public JsonObject getObject(String url) {
        return JsonAccessHelper.asObject(get(url));
    }

    @Override
    public String getString(String url) {
        return JsonAccessHelper.asString(get(url));
    }

    @Override
    public JsonValue getValue(String url) {
        return get(url);
    }

    /**
     * Get a random int.
     * 
     * @return A random int.
     */
    public int nextRandomInt() {
        return config.nextRandomInt();
    }

    @Override
    public JsonValue parse(String json) {
        return factory().parse(json);
    }

    @Override
    public JsonValue parseReader(Object reader) {
        return factory().parseReader(reader);
    }

    @Override
    public JsonBoolean put(String url, Boolean value) {
        JsonBoolean jsonValue = createBoolean(value);
        put(url, jsonValue);
        return jsonValue;
    }

    @Override
    public JsonString put(String url, byte[] value) {
        JsonString jsonValue = createString(value);
        put(url, jsonValue);
        return jsonValue;
    }

    @Override
    public JsonNumber put(String url, Double value) {
        JsonNumber jsonValue = createNumber(value);
        put(url, jsonValue);
        return jsonValue;
    }

    @Override
    public JsonNumber put(String url, Float value) {
        JsonNumber jsonValue = createNumber(value);
        put(url, jsonValue);
        return jsonValue;
    }

    @Override
    public JsonNumber put(String url, Integer value) {
        JsonNumber jsonValue = createNumber(value);
        put(url, jsonValue);
        return jsonValue;
    }

    @Override
    public JsonValue put(String url, JsonValue value) {
        return put(Url.create(url), value);
    }

    @Override
    public void put(String url, JsonValue value, PutCallback callback) {
        put(Url.create(url), value, callback);
    }

    @Override
    public JsonString put(String url, Long value) {
        JsonString jsonValue = createString(value);
        put(url, jsonValue);
        return jsonValue;
    }

    @Override
    public JsonString put(String url, String value) {
        JsonString jsonValue = createString(value);
        put(url, jsonValue);
        return jsonValue;
    }

    @Override
    public JsonValue put(Url url, JsonValue value) {
        Url fullUrl = createRootRelativeUrl(url);
        if (isNotMemSchemeAndHasFragment(fullUrl)) { throw ItemscriptError.internalError(this,
                "put.url.with.fragment.not.supported", fullUrl + ""); }
        return ((SyncPutConnector) getConnector(fullUrl)).put(fullUrl, value);
    }

    @Override
    public void put(final Url url, final JsonValue value, final PutCallback callback) {
        Url fullUrl = createRootRelativeUrl(url);
        if (isNotMemSchemeAndHasFragment(fullUrl)) { throw ItemscriptError.internalError(this,
                "put.url.with.fragment.not.supported", fullUrl + ""); }
        ((AsyncPutConnector) getConnector(fullUrl)).put(fullUrl, value, new PutCallback() {
            public void onError(Throwable e) {
                callback.onError(e);
            }

            public void onSuccess(JsonValue value) {
                callback.onSuccess(value);
            }
        });
    }

    private boolean isNotMemSchemeAndHasFragment(final Url url) {
        return !url.scheme()
                .equals(Url.MEM_SCHEME) && url.hasFragment();
    }

    @Override
    public JsonNative putNative(String url, Object nativeValue) {
        JsonNative jsonValue = factory().createNative(nativeValue);
        put(url, jsonValue);
        return jsonValue;
    }

    @Override
    public JsonValue putValue(String url, JsonValue value) {
        return put(url, value);
    }

    @Override
    public void remove(String url) {
        remove(Url.create(url));
    }

    @Override
    public void remove(final String url, final RemoveCallback callback) {
        remove(Url.create(url), callback);
    }

    @Override
    public void remove(Url url) {
        Url fullUrl = createRootRelativeUrl(url);
        if (isNotMemSchemeAndHasFragment(fullUrl)) { throw ItemscriptError.internalError(this,
                "remove.url.with.fragment.not.supported", fullUrl + ""); }
        ((SyncPutConnector) getConnector(fullUrl)).remove(fullUrl);
    }

    @Override
    public void remove(final Url url, final RemoveCallback callback) {
        Url fullUrl = createRootRelativeUrl(url);
        if (isNotMemSchemeAndHasFragment(fullUrl)) { throw ItemscriptError.internalError(this,
                "remove.url.with.fragment.not.supported", fullUrl + ""); }
        ((AsyncPutConnector) getConnector(fullUrl)).remove(fullUrl, callback);
    }

    @Override
    public void removeValue(String url) {
        remove(url);
    }

    @Override
    public String toString() {
        return "[JsonSystem]";
    }

    @Override
    public JsonItem createItem(String sourceUrl, JsonObject meta, JsonValue value) {
        return factory.createItem(sourceUrl, meta, value);
    }
}