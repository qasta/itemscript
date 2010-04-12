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

package org.itemscript.core.gwt;

import org.itemscript.core.JsonSystem;
import org.itemscript.core.config.JsonConfig;
import org.itemscript.core.util.Base64;
import org.itemscript.core.values.JsonCreator;

/**
 * A minimal JsonConfig implementation for the GWT environment. This configuration does not allow access to
 * connectors except the MemConnector.
 * 
 * @author Jacob Davies<br/><a href="mailto:jacob@itemscript.org">jacob@itemscript.org</a>
 *
 */
public class MinimalGwtConfig implements JsonConfig {
    private static final String ITEMSCRIPT_CONNECTORS_PREFIX = "/itemscript/connectors#";
    private final static String integerToHex = "0123456789abcdef";

    @Override
    public JsonCreator createJsonCreator(JsonSystem system) {
        return new GwtJsonCreator(system);
    }

    @Override
    public String generateB64id() {
        byte[] s = new byte[16];
        for (int i = 0; i < 16; ++i) {
            s[i] = (byte) (Math.abs(nextRandomInt()) % 256);
        }
        return new String(Base64.encodeForUrl(s));
    }

    @Override
    public String generateUuid() {
        /* generateUuid() derived from:
         * randomUUID.js - Version 1.0
        * Copyright 2008, Robert Kieffer
        * This software is made available under the terms of the Open Software License
        * v3.0 (available here: http://www.opensource.org/licenses/osl-3.0.php )
        * The latest version of this file can be found at:
        * http://www.broofa.com/Tools/randomUUID.js
        * For more information, or to comment on this, please go to:
        * http://www.broofa.com/blog/?p=151
        */
        char[] s = new char[36];
        // Make array of random hex digits. The UUID only has 32 digits in it, but we
        // allocate an extra items to make room for the '-'s we'll be inserting.
        for (int i = 0; i < 36; ++i) {
            char c = (char) (Math.abs(nextRandomInt()) % 0x10);
            s[i] = c;
        }
        ;
        // Conform to RFC-4122, section 4.4
        s[14] = 4; // Set 4 high bits of time_high field to version
        s[19] = (char) ((s[19] & 0x3) | 0x8); // Specify 2 high bits of clock sequence
        // Convert to hex chars
        for (int i = 0; i < 36; ++i) {
            s[i] = integerToHex.charAt(s[i]);
        }
        // Insert '-'s
        s[8] = s[13] = s[18] = s[23] = '-';
        return new String(s);
    }

    @Override
    public int nextRandomInt() {
        return com.google.gwt.user.client.Random.nextInt();
    }

    @Override
    public void seedSystem(JsonSystem system) {}
}