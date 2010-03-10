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

package org.itemscript.standard;

import java.io.File;

import org.itemscript.core.JsonSystem;
import org.itemscript.core.Params;
import org.itemscript.core.connectors.ConnectorBase;
import org.itemscript.core.connectors.SyncBrowseConnector;
import org.itemscript.core.connectors.SyncGetConnector;
import org.itemscript.core.exceptions.ItemscriptError;
import org.itemscript.core.url.Url;
import org.itemscript.core.values.JsonArray;
import org.itemscript.core.values.JsonObject;

/**
 * Base Connector class for file connectors.
 * <p>
 * Note: At present support for file connectors is fairly minimal; loading of file resources on
 * other servers is not supported, nor are pagedItems or pagedKeys methods from SyncBrowseConnector,
 * nor is SyncPutConnector.
 * 
 * @author Jacob Davies<br/><a href="mailto:jacob@itemscript.org">jacob@itemscript.org</a>
 */
public abstract class FileConnectorBase extends ConnectorBase implements SyncGetConnector, SyncBrowseConnector {
    public FileConnectorBase(JsonSystem system) {
        super(system);
    }

    @Override
    public final JsonObject countItems(Url url) {
        return countObject(getKeys(url).asArray()
                .size());
    }

    private File getDirectory(Url url) {
        File file = getFile(url);
        if (!file.isDirectory()) { throw new ItemscriptError(
                "error.itemscript.FileConnectorBase.getKeys.file.was.not.a.directory", url + ""); }
        return file;
    }

    /**
     * Get a File object for a given URL.
     * 
     * @param url The URL to get.
     * @return A File object.
     */
    protected final File getFile(Url url) {
        File file = new File(url.pathString());
        long length = file.length();
        if (length > Integer.MAX_VALUE) { throw ItemscriptError.internalError(this, "get.file.too.large",
                new Params().p("url", url + "")
                        .p("size", length + "")
                        .p("maxSize", Integer.MAX_VALUE + "")); }
        return file;
    }

    public final JsonArray getKeys(Url url) {
        File file = getDirectory(url);
        JsonArray keys = system().createArray();
        for (String filename : file.list()) {
            keys.add(filename);
        }
        return system().createItem(url + "", keys)
                .value()
                .asArray();
    }

    public final JsonArray pagedItems(Url url) {
        // FIXME
        return null;
    }

    public final JsonArray pagedKeys(Url url) {
        // FIXME
        return null;
    }
}