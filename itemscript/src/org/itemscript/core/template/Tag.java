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

package org.itemscript.core.template;

import java.util.ArrayList;
import java.util.List;

import org.itemscript.core.exceptions.ItemscriptError;

/**
 * @author Jacob Davies<br/><a href="mailto:jacob@itemscript.org">jacob@itemscript.org</a>
 */
class Tag extends Token {
    public static Token tag(String contents, int beginIndex, int endIndex, int line, int column) {
        if (contents.startsWith(Template.DIRECTIVE_CHAR + "")) {
            return new Directive(contents, beginIndex, endIndex, line, column);
        } else {
            return new Tag(contents, beginIndex, endIndex, line, column);
        }
    }

    private final List<String> contents;

    protected Tag(String contentsString, int beginIndex, int endIndex, int line, int column) {
        super(beginIndex, endIndex, line, column);
        if (contentsString.indexOf("\n") != -1) {
            // If it contains an embedded carriage return, it's an error.
            throw ItemscriptError.internalError(this, "constructor.contentsString.contained.carriage.return",
                    contentsString);
        }
        this.contents = new ArrayList<String>();
        String trimmedContents = contentsString.trim();
        int pos = 0;
        while (pos < trimmedContents.length()) {
            int firstSpace = trimmedContents.indexOf(' ', pos);
            if (firstSpace == -1) {
                // No more spaces, append the remainder.
                contents.add(trimmedContents.substring(pos));
                break;
            } else {
                // FIXME multiple spaces handling here.
                contents.add(trimmedContents.substring(pos, firstSpace));
                pos = firstSpace + 1;
            }
        }
    }

    @Override
    public Tag asTag() {
        return this;
    }

    public List<String> contents() {
        return contents;
    }

    @Override
    public boolean isTag() {
        return true;
    }

    @Override
    public String toString() {
        return "[Tag contents=" + contents + "]";
    }
}