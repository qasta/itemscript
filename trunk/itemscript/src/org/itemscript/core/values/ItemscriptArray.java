/*
 * Copyright � 2010, Data Base Architects, Inc. All rights reserved.
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
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.itemscript.core.JsonSystem;
import org.itemscript.core.exceptions.ItemscriptError;
import org.itemscript.core.util.JsonAccessHelper;

final class ItemscriptArray extends ItemscriptContainer implements JsonArray {
    private final ArrayList<JsonValue> values = new ArrayList<JsonValue>();

    protected ItemscriptArray(JsonSystem system) {
        super(system);
    }

    protected ItemscriptArray(JsonSystem system, List<JsonValue> values) {
        super(system);
        values.addAll(values);
    }

    @Override
    public JsonArray a(Boolean value) {
        add(value);
        return this;
    }

    @Override
    public JsonArray a(byte[] value) {
        add(value);
        return this;
    }

    @Override
    public JsonArray a(Double value) {
        add(value);
        return this;
    }

    @Override
    public JsonArray a(Float value) {
        add(value);
        return this;
    }

    @Override
    public JsonArray a(Integer value) {
        add(value);
        return this;
    }

    @Override
    public JsonArray a(JsonValue value) {
        add(value);
        return this;
    }

    @Override
    public JsonArray a(Long value) {
        add(value);
        return this;
    }

    @Override
    public JsonArray a(String value) {
        add(value);
        return this;
    }

    @Override
    public void add(Boolean value) {
        add(system().createBoolean(value));
    }

    @Override
    public void add(byte[] value) {
        add(system().createString(value));
    }

    @Override
    public void add(Double value) {
        add(system().createNumber(value));
    }

    @Override
    public void add(Float value) {
        add(system().createNumber(value));
    }

    @Override
    public void add(int index, JsonValue value) {
        prepareValueForPut(index + "", value);
        values.add(index, value);
        renumberEntriesFrom(index);
    }

    @Override
    public void add(Integer value) {
        add(system().createNumber(value));
    }

    @Override
    public boolean add(JsonValue value) {
        if (value == null) {
            value = system().createNull();
        }
        if (value.system() != system()) { throw ItemscriptError.internalError(this, "add.system.mismatch"); }
        int index = values.size();
        boolean ret = values.add(value);
        prepareValueForPut(index + "", value);
        if (item() != null) {
            ((ItemscriptItem) item()).notifyPut(value.fragment(), value);
        }
        return ret;
    }

    @Override
    public void add(Long value) {
        add(system().createString(value));
    }

    @Override
    public void add(String value) {
        add(system().createString(value));
    }

    @Override
    public boolean addAll(Collection<? extends JsonValue> c) {
        for (JsonValue value : c) {
            add(value);
        }
        return c.size() > 0;
    }

    @Override
    public boolean addAll(int index, Collection<? extends JsonValue> c) {
        throw new UnsupportedOperationException("error.itemscript.ItemscriptArray.addAll.index.not.supported");
    }

    @Override
    public JsonArray asArray() {
        return this;
    }

    private int checkIndex(String key) {
        try {
            return Integer.parseInt(key);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("error.itemscript.ItemscriptArray.checkIndex.non.integer.key", e);
        }
    }

    @Override
    public void clear() {
        values.clear();
    }

    @Override
    public boolean contains(Object o) {
        return values.contains(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return values.containsAll(c);
    }

    @Override
    public boolean containsKey(String key) {
        int index;
        try {
            index = Integer.parseInt(key);
        } catch (NumberFormatException e) {
            return false;
        }
        return index < size();
    }

    @Override
    public JsonArray createArray(int index) {
        return createArray(index + "");
    }

    @Override
    public JsonObject createObject(int index) {
        return createObject(index + "");
    }

    @Override
    public JsonArray copy() {
        JsonArray newArray = system().createArray();
        for (int i = 0; i < size(); ++i) {
            JsonValue value = get(i);
            JsonValue newValue = value.copy();
            newArray.set(i, newValue);
        }
        return newArray;
    }

    private void enlargeValues(int index) {
        while (index >= size()) {
            add(system().createNull());
        }
    }

    @Override
    public JsonValue get(int index) {
        return values.get(index);
    }

    @Override
    public JsonArray getArray(int index) {
        return JsonAccessHelper.asArray(get(index));
    }

    @Override
    public Boolean getBoolean(int index) {
        return JsonAccessHelper.asBoolean(get(index));
    }

    @Override
    public Double getDouble(int index) {
        return JsonAccessHelper.asDouble(get(index));
    }

    @Override
    public Float getFloat(int index) {
        return JsonAccessHelper.asFloat(get(index));
    }

    @Override
    public Integer getInt(int index) {
        return JsonAccessHelper.asInt(get(index));
    }

    @Override
    public Long getLong(int index) {
        return JsonAccessHelper.asLong(get(index));
    }

    @Override
    public JsonObject getObject(int index) {
        return JsonAccessHelper.asObject(get(index));
    }

    @Override
    public String getString(int index) {
        return JsonAccessHelper.asString(get(index));
    }

    @Override
    public JsonValue getValue(String key) {
        try {
            int index = Integer.valueOf(key);
            return get(index);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public int hashCode() {
        return values.hashCode();
    }

    @Override
    public int indexOf(Object o) {
        return values.indexOf(o);
    }

    @Override
    public boolean isArray() {
        return true;
    }

    @Override
    public boolean isEmpty() {
        return values.isEmpty();
    }

    @Override
    public Iterator<JsonValue> iterator() {
        return values.iterator();
    }

    @Override
    public Set<String> keySet() {
        HashSet<String> keys = new HashSet<String>();
        for (int i = 0; i < size(); ++i) {
            keys.add(i + "");
        }
        return keys;
    }

    @Override
    public int lastIndexOf(Object o) {
        return values.lastIndexOf(o);
    }

    @Override
    public ListIterator<JsonValue> listIterator() {
        return values.listIterator();
    }

    @Override
    public ListIterator<JsonValue> listIterator(int index) {
        return values.listIterator(index);
    }

    @Override
    public JsonValue putValue(String key, JsonValue value) {
        set(Integer.valueOf(key), value);
        return value;
    }

    @Override
    public JsonValue remove(int index) {
        String fragment = null;
        if (item() != null) {
            JsonValue value = get(index);
            if (value != null) {
                fragment = value.fragment();
            }
        }
        JsonValue ret = values.remove(index);
        updateRemovedValue(ret);
        if (item() != null && fragment != null) {
            ((ItemscriptItem) item()).notifyRemove(fragment);
        }
        renumberEntriesFrom(index);
        return ret;
    }

    /**
     * remove(Object o) is not supported on ItemscriptArrays.
     */
    @Override
    public boolean remove(Object o) {
        // FIXME this should be supported. Remember to call notifyDelete on the value and notifyUpdate on this value,
        // and re-index after the value is removed.
        throw new UnsupportedOperationException("error.itemscript.ItemscriptArray.remove.Object.not.supported");
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("error.itemscript.ItemscriptArray.removeAll.not.supported");
    }

    @Override
    public void removeValue(String key) {
        remove(checkIndex(key));
    }

    private void renumberEntriesFrom(int index) {
        for (int i = index; i < size(); ++i) {
            ItemscriptValue value = (ItemscriptValue) get(i);
            value.setKey(null);
            value.setKey(i + "");
        }
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("error.itemscript.ItemscriptArray.retainAll.not.supported");
    }

    @Override
    public void set(int index, Boolean value) {
        set(index, system().createBoolean(value));
    }

    @Override
    public void set(int index, byte[] value) {
        set(index, system().createString(value));
    }

    @Override
    public void set(int index, Double value) {
        set(index, system().createNumber(value));
    }

    @Override
    public void set(int index, Float value) {
        set(index, system().createNumber(value));
    }

    @Override
    public void set(int index, Integer value) {
        set(index, system().createNumber(value));
    }

    @Override
    public JsonValue set(int index, JsonValue value) {
        if (value == null) {
            value = system().createNull();
        }
        if (value.system() != system()) { throw ItemscriptError.internalError(this, "set.system.mismatch", index
                + ""); }
        enlargeValues(index);
        prepareValueForPut(index + "", value);
        JsonValue previous = values.set(index, value);
        updateRemovedValue(previous);
        if (item() != null) {
            ((ItemscriptItem) item()).notifyPut(value.fragment(), value);
        }
        return previous;
    }

    @Override
    public void set(int index, Long value) {
        set(index, system().createString(value));
    }

    @Override
    public void set(int index, String value) {
        set(index, system().createString(value));
    }

    @Override
    public int size() {
        return values.size();
    }

    @Override
    public List<JsonValue> subList(int fromIndex, int toIndex) {
        // We cannot support this because GWT's ArrayList does not support it.
        throw new UnsupportedOperationException("error.itemscript.ItemscriptArray.subList.not.supported");
    }

    @Override
    public Object[] toArray() {
        return values.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return values.toArray(a);
    }

    @Override
    public String toCompactJsonString() {
        if (size() == 0) { return "[]"; }
        StringBuffer sb = new StringBuffer("[");
        for (int i = 0; i < values.size(); ++i) {
            JsonValue value = values.get(i);
            if (value.isContainer()) {
                sb.append(value.asContainer()
                        .toCompactJsonString());
            } else {
                sb.append(value.toCompactJsonString());
            }
            if (i + 1 != size()) {
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public String toJsonString() {
        return toJsonString(0) + "\n";
    }

    @Override
    public String toJsonString(int indent) {
        if (size() == 0) { return "[]"; }
        StringBuffer sb = new StringBuffer("[");
        sb.append("\n");
        for (int i = 0; i < values.size(); ++i) {
            JsonValue value = values.get(i);
            sb.append(indent(indent + 1));
            if (value.isContainer()) {
                sb.append(((ItemscriptContainer) value).toJsonString(indent + 1));
            } else {
                sb.append(value.toJsonString());
            }
            if (i + 1 != size()) {
                sb.append(",");
            }
            sb.append("\n");
        }
        sb.append(indent(indent) + "]");
        return sb.toString();
    }
}