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

package org.itemscript.core.foundries;

import java.util.ArrayList;
import java.util.List;

import org.itemscript.core.HasSystem;
import org.itemscript.core.JsonSystem;
import org.itemscript.core.Params;
import org.itemscript.core.exceptions.ItemscriptError;
import org.itemscript.core.values.ChainObject;
import org.itemscript.core.values.JsonObject;
import org.itemscript.core.values.JsonValue;

/**
 * The implementation class for {@link JsonFoundry}. You can either instantiate a typed instance
 * of this class, or subclass it. 
 * 
 * @author Jacob Davies<br/><a href="mailto:jacob@itemscript.org">jacob@itemscript.org</a>
 * 
 * @param <T> The supertype that this foundry will create.
 */
public class ItemscriptFoundry<T> implements HasSystem, JsonFoundry<T> {
    private JsonSystem system;
    private final String location;
    private final JsonObject factoryObject;
    private final String nameKey;

    /**
     * Subclasses must call this constructor.
     * 
     * @param system The associated JsonSystem.
     * @param location The mem:/ location that factories in this foundry will be stored under.
     * @param nameKey The key in the objects supplied to {@link #create(JsonValue)} that contains the name of the factory.
     */
    public ItemscriptFoundry(JsonSystem system, String location, String nameKey) {
        this.system = system;
        this.location = location;
        this.nameKey = nameKey;
        factoryObject = system.createObject();
        system.put(location, factoryObject);
    }

    private void checkName(String name) {
        if (name == null || name.length() == 0) { throw ItemscriptError.internalError(this,
                "checkName.empty.name.in.put"); }
    }

    @Override
    public T create(JsonValue params) {
        if (params.isString()) { return create(params.stringValue()); }
        if (params.isObject()) {
            JsonObject p = params.asObject();
            String name = p.getString(nameKey);
            if (name == null) {
                name = findMissingName(p);
            }
            return create(name, p);
        }
        throw ItemscriptError.internalError(this, "create.params.must.be.JsonString.or.JsonObject",
                params.toCompactJsonString());
    }

    @Override
    public T create(String name) {
        return create(name, system().createObject());
    }

    @SuppressWarnings("unchecked")
    @Override
    public T create(String name, JsonObject params) {
        if (params == null) { throw new ItemscriptError(
                "error.itemscript.ItemscriptFoundry.create.params.was.null"); }
        if (name == null) {
            name = findMissingName(params);
            if (name == null) { throw ItemscriptError.internalError(this,
                    "create.no.name.supplied.and.no.name.could.be.found", params.toCompactJsonString()); }
            return create(name, params);
        }
        JsonValue factoryValue = factoryObject.get(name);
        if (factoryValue != null) {
            if (factoryValue.isNative()) {
                Object nativeValue = factoryValue.nativeValue();
                JsonFactory<T> factory = (JsonFactory<T>) nativeValue;
                return factory.create(params);
            } else if (factoryValue.isObject()) {
                JsonObject factoryObject = factoryValue.asObject();
                String underlyingName = factoryObject.getString(nameKey);
                if (underlyingName == null) {
                    underlyingName = findMissingName(factoryObject);
                }
                List<JsonObject> objects = new ArrayList<JsonObject>();
                objects.add(factoryObject);
                objects.add(params);
                return create(underlyingName, new ChainObject(system(), objects));
            }
        }
        throw ItemscriptError.internalError(this, "create.factory.not.found", new Params().p("name", name));
    }

    public String findMissingName(JsonObject params) {
        return null;
    }

    @Override
    public final void put(final FactoryName<T> factoryName) {
        final String name = factoryName.getName();
        put(name, factoryName);
    }

    @Override
    public final void put(String name, final JsonFactory<T> factory) {
        checkName(name);
        factoryObject.putNative(name, factory);
    }

    @Override
    public final void put(String name, JsonObject params) {
        checkName(name);
        factoryObject.put(name, params.copy());
    }

    @Override
    public JsonSystem system() {
        return system;
    }

    @Override
    public final String toString() {
        return "[Foundry location=" + location + "]";
    }
}