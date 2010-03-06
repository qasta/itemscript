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

package org.itemscript.core.connectors;

import java.util.Arrays;

import org.itemscript.core.JsonSystem;
import org.itemscript.core.exceptions.ItemscriptError;
import org.itemscript.core.url.Pagination;
import org.itemscript.core.url.Path;
import org.itemscript.core.url.Query;
import org.itemscript.core.url.Url;
import org.itemscript.core.values.JsonArray;
import org.itemscript.core.values.JsonNumber;
import org.itemscript.core.values.JsonObject;
import org.itemscript.core.values.JsonValue;

/**
 * Implements the in-memory item store.
 * 
 * @author Jacob Davies<br/><a href="mailto:jacob@itemscript.org">jacob@itemscript.org</a>
 *
 */
public final class MemConnector extends ConnectorBase
        implements
            SyncGetConnector,
            SyncPutConnector,
            SyncBrowseConnector,
            SyncDumpConnector,
            SyncLoadConnector,
            SyncPostConnector {
    /**
     * Create the MemConnector for an {@link JsonSystem}.
     * 
     * This initializes the system and returns the JsonObject from the location
     * "mem:/itemscript/connectors" that is used to store the {@link Connector} objects for
     * the system.
     * 
     * @param system The associated JsonSystem.
     * @return The JsonObject from the location "mem:/itemscript/connectors".
     */
    public static JsonObject create(JsonSystem system) {
        MemConnector connector = new MemConnector(system);
        connector.root.put("itemscript", new ItemNode(system.createItem("mem:/itemscript", system.createNull())));
        JsonObject connectorsObject = system.createObject();
        connector.root.get("itemscript")
                .put("connectors", new ItemNode(system.createItem("mem:/itemscript/connectors", connectorsObject)));
        connectorsObject.putNative("mem", connector);
        return connectorsObject;
    }

    private final ItemNode root;

    private MemConnector(JsonSystem system) {
        super(system);
        root = new ItemNode(system().createItem(JsonSystem.ROOT_URL_STRING, system.createNull()));
    }

    @Override
    public JsonValue countItems(Url url) {
        ItemNode node = findNode(url);
        if (node == null) { return null; }
        JsonNumber count = system().createNumber(node.size());
        system().createItem(url + "", count);
        return count;
    }

    private ItemNode findNode(Url url) {
        ItemNode node = root;
        // We start at 1 because the first element of any path is always "/".
        for (int i = 1; i < url.path()
                .size(); ++i) {
            String key = url.path()
                    .get(i);
            node = node.get(key);
            if (node == null) { return null; }
        }
        return node;
    }

    @Override
    public JsonValue get(Url url) {
        ItemNode node = findNode(url);
        if (node == null) { return null; }
        return node.item()
                .value();
    }

    @Override
    public JsonValue getKeys(Url url) {
        ItemNode node = findNode(url);
        if (node == null) { return null; }
        JsonArray keys = system().createArray();
        for (String key : node.keySet()) {
            keys.add(key);
        }
        system().createItem(url + "", keys);
        return keys;
    }

    @Override
    public JsonValue pagedItems(Url url) {
        ItemNode node = findNode(url);
        if (node == null) { return null; }
        String[] keyArray = sortedKeys(node);
        Pagination pagination = url.query()
                .pagination();
        int beginIndex = 0;
        int endIndex = beginIndex + SyncBrowseConnector.DEFAULT_NUM_ROWS - 1;
        if (pagination.startRow() != -1) {
            beginIndex = pagination.startRow();
        }
        if (pagination.numRows() != -1) {
            endIndex = beginIndex + pagination.numRows() - 1;
        }
        if (endIndex >= keyArray.length) {
            endIndex = keyArray.length - 1;
        }
        JsonArray pagedItems = system().createArray();
        for (int i = beginIndex; i <= endIndex; ++i) {
            String key = keyArray[i];
            pagedItems.add(system().createArray()
                    .a(key)
                    .a(node.get(key)
                            .item()
                            .value()
                            .copy()));
        }
        system().createItem(url + "", pagedItems);
        return pagedItems;
    }

    @Override
    public JsonValue pagedKeys(Url url) {
        ItemNode node = findNode(url);
        if (node == null) { return null; }
        String[] keyArray = sortedKeys(node);
        Pagination pagination = url.query()
                .pagination();
        int beginIndex = 0;
        int endIndex = beginIndex + SyncBrowseConnector.DEFAULT_NUM_ROWS - 1;
        if (pagination.startRow() != -1) {
            beginIndex = pagination.startRow();
        }
        if (pagination.numRows() != -1) {
            endIndex = beginIndex + pagination.numRows() - 1;
        }
        if (endIndex >= keyArray.length) {
            endIndex = keyArray.length - 1;
        }
        JsonArray pagedKeys = system().createArray();
        for (int i = beginIndex; i <= endIndex; ++i) {
            pagedKeys.add(keyArray[i]);
        }
        system().createItem(url + "", pagedKeys);
        return pagedKeys;
    }

    @Override
    public JsonValue put(Url url, JsonValue value) {
        ItemNode node = root;
        Path path = url.path();
        if (path.size() <= 1) { throw ItemscriptError.internalError(this, "put.cannot.put.to.root.node"); }
        String nodeUrl = "mem:";
        // Starting at path index 1 to skip the initial "/" element...
        for (int i = 1; i < (path.size()); ++i) {
            String key = path.get(i);
            nodeUrl += "/" + Url.encode(key);
            ItemNode next = node.get(key);
            if (next == null) {
                next = new ItemNode(system().createItem(nodeUrl, system().createObject()));
                node.put(key, next);
            }
            node = next;
        }
        String fragmentString = url.fragmentString();
        if (fragmentString == null) {
            fragmentString = "";
        }
        node.item()
                .put("#" + fragmentString, value);
        return value;
    }

    @Override
    public void remove(Url url) {
        ItemNode node = root;
        Path path = url.path();
        // Path must have at least one component as well as the root component...
        if (path.size() <= 1) { throw ItemscriptError.internalError(this, "remove.path.not.long.enough",
                url.pathString()); }
        for (int i = 1; i < (path.size() - 1); ++i) {
            String key = path.get(i);
            node = node.get(key);
            if (node == null) { return; }
        }
        if (url.hasFragment()) {
            node = node.get(path.lastKey());
            if (node != null) {
                node.item()
                        .remove("#" + url.fragmentString());
            }
        } else {
            node.remove(path.lastKey());
        }
    }

    private String[] sortedKeys(ItemNode node) {
        String[] keyArray = new String[1];
        keyArray = node.keySet()
                .toArray(keyArray);
        Arrays.sort(keyArray);
        return keyArray;
    }

    @Override
    public JsonObject dump(Url url) {
        ItemNode node = findNode(url);
        if (node == null) { return null; }
        return dumpNode(node);
    }

    private JsonObject dumpNode(ItemNode node) {
        JsonObject dump = system().createObject();
        dump.put("value", node.item()
                .value()
                .copy());
        JsonObject subItems = system().createObject();
        dump.put("subItems", subItems);
        for (String key : node.keySet()) {
            subItems.put(key, dumpNode(node.get(key)));
        }
        return dump;
    }

    @Override
    public void load(Url url, JsonObject value) {
        if (value.size() == 0) { return; }
        Url pathedUrl = Url.createRelative(JsonSystem.ROOT_URL_STRING, url.pathString());
        put(pathedUrl, value.get("value"));
        JsonObject subItems = value.getObject("subItems");
        if (subItems == null) { return; }
        for (String key : subItems.keySet()) {
            String subUrl = pathedUrl + "/" + Url.encode(key);
            load(Url.create(subUrl), subItems.getObject(key));
        }
    }

    @Override
    public JsonValue post(Url url, JsonValue value) {
        ItemNode node = root;
        Path path = url.path();
        String nodeUrl = "mem:";
        // Starting at path index 1 to skip the initial "/" element...
        for (int i = 1; i < (path.size()); ++i) {
            String key = path.get(i);
            nodeUrl += "/" + Url.encode(key);
            ItemNode next = node.get(key);
            if (next == null) {
                next = new ItemNode(system().createItem(nodeUrl, system().createObject()));
                node.put(key, next);
            }
            node = next;
        }
        Query query = url.query();
        if (query.containsKey("uuid")) {
            String uuid = system().generateUuid();
            ItemNode next = new ItemNode(system().createItem(nodeUrl + "/" + uuid, system().createObject()));
            node.put(uuid, next);
            node = next;
        } else {
            throw ItemscriptError.internalError(this, "post.has.unknown.query.type", url.queryString());
        }
        String fragmentString = url.fragmentString();
        if (fragmentString == null) {
            fragmentString = "";
        }
        System.err.println("value: " + value);
        node.item()
                .put("#" + fragmentString, value);
        return value;
    }
}