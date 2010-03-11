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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.itemscript.core.JsonSystem;
import org.itemscript.core.exceptions.ItemscriptError;
import org.itemscript.core.url.Url;
import org.itemscript.core.values.JsonArray;
import org.itemscript.core.values.JsonValue;

/**
 * Connector for reading text files in the standard-Java environment.
 * <p>
 * The returned value will be a JsonArray, where each line in the file corresponds to a JsonString entry in the
 * array, with line endings removed.
 * <p>
 * TODO: This needs to be rolled into the single file connector. 
 * 
 * @author Jacob Davies<br/><a href="mailto:jacob@itemscript.org">jacob@itemscript.org</a>
 */
@Deprecated
public final class TextFileConnector extends FileConnectorBase {
    /**
     * Create a new TextFileConnector.
     * 
     * @param system The associated JsonSystem.
     */
    public TextFileConnector(JsonSystem system) {
        super(system);
    }

    @Override
    public JsonValue get(Url url) {
        File file = getFile(url);
        if (!file.exists()) { throw new ItemscriptError(
                "error.itemscript.TextFileConnector.get.file.does.not.exist", url.pathString()); }
        if (file.isDirectory()) {
            throw ItemscriptError.internalError(this, "get.was.directory", file + "");
        } else {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                JsonArray array = system().createArray();
                while (true) {
                    String line = reader.readLine();
                    if (line == null) {
                        break;
                    }
                    array.add(line);
                }
                reader.close();
                return system().createItem(url + "", array)
                        .value();
            } catch (IOException e) {
                throw new ItemscriptError("error.itemscript.TextFileConnector.get.IOException", e);
            }
        }
    }
}