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

package examples.org.itemscript.gwt.server;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.itemscript.core.JsonSystem;
import org.itemscript.core.values.ItemscriptCreator;
import org.itemscript.core.values.JsonValue;
import org.itemscript.standard.MinimalConfig;

public class PostEncapServlet extends HttpServlet {
    //@Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonSystem system = MinimalConfig.createSystem();
        String method = null;
        JsonValue value = null;
        String fragment = null;
        if (req.getContentType()
                .startsWith("application/x-www-form-urlencoded")) {
            method = req.getParameter("method");
            String valueJson = req.getParameter("value");
            if (valueJson != null && valueJson.length() > 0) {
                value = system.parse(valueJson);
            }
            fragment = req.getParameter("fragment");
        } else {
            JsonValue input = system.parseReader(req.getReader());
            if (!input.isObject()) { throw new RuntimeException("input must be an object"); }
        }
        resp.setContentType("application/json");
        if (value != null) {
            Writer writer = resp.getWriter();
            writer.write(ItemscriptCreator.quotedString(method + " " + value + " " + fragment));
        }
    }

    //@Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonSystem system = MinimalConfig.createSystem();
        JsonValue value = system.parseReader(req.getReader());
        resp.setContentType("application/json");
        resp.getWriter()
                .write(value.toCompactJsonString());
    }

    private JsonValue putValue(JsonValue value) {
        JsonValue retValue = value.copy();
        return retValue;
    }

    //@Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonSystem system = MinimalConfig.createSystem();
        resp.setStatus(200);
    }
}