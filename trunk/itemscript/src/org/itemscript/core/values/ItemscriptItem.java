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
 *     * Neither the names of Kalinda Software, DBA Software, Data Base Architects,
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

package org.itemscript.core.values;

import java.util.ArrayList;
import java.util.List;

import org.itemscript.core.JsonSystem;
import org.itemscript.core.Params;
import org.itemscript.core.connectors.GetCallback;
import org.itemscript.core.connectors.PutCallback;
import org.itemscript.core.connectors.RemoveCallback;
import org.itemscript.core.events.Event;
import org.itemscript.core.events.EventType;
import org.itemscript.core.events.Handler;
import org.itemscript.core.exceptions.ItemscriptError;
import org.itemscript.core.url.Fragment;
import org.itemscript.core.url.Url;
import org.itemscript.core.util.JsonAccessHelper;

/**
 * The implementation class for {@link JsonItem}.
 * 
 * @author Jacob Davies<br/><a href="mailto:jacob@itemscript.org">jacob@itemscript.org</a>
 */
public final class ItemscriptItem implements JsonItem {
    private final JsonSystem system;
    private final Url source;
    private JsonValue value;
    private final JsonObject meta;
    private List<Handler> handlers;

    protected ItemscriptItem(JsonSystem system, Url source, JsonValue value) {
        this(system, source, null, value);
    }

    protected ItemscriptItem(JsonSystem system, Url source, JsonObject meta, JsonValue value) {
        this.system = system;
        this.value = value;
        this.source = source;
        this.meta = meta;
        ((ItemscriptValue) value).setItem(this);
    }

    @Override
    public JsonObject meta() {
        return meta;
    }

    @Override
    public void addHandler(Handler handler) {
        if (handlers == null) {
            handlers = new ArrayList<Handler>();
        }
        handlers.add(handler);
    }

    @Override
    public final JsonValue dereference(String key) {
        return JsonAccessHelper.dereference(getValue(key));
    }

    @Override
    public final void dereference(String key, GetCallback callback) {
        JsonAccessHelper.dereference(getValue(key), callback);
    }

    @Override
    public JsonValue get(String url) {
        return get(Url.create(url));
    }

    @Override
    public void get(String url, GetCallback callback) {
        get(Url.create(url), callback);
    }

    JsonValue get(Url url) {
        if (isFragmentOnly(url)) {
            Fragment fragment = url.fragment();
            if (fragment.size() == 0) { return value; }
            return ((ItemscriptContainer) value).getByFragment(fragment);
        } else {
            return system.get(Url.createRelative(source, url));
        }
    }

    void get(Url url, GetCallback callback) {
        if (isFragmentOnly(url)) {
            try {
                callback.onSuccess(get(url));
            } catch (ItemscriptError e) {
                callback.onError(e);
            }
        } else {
            system.get(Url.createRelative(source, url), callback);
        }
    }

    @Override
    public JsonArray getArray(String key) {
        return JsonAccessHelper.asArray(get(key));
    }

    @Override
    public byte[] getBinary(String key) {
        return JsonAccessHelper.asBinary(get(key));
    }

    @Override
    public Boolean getBoolean(String key) {
        return JsonAccessHelper.asBoolean(get(key));
    }

    @Override
    public Double getDouble(String key) {
        return JsonAccessHelper.asDouble(get(key));
    }

    @Override
    public Float getFloat(String key) {
        return JsonAccessHelper.asFloat(get(key));
    }

    @Override
    public Integer getInt(String key) {
        return JsonAccessHelper.asInt(get(key));
    }

    @Override
    public Long getLong(String key) {
        return JsonAccessHelper.asLong(get(key));
    }

    @Override
    public Object getNative(String key) {
        return JsonAccessHelper.asNative(get(key));
    }

    private JsonValue getNext(Url url, JsonValue next, String key) {
        if (next.isContainer()) {
            next = next.asContainer()
                    .getValue(key);
        } else {
            throw new ItemscriptError("error.itemscript.JsonSystem.getNext.next.was.not.a.container",
                    new Params().p("next", next + "")
                            .p("fragment", url.fragmentString())
                            .p("key", key));
        }
        return next;
    }

    @Override
    public JsonObject getObject(String key) {
        return JsonAccessHelper.asObject(get(key));
    }

    @Override
    public String getString(String key) {
        return JsonAccessHelper.asString(get(key));
    }

    @Override
    public JsonValue getValue(String url) {
        return get(url);
    }

    boolean hasHandlers() {
        return handlers != null;
    }

    private boolean isFragmentOnly(Url url) {
        return (url.fragment() != null) && (!url.hasScheme()) && (!url.hasPath()) && (!url.hasQuery());
    }

    void notifyPut(String fragment, JsonValue newValue) {
        if (handlers != null) {
            Event event = new Event(EventType.PUT, fragment, newValue);
            for (int i = 0; i < handlers.size(); ++i) {
                handlers.get(i)
                        .handle(event);
            }
        }
    }

    void notifyRemove(String fragment) {
        if (handlers != null) {
            Event event = new Event(EventType.REMOVE, fragment, null);
            for (int i = 0; i < handlers.size(); ++i) {
                handlers.get(i)
                        .handle(event);
            }
        }
    }

    @Override
    public JsonBoolean put(String url, Boolean value) {
        JsonBoolean jsonValue = system().createBoolean(value);
        putValue(url, jsonValue);
        return jsonValue;
    }

    @Override
    public JsonString put(String url, byte[] value) {
        JsonString jsonValue = system().createString(value);
        putValue(url, jsonValue);
        return jsonValue;
    }

    @Override
    public JsonNumber put(String url, Double value) {
        JsonNumber jsonValue = system().createNumber(value);
        putValue(url, jsonValue);
        return jsonValue;
    }

    @Override
    public JsonNumber put(String url, Float value) {
        JsonNumber jsonValue = system().createNumber(value);
        putValue(url, jsonValue);
        return jsonValue;
    }

    @Override
    public JsonNumber put(String url, Integer value) {
        JsonNumber jsonValue = system().createNumber(value);
        putValue(url, jsonValue);
        return jsonValue;
    }

    @Override
    public JsonValue put(String url, JsonValue value) {
        putValue(Url.create(url), value);
        return value;
    }

    @Override
    public void put(String url, JsonValue value, PutCallback callback) {
        put(Url.create(url), value, callback);
    }

    @Override
    public JsonString put(String url, Long value) {
        JsonString jsonValue = system().createString(value);
        putValue(url, jsonValue);
        return jsonValue;
    }

    @Override
    public JsonString put(String url, String value) {
        JsonString jsonValue = system().createString(value);
        putValue(url, jsonValue);
        return jsonValue;
    }

    JsonValue put(Url url, JsonValue value) {
        putValue(url, value);
        return value;
    }

    void put(Url url, JsonValue value, PutCallback callback) {
        if (isFragmentOnly(url)) {
            // If it's only a fragment, put the value inside the root value of this item and call
            // the callback immediately.
            try {
                put(url, value);
                callback.onSuccess(value);
            } catch (ItemscriptError e) {
                callback.onError(e);
            }
        } else {
            // Otherwise interpret it as relative to the source URL of this item.
            system.put(Url.createRelative(source, url), value, callback);
        }
    }

    @Override
    public JsonNative putNative(String url, Object value) {
        JsonNative jsonValue = system().createNative(value);
        put(url, jsonValue);
        return jsonValue;
    }

    @Override
    public JsonValue putValue(String url, JsonValue value) {
        return putValue(Url.create(url), value);
    }

    JsonValue putValue(Url url, JsonValue value) {
        // First, determine if the URL was just a fragment, or whether it was something to be interpreted as
        // relative to the source of this item.
        if (isFragmentOnly(url)) {
            Fragment fragment = url.fragment();
            if (fragment.size() > 0) {
                // Find (or create) the last container...
                if (!value().isContainer()) { throw ItemscriptError.internalError(this,
                        "putValue.value.was.not.a.container", "#" + url.fragmentString()); }
                JsonContainer container = value().asContainer();
                for (int i = 0; i < (fragment.size() - 1); ++i) {
                    String key = fragment.get(i);
                    JsonValue next = getNext(url, container, key);
                    if (next == null) {
                        next = container.createObject(key);
                    }
                    if (!next.isContainer()) { throw ItemscriptError.internalError(this,
                            "putValue.next.was.not.a.container", key); }
                    container = next.asContainer();
                }
                // Then put the value in that container.
                container.putValue(fragment.lastKey(), value);
            } else {
                // If the fragment was of zero length, replace the root value of this item with the supplied value.
                JsonValue prevValue = value();
                ((ItemscriptValue) prevValue).setItem(null);
                this.value = value;
                ((ItemscriptValue) value).setItem(this);
                notifyPut("#", value);
            }
            return value;
        } else {
            // If the URL was anything other than a fragment, interpret it as being relative to the source of this
            // item and put the value there.
            return system.put(Url.createRelative(source, url), value);
        }
    }

    @Override
    public void remove(String url) {
        remove(Url.create(url));
    }

    @Override
    public void remove(String url, RemoveCallback callback) {
        remove(Url.create(url), callback);
    }

    void remove(Url url) {
        if (isFragmentOnly(url)) {
            // If it's a fragment only, and has at least one key, remove the corresponding value from this item.
            Fragment fragment = url.fragment();
            if (fragment.size() > 0) {
                JsonContainer container = value().asContainer();
                // Find the last container...
                for (int i = 0; i < (fragment.size() - 1); ++i) {
                    String key = fragment.get(i);
                    JsonContainer next = getNext(url, container, key).asContainer();
                    // If the next container is null, just return.
                    if (next == null) { return; }
                    container = next;
                }
                container.removeValue(fragment.lastKey());
            } else {
                // If it was a fragment only but with no keys, remove this item.
                system.remove(source());
            }
        } else {
            // Otherwise treat the URL as relative to the source of this item and call system.remove().
            system.remove(Url.createRelative(source, url));
        }
    }

    void remove(Url url, RemoveCallback callback) {
        // If it's a fragment only, and has at least one key, remove that value from this item.
        if (isFragmentOnly(url)) {
            if (url.fragment()
                    .size() > 0) {
                try {
                    remove(url);
                    callback.onSuccess();
                } catch (ItemscriptError e) {
                    callback.onError(e);
                }
                return;
            }
        }
        // Otherwise treat the URL as relative to the source of this item and call system.remove().
        system.remove(Url.createRelative(source, url), callback);
    }

    @Override
    public void removeValue(String url) {
        remove(url);
    }

    @Override
    public String source() {
        return source + "";
    }

    @Override
    public JsonSystem system() {
        return system;
    }

    @Override
    public String toString() {
        return "[ItemscriptItem sourceUrl=" + source + " value=" + value.toCompactJsonString() + "]";
    }

    @Override
    public JsonValue value() {
        return value;
    }
}