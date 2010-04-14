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
class Analyzer {
    private int pos;
    private List<Token> tokens;
    private int end;

    /**
     * Analyze a list of Tokens, returning a list of Elements.
     * 
     * @return
     */
    public List<Element> analyze(List<Token> tokens) {
        this.pos = 0;
        this.end = tokens.size();
        this.tokens = tokens;
        List<Element> elements = new ArrayList<Element>();
        while (pos < end) {
            Token token = nextToken();
            if (token.isText()) {
                elements.add(token);
            } else {
                if (token.isDirective()) {
                    // Should be a segment start...
                    elements.add(analyzeSegment(token.asDirective()));
                } else {
                    // Just a regular tag, add it.
                    elements.add(token);
                }
            }
        }
        return elements;
    }

    private Element analyzeForeach(Directive directive) {
        Foreach foreach = new Foreach(extractFieldTokens(directive));
        // Start with the regular contents.
        List<Element> contents = foreach.contents();
        while (pos < end) {
            Token token = nextToken();
            if (!token.isTag() || !token.isDirective()) {
                // Add regular tokens and tags to the contents of this foreach.
                contents.add(token);
            } else {
                if (token.asDirective()
                        .directiveType()
                        .equals(Template.JOIN_DIRECTIVE)) {
                    // If we encounter .join, start adding elements to the join section.
                    contents = foreach.join();
                } else if (token.asDirective()
                        .directiveType()
                        .equals(Template.END_DIRECTIVE)) {
                    // Encountered .end; finished analyzing this foreach.
                    return foreach;
                } else {
                    // Analyze the nested segment, and then continue.
                    contents.add(analyzeSegment(token.asDirective()));
                }
            }
        }
        throw ItemscriptError.internalError(this, "analyzeForeach.missing.end.directive");
    }

    private List<String> extractFieldTokens(Directive directive) {
        List<String> fieldTokens = new ArrayList<String>();
        for (int i = 1; i < directive.contents()
                .size(); ++i) {
            fieldTokens.add(directive.contents()
                    .get(i));
        }
        return fieldTokens;
    }

    private Element analyzeIf(Directive directive) {
        If ifSegment = new If(extractFieldTokens(directive));
        // Start by adding to the true contents.
        List<Element> contents = ifSegment.trueContents();
        while (pos < end) {
            Token token = nextToken();
            if (!token.isTag() || !token.isDirective()) {
                // Add regular tokens and tags to the contents of this section.
                contents.add(token);
            } else {
                if (token.asDirective()
                        .directiveType()
                        .equals(Template.ELSE_DIRECTIVE)) {
                    // Encountered .else; switch to adding to the falseContents of this section.
                    contents = ifSegment.falseContents();
                    continue;
                } else if (token.asDirective()
                        .directiveType()
                        .equals(Template.END_DIRECTIVE)) {
                    // Encountered .end; finished analyzing this section.
                    return ifSegment;
                } else {
                    // Analyze the nested segment, and then continue.
                    contents.add(analyzeSegment(token.asDirective()));
                }
            }
        }
        throw ItemscriptError.internalError(this, "analyzeIf.missing.end.directive");
    }

    private Element analyzeSection(Directive directive) {
        Section section = new Section(extractFieldTokens(directive));
        // Start by adding to the regular section contents.
        List<Element> contents = section.regularContents();
        while (pos < end) {
            Token token = nextToken();
            if (!token.isTag() || !token.isDirective()) {
                // Add regular tokens and tags to the contents of this section.
                contents.add(token);
            } else {
                if (token.asDirective()
                        .directiveType()
                        .equals(Template.OR_DIRECTIVE)) {
                    // Encountered .or; switch to adding to the orContents of this section.
                    contents = section.orContents();
                    continue;
                } else if (token.asDirective()
                        .directiveType()
                        .equals(Template.END_DIRECTIVE)) {
                    // Encountered .end; finished analyzing this section.
                    return section;
                } else {
                    // Analyze the nested segment, and then continue.
                    contents.add(analyzeSegment(token.asDirective()));
                }
            }
        }
        throw ItemscriptError.internalError(this, "analyzeSection.missing.end.directive");
    }

    private Element analyzeSegment(Directive directive) {
        if (directive.directiveType()
                .equals(Template.SECTION_DIRECTIVE)) {
            return analyzeSection(directive);
        } else if (directive.directiveType()
                .equals(Template.FOREACH_DIRECTIVE)) {
            return analyzeForeach(directive);
        } else if (directive.directiveType()
                .equals(Template.IF_DIRECTIVE)) {
            return analyzeIf(directive);
        } else {
            throw ItemscriptError.internalError(this, "analyzeSegment.encountered.end.outside.segment", directive
                    + "");
        }
    }

    private Token nextToken() {
        Token token = tokens.get(pos);
        ++pos;
        return token;
    }
}