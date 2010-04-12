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

import org.itemscript.core.Params;
import org.itemscript.core.exceptions.ItemscriptError;

/**
 * @author Jacob Davies<br/><a href="mailto:jacob@itemscript.org">jacob@itemscript.org</a>
 */
class Scanner {
    public List<Token> scan(String template) {
        List<Token> tokens = new ArrayList<Token>();
        int pos = 0;
        if (template == null) { throw ItemscriptError.internalError(this, "tokenize.template.was.null"); }
        while (pos < template.length()) {
            int openBrace = template.indexOf(Template.OPEN_TAG_CHAR, pos);
            int nextOpenBrace = template.indexOf(Template.OPEN_TAG_CHAR, openBrace + 1);
            int closeBrace = template.indexOf(Template.CLOSE_TAG_CHAR, pos);
            if (nextOpenBrace != -1 && closeBrace != -1 && nextOpenBrace < closeBrace) { throw ItemscriptError.internalError(
                    this, "tokenize.overlapping.open.braces", template.substring(openBrace, closeBrace)); }
            // If no more open braces found, the rest of the template is a text token.
            if (openBrace == -1) {
                String remainder = template.substring(pos);
                tokens.add(new Text(remainder, pos, template.length(), 0, 0));
                break;
            }
            if (closeBrace == -1) { throw new ItemscriptError(
                    "error.itemscript.Template.textTemplate.unbalanced.brace", new Params().p("openBraceIndex",
                            openBrace)); }
            if (pos < openBrace) {
                // Add text preceding the tag.
                tokens.add(new Text(template.substring(pos, openBrace), pos, openBrace - 1, 0, 0));
            }
            // Add a tag token 
            tokens.add(Tag.tag(template.substring(openBrace + 1, closeBrace)
                    .trim(), (openBrace + 1), (closeBrace - 1), 0, 0));
            pos = closeBrace + 1;
        }
        return tokens;
    }
}