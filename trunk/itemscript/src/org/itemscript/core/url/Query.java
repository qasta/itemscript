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

package org.itemscript.core.url;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A representation of the query section of a URL.<br/>
 * <br/>
 * No changes should be made to values in this Map except by the scheme parser that produced it. The behavior is undefined
 * if changes are made.
 * 
 * @author Jacob Davies<br/><a href="mailto:jacob@itemscript.org">jacob@itemscript.org</a>
 */
public final class Query extends HashMap<String, List<String>> implements Map<String, List<String>> {
    /**
     * The key used to indicate the starting row for paged queries.
     */
    public static final String START_ROW_KEY = "startRow";
    /**
     * The key used to indicate the number of rows to return for paged queries.
     */
    public static final String NUM_ROWS_KEY = "numRows";
    /**
     * The key used to indicate a request for all the keys for an item.
     */
    public static final String KEYS_KEY = "keys";
    /**
     * The key used to indicate a request for a page of keys for an item.
     */
    public static final String PAGED_KEYS = "pagedKeys";
    /**
     * The key used to indicate a request for a page of sub-items for an item.
     */
    public static final String PAGED_ITEMS = "pagedItems";
    /**
     * The key used to indicate a request to count the number of keys for an item.
     */
    public static final String COUNT_ITEMS = "countItems";
    private Pagination pagination;

    /**
     * Create a new Query.
     */
    public Query() {
        super();
    }

    /**
     * Test whether this is a "count items" query.
     * 
     * @return True if this is a "count items" query, false otherwise.
     */
    public boolean isCountItemsQuery() {
        return containsKey(COUNT_ITEMS);
    }

    /**
     * Test whether this is a "keys" query.
     * 
     * @return True if this is a "keys" query, false otherwise.
     */
    public boolean isKeysQuery() {
        return containsKey(KEYS_KEY);
    }

    /**
     * Test whether this is a "paged items" query.
     * 
     * @return True if this is a "paged items" query, false otherwise.
     */
    public boolean isPagedItemsQuery() {
        return containsKey(PAGED_ITEMS);
    }

    /**
     * Test whether this is a "paged keys" query.
     * 
     * @return True if this is a "paged keys" query, false otherwise.
     */
    public boolean isPagedKeysQuery() {
        return containsKey(PAGED_KEYS);
    }

    /**
     * Return the Pagination specified by this query, if any.
     * 
     * @return The Pagination specified by this query, or null if there was not one. 
     */
    public Pagination pagination() {
        if (pagination == null) {
            pagination = new Pagination(this);
        }
        return pagination;
    }
}