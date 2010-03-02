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

import org.itemscript.core.HasSystem;
import org.itemscript.core.JsonSystem;
import org.itemscript.core.Params;
import org.itemscript.core.exceptions.ItemscriptError;
import org.itemscript.core.values.JsonObject;

/**
 * A Foundry is a class that facilitates the creation of instance objects.
 * 
 * It contains a map of {@link JsonFactory} objects. The right factory is found
 * for a {@link JsonObject}, and that factory is used to create a Java object. 
 * 
 * @author Jacob Davies<br/><a href="mailto:jacob@itemscript.org">jacob@itemscript.org</a>
 * 
 * @param <T> The supertype that this foundry will create.
 */
public abstract class ItemscriptFoundry<T> implements JsonFactory<T>, HasSystem, JsonFoundry<T> {
    private JsonSystem system;
    private final String location;
    private final JsonObject factoryObject;

    /**
     * Subclasses must call this constructor.
     * 
     * @param location The mem:/ location that factories in this foundry will be stored under.
     */
    protected ItemscriptFoundry(JsonSystem system, String location) {
        if (system == null) {
            this.location = null;
            this.factoryObject = null;
            return;
        }
        this.system = system;
        this.location = location;
        factoryObject = system.createObject();
        system.put(location, factoryObject);
    }

    @Override
    public T create(JsonObject params) {
        return create(params.get(getFactoryNameParameter())
                .stringValue(), params);
    }

    @Override
    public T create(String name) {
        return create(name, system().createObject());
    }

    @Override
    public T create(String name, JsonObject params) {
        if (params == null) { throw new ItemscriptError("error.foundry.create.params.was.null"); }
        if (name == null) { throw new ItemscriptError("error.itemscript.Foundry.create.factoryName.was.null",
                new Params().p("factoryNameParameter", getFactoryNameParameter())); }
        JsonFactory<T> factory = get(name);
        if (factory == null) { throw new ItemscriptError("error.foundry.create.factory.not.found", new Params().p(
                "name", name)); }
        return factory.create(params);
    }

    @SuppressWarnings("unchecked")
    private final JsonFactory<T> get(String name) {
        return (JsonFactory<T>) factoryObject.get(name)
                .nativeValue();
    }

    @Override
    public abstract String getFactoryNameParameter();

    @Override
    public final void put(final FactoryName<T> factoryName) {
        final String name = factoryName.getName();
        put(name, factoryName);
    }

    @Override
    public final void put(String name, final JsonFactory<T> factory) {
        if (name == null || name.length() == 0) { throw new IllegalArgumentException(
                "error.itemscript.foundry.empty.name.in.put"); }
        factoryObject.putNative(name, factory);
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