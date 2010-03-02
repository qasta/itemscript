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
/*
 * Incorporates code from the W3 issued under this license:
 *
 * Created: 17 April 1997
 * Author: Bert Bos <bert@w3.org>
 *
 * URLUTF8Encoder: http://www.w3.org/International/URLUTF8Encoder.java
 *
 * Copyright © 1997 World Wide Web Consortium, (Massachusetts
 * Institute of Technology, European Research Consortium for
 * Informatics and Mathematics, Keio University). All Rights Reserved. 
 * This work is distributed under the W3C® Software License [1] in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE.
 *
 * [1] http://www.w3.org/Consortium/Legal/2002/copyright-software-20021231
 */

package org.itemscript.core.url;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.itemscript.core.exceptions.ItemscriptError;

final class HttpLikeSchemeParser implements SchemeParser {
    private String urlString;
    private String remainder;
    private int length;
    private boolean hasScheme;
    private String scheme;
    private String authority;
    private String userInformation;
    private String hostname;
    private boolean hasPort;
    private String port;
    private String pathString;
    private boolean hasQuery;
    private boolean hasFragment;
    private String directory;
    private String filename;
    private Path path = new Path();;
    private String queryString;
    private Query query = new Query();
    private String fragmentString;
    private Fragment fragment = new Fragment();

    private Url _parse(int endOfScheme) {
        if (endOfScheme == 0) {
            hasScheme = true;
        } else {
            hasScheme = false;
        }
        int startOfPath;
        if (hasScheme) {
            remainder = null;
            scheme = null;
            hostname = null;
            port = null;
            startOfPath = 0;
        } else {
            scheme = substring(0, endOfScheme).toLowerCase();
            remainder = urlString.substring(endOfScheme + 1);
            int endOfAuthority = findAuthority(endOfScheme);
            startOfPath = endOfAuthority;
        }
        int endOfPath = findPath(startOfPath);
        int endOfPathOrQuery = endOfPath;
        if (hasQuery) {
            endOfPathOrQuery = findQuery(endOfPath);
        }
        if (hasFragment) {
            int endOfFragment = findFragment(endOfPathOrQuery);
        }
        return new Url(urlString, remainder, scheme, authority, userInformation, hostname, port, pathString, path,
                directory, filename, queryString, query, fragmentString, fragment);
    }

    private char charAt(int index) {
        // At the end of the string or past it, return a 0 char.
        if (index >= urlString.length()) { return 0; }
        return urlString.charAt(index);
    }

    private void decodePath() {
        // Look for a leading '/' indicating a rooted path.
        int startOfFirstNonRootComponent = 0;
        if (pathString.length() > 0 && pathString.charAt(0) == '/') {
            path.add("/");
            startOfFirstNonRootComponent = 1;
        }
        int startOfComponent = startOfFirstNonRootComponent;
        for (int i = startOfFirstNonRootComponent; i <= pathString.length(); ++i) {
            char c;
            if (i == pathString.length()) {
                c = 0;
            } else {
                c = pathString.charAt(i);
            }
            if (c == '/' || c == 0) {
                if (i > startOfComponent) {
                    String component = pathString.substring(startOfComponent, i);
                    path.add(Url.decode(component));
                }
                startOfComponent = i + 1;
            }
        }
        // Look for a trailing '/' indicating no filename. If not found, set the directory & filename.
        if (pathString.length() > 0 && pathString.charAt(pathString.length() - 1) != '/') {
            filename = path.get(path.size() - 1);
            directory = pathString.substring(0, pathString.length() - filename.length());
        } else {
            directory = pathString;
            filename = null;
        }
    }

    private void decodeQuery() {
        boolean inKey = true;
        String key = "";
        int startOfKey = 0;
        int startOfValue = 0;
        for (int i = 0; i <= queryString.length(); ++i) {
            char c;
            if (i == queryString.length()) {
                c = 0;
            } else {
                c = queryString.charAt(i);
            }
            if (inKey) {
                if (c == '=') {
                    key = Url.decode(queryString.substring(startOfKey, i));
                    inKey = false;
                    startOfValue = i + 1;
                } else if (c == '&' || c == 0) {
                    // A key with no value....
                    key = queryString.substring(startOfKey, i);
                    pushValue(key, "");
                    inKey = true;
                    startOfKey = i + 1;
                }
            } else {
                if (c == '&' || c == 0) {
                    String value = Url.decode(queryString.substring(startOfValue, i));
                    pushValue(key, value);
                    inKey = true;
                    startOfKey = i + 1;
                }
            }
        }
    }

    private int findAuthority(int endOfScheme) {
        // First check that after the scheme comes a double slash "//" - if not, there's no authority.
        if (charAt(endOfScheme + 1) != '/' || charAt(endOfScheme + 2) != '/') {
            authority = null;
            userInformation = null;
            hostname = null;
            port = null;
            return endOfScheme + 1;
        }
        int startOfAuthority = endOfScheme + 3;
        int endOfAuthority = 0;
        boolean hasUserInformation = false;
        boolean hasPort = false;
        // Look for a /, ?, or # to end the authority.
        for (int i = startOfAuthority; i <= length; ++i) {
            char c = charAt(i);
            if (c == '@') {
                hasUserInformation = true;
            } else if (c == 0) {
                endOfAuthority = i;
                break;
            } else if (c == '/') {
                endOfAuthority = i;
                break;
            }
        }
        // The authority can be empty even if the url had a scheme e.g. "file:///foo/bar/"
        if (startOfAuthority == endOfAuthority) {
            authority = null;
        } else {
            authority = substring(startOfAuthority, endOfAuthority).toLowerCase();
        }
        if (authority != null) {
            // Now break it up and look for a user information section, host, and port.
            String hostnameAndPort = "";
            if (hasUserInformation) {
                for (int i = 0; i < authority.length(); ++i) {
                    char c = authority.charAt(i);
                    if (c == '@') {
                        userInformation = authority.substring(0, i);
                        hostnameAndPort = authority.substring(i + 1);
                        break;
                    }
                }
            } else {
                userInformation = null;
                hostnameAndPort = authority;
            }
            // Now look for a hostname and maybe a port:
            for (int i = 0; i <= hostnameAndPort.length(); ++i) {
                char c;
                if (i == hostnameAndPort.length()) {
                    c = 0;
                } else {
                    c = hostnameAndPort.charAt(i);
                }
                if (c == ':') {
                    hostname = hostnameAndPort.substring(0, i);
                    port = hostnameAndPort.substring(i + 1);
                    break;
                } else if (c == 0) {
                    hostname = hostnameAndPort;
                    port = null;
                }
            }
        } else {
            userInformation = null;
            hostname = null;
            port = null;
        }
        return endOfAuthority;
    }

    private int findFragment(int endOfPathOrQuery) {
        int startOfFragment = endOfPathOrQuery + 1;
        int endOfFragment = 0;
        // For fragments they always run to the end of the string, but we're gonna go by character by character just for the hell of it...
        for (int i = startOfFragment; i <= length; ++i) {
            char c = charAt(i);
            if (c == 0) {
                endOfFragment = i;
                break;
            }
        }
        fragmentString = substring(startOfFragment, endOfFragment);
        fragment = Url.decodeFragment(fragmentString);
        return endOfFragment;
    }

    private int findPath(int startOfPath) {
        int endOfPath = 0;
        for (int i = startOfPath; i <= length; ++i) {
            char c = charAt(i);
            // The path runs until the end of the string or a ? or # character.
            if (c == 0) {
                endOfPath = i;
                hasQuery = false;
                hasFragment = false;
                break;
            } else if (c == '?') {
                endOfPath = i;
                hasQuery = true;
                break;
            } else if (c == '#') {
                endOfPath = i;
                hasQuery = false;
                hasFragment = true;
                break;
            }
        }
        // An empty path with a scheme is equivalent to "/" - e.g. a URL like "http://well.com"
        // An empty path with no scheme is equivalent to null, though - e.g. "?foo=bar" or "#someanchor"
        if (startOfPath == endOfPath) {
            if (hasScheme) {
                pathString = null;
            } else {
                pathString = "/";
            }
        } else {
            pathString = substring(startOfPath, endOfPath);
        }
        // if there was a scheme, paths must start with "/"
        if (!hasScheme) {
            if (pathString.charAt(0) != '/') { throw new ItemscriptError(
                    "error.itemscript.Url.findPath.url.with.scheme.path.did.not.start.with.slash", pathString); }
        }
        if (pathString != null) {
            decodePath();
        }
        return endOfPath;
    }

    private int findQuery(int endOfPath) {
        int startOfQuery = endOfPath + 1;
        int endOfQuery = 0;
        for (int i = startOfQuery; i <= length; ++i) {
            char c = charAt(i);
            if (c == 0) {
                endOfQuery = i;
                hasFragment = false;
                break;
            } else if (c == '#') {
                endOfQuery = i;
                hasFragment = true;
                break;
            }
        }
        queryString = substring(startOfQuery, endOfQuery);
        decodeQuery();
        return endOfQuery;
    }

    public Url parse(String urlString, int endOfScheme) {
        this.urlString = urlString;
        this.length = urlString.length();
        return _parse(endOfScheme);
    }

    public void pushValue(String key, String value) {
        List<String> list = query.get(key);
        if (list == null) {
            list = new ArrayList<String>();
            query.put(key, list);
        }
        list.add(value);
    }

    private String substring(int begin, int end) {
        return urlString.substring(begin, end);
    }
}

final class UnknownSchemeParser implements SchemeParser {
    public Url parse(String urlString, int endOfScheme) {
        String scheme = urlString.substring(0, endOfScheme);
        String remainder = urlString.substring(endOfScheme + 1, urlString.length());
        boolean foundFragment = false;
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < urlString.length(); ++i) {
            char c = urlString.charAt(i);
            if (foundFragment) {
                sb.append(c);
            }
            if (c == '#') {
                foundFragment = true;
            }
        }
        String fragmentString = null;
        Fragment fragment = null;
        if (sb.length() > 0) {
            fragmentString = sb.toString();
            fragment = Url.decodeFragment(fragmentString);
        }
        return new Url(urlString, remainder, scheme, null, null, null, null, null, null, null, null, null, null,
                fragmentString, fragment);
    }
}

/**
 * Represents a decoded URL in the Itemscript system.
 * 
 * This separate implementation to {@link java.net.URL} was necessary because the latter is not supported in the GWT
 * environment.
 * 
 * @author Jacob Davies<br/><a href="mailto:jacob@itemscript.org">jacob@itemscript.org</a>
 */
// FIXME - this entire class should be made non-static...
public final class Url {
    private static String addBasePath(Url baseUrl, Url relativeUrl, String finalUrl) {
        // If the relative URL didn't have a path, use the entire original path:
        finalUrl += baseUrl.pathString();
        // And if the relative URL had no query, use the entire original query if there was one:
        if (relativeUrl.queryString() == null || relativeUrl.queryString()
                .length() == 0) {
            if (baseUrl.queryString() != null && baseUrl.queryString()
                    .length() > 0) {
                finalUrl += "?" + baseUrl.queryString();
            }
        } else {
            // Otherwise add the relative URL's query:
            finalUrl += "?" + relativeUrl.queryString();
        }
        return finalUrl;
    }

    private static String addRelativePath(Url baseUrlObj, Url relativeUrlObj, String finalUrl) {
        // If the relative URL's path was absolute, ignore the base URL's path:
        List<String> unreducedPath = combinePaths(baseUrlObj, relativeUrlObj);
        // Now, throw out the leading "/" entry for now, and remove any components consisting only of "."
        List<String> reducedPath = reducePath(relativeUrlObj, unreducedPath);
        // Bearing in mind that the path should start with a slash...
        if (reducedPath.size() > 0) {
            for (int i = 0; i < reducedPath.size(); ++i) {
                finalUrl += "/";
                finalUrl += reducedPath.get(i);
            }
        } else {
            finalUrl += "/";
        }
        // OK - now, if the relative URL ended with a "/" AND was not just the string "/" AND the final URL doesn't
        // already end with a slash, append one...
        if (relativeUrlObj.pathString()
                .length() > 1 && relativeUrlObj.pathString()
                .endsWith("/") && !finalUrl.endsWith("/")) {
            finalUrl += "/";
        }
        // Now if the relative URL had a query, add it:
        if (relativeUrlObj.queryString() != null && relativeUrlObj.queryString()
                .length() > 0) {
            finalUrl += "?" + relativeUrlObj.queryString();
        }
        return finalUrl;
    }

    private static List<String> combinePaths(Url baseUrlObj, Url relativeUrlObj) {
        List<String> unreducedPath = new ArrayList<String>();
        if (relativeUrlObj.path()
                .get(0)
                .equals("/")) {
            unreducedPath.addAll(relativeUrlObj.path());
        } else {
            // Otherwise add the relative URL path to the base URL's directory:
            int lastBaseElementToAdd;
            // If it had a filename, stop before that.. unless the filename was ".."!
            if (baseUrlObj.filename() != null && baseUrlObj.filename()
                    .length() > 0) {
                if (baseUrlObj.filename()
                        .equals("..")) {
                    lastBaseElementToAdd = baseUrlObj.path()
                            .size() - 1;
                } else {
                    lastBaseElementToAdd = baseUrlObj.path()
                            .size() - 2;
                }
            } else {
                lastBaseElementToAdd = baseUrlObj.path()
                        .size() - 1;
            }
            for (int i = 0; i <= lastBaseElementToAdd; ++i) {
                unreducedPath.add(baseUrlObj.path()
                        .get(i));
            }
            unreducedPath.addAll(relativeUrlObj.path());
        }
        return unreducedPath;
    }

    /**
     * URL-decode the supplied string.
     * 
     * @param s The String to decode.
     * @return The decoded String.
     */
    public static String decode(String s) {
        StringBuffer sbuf = new StringBuffer();
        int l = s.length();
        int ch = -1;
        int b, sumb = 0;
        for (int i = 0, more = -1; i < l; i++) {
            /* Get next byte b from URL segment s */
            switch (ch = s.charAt(i)) {
                case '%' :
                    if (i + 2 >= l) { throw new ItemscriptError(
                            "error.itemscript.Url.decode.encountered.percent.sign.without.trailing.characters", s); }
                    ch = s.charAt(++i);
                    int hb =
                            (Character.isDigit((char) ch) ? ch - '0' : 10 + Character.toLowerCase((char) ch) - 'a') & 0xF;
                    ch = s.charAt(++i);
                    int lb =
                            (Character.isDigit((char) ch) ? ch - '0' : 10 + Character.toLowerCase((char) ch) - 'a') & 0xF;
                    b = (hb << 4) | lb;
                    break;
                case '+' :
                    b = ' ';
                    break;
                default :
                    b = ch;
            }
            /* Decode byte b as UTF-8, sumb collects incomplete chars */
            if ((b & 0xc0) == 0x80) { // 10xxxxxx (continuation byte)
                sumb = (sumb << 6) | (b & 0x3f); // Add 6 bits to sumb
                if (--more == 0) sbuf.append((char) sumb); // Add char to sbuf
            } else if ((b & 0x80) == 0x00) { // 0xxxxxxx (yields 7 bits)
                sbuf.append((char) b); // Store in sbuf
            } else if ((b & 0xe0) == 0xc0) { // 110xxxxx (yields 5 bits)
                sumb = b & 0x1f;
                more = 1; // Expect 1 more byte
            } else if ((b & 0xf0) == 0xe0) { // 1110xxxx (yields 4 bits)
                sumb = b & 0x0f;
                more = 2; // Expect 2 more bytes
            } else if ((b & 0xf8) == 0xf0) { // 11110xxx (yields 3 bits)
                sumb = b & 0x07;
                more = 3; // Expect 3 more bytes
            } else if ((b & 0xfc) == 0xf8) { // 111110xx (yields 2 bits)
                sumb = b & 0x03;
                more = 4; // Expect 4 more bytes
            } else /*if ((b & 0xfe) == 0xfc)*/{ // 1111110x (yields 1 bit)
                sumb = b & 0x01;
                more = 5; // Expect 5 more bytes
            }
            /* We don't test if the UTF-8 encoding is well-formed */
        }
        return sbuf.toString();
    }

    /**
     * Decode the given fragment string.
     * 
     * @param fragmentString The fragment string to decode.
     * @return The decoded Fragment.
     */
    public static Fragment decodeFragment(String fragmentString) {
        Fragment fragment = new Fragment();
        int startOfComponent = 0;
        for (int i = 0; i <= fragmentString.length(); ++i) {
            char c;
            if (i == fragmentString.length()) {
                c = 0;
            } else {
                c = fragmentString.charAt(i);
            }
            if (c == '/' || c == 0) {
                if (i > startOfComponent) {
                    String component = fragmentString.substring(startOfComponent, i);
                    fragment.add(decode(component));
                    startOfComponent = i + 1;
                }
            }
        }
        return fragment;
    }

    /**
    * Encode a string to the "x-www-form-urlencoded" form, enhanced
    * with the UTF-8-in-URL proposal. This is what happens:
    *
    * <ul>
    * <li><p>The ASCII characters 'a' through 'z', 'A' through 'Z',
    *        and '0' through '9' remain the same.
    *
    * <li><p>The unreserved characters - _ . ! ~ * ' ( ) remain the same.
    *
    * <li><p>The space character ' ' is converted into a plus sign '+'.
    *
    * <li><p>All other ASCII characters are converted into the
    *        3-character string "%xy", where xy is
    *        the two-digit hexadecimal representation of the character
    *        code
    *
    * <li><p>All non-ASCII characters are encoded in two steps: first
    *        to a sequence of 2 or 3 bytes, using the UTF-8 algorithm;
    *        secondly each of these bytes is encoded as "%xx".
    * </ul>
    *
    * @param s The string to be encoded
    * @return The encoded string
    */
    public static String encode(String s) {
        if (s == null) { return null; }
        StringBuffer sbuf = new StringBuffer();
        int len = s.length();
        for (int i = 0; i < len; i++) {
            int ch = s.charAt(i);
            if ('A' <= ch && ch <= 'Z') { // 'A'..'Z'
                sbuf.append((char) ch);
            } else if ('a' <= ch && ch <= 'z') { // 'a'..'z'
                sbuf.append((char) ch);
            } else if ('0' <= ch && ch <= '9') { // '0'..'9'
                sbuf.append((char) ch);
            } else if (ch == ' ') { // space
                sbuf.append('+');
            } else if (ch == '-' || ch == '_' // unreserved
                    || ch == '.' || ch == '!' || ch == '~' || ch == '*' || ch == '\'' || ch == '(' || ch == ')') {
                sbuf.append((char) ch);
            } else if (ch <= 0x007f) { // other ASCII
                sbuf.append(hex[ch]);
            } else if (ch <= 0x07FF) { // non-ASCII <= 0x7FF
                sbuf.append(hex[0xc0 | (ch >> 6)]);
                sbuf.append(hex[0x80 | (ch & 0x3F)]);
            } else { // 0x7FF < ch <= 0xFFFF
                sbuf.append(hex[0xe0 | (ch >> 12)]);
                sbuf.append(hex[0x80 | ((ch >> 6) & 0x3F)]);
                sbuf.append(hex[0x80 | (ch & 0x3F)]);
            }
        }
        return sbuf.toString();
    }

    private static boolean isSchemeChar(char c) {
        if (c == '+' || c == '.' || c == '-') { return true; }
        if (Character.isLetterOrDigit(c)) { return true; }
        return false;
    }

    private static List<String> reducePath(Url relativeUrlObj, List<String> unreducedPath) {
        // Otherwise we need to remove the "." and ".." entries. First, the leading "/" and any "." entries:
        List<String> reducedPath = new ArrayList<String>();
        for (int i = 1; i < unreducedPath.size(); ++i) {
            String component = unreducedPath.get(i);
            if (!component.equals(".")) {
                reducedPath.add(component);
            }
        }
        // Now, as long as any exist, remove any combinations of a component followed by .., iteratively.
        LOOKFORDOTDOTS : while (true) {
            int indexOfFirstDotDot = -1;
            COMPONENTS : for (int i = 0; i < reducedPath.size(); ++i) {
                String component = reducedPath.get(i);
                if (component.equals("..")) {
                    indexOfFirstDotDot = i;
                    break COMPONENTS;
                }
            }
            // If we didn't find any, move on.
            if (indexOfFirstDotDot == -1) {
                break LOOKFORDOTDOTS;
            }
            // Otherwise create a new reduced path with the component before the first ".." and the ".." itself removed.
            if (indexOfFirstDotDot == 0) { throw ItemscriptError.internalError(relativeUrlObj,
                    "first.component.was.dot.dot", reducedPath + ""); }
            List<String> newReducedPath = new ArrayList<String>();
            // OK, now add all the components up to the one before the ".."
            for (int i = 0; i < (indexOfFirstDotDot - 1); ++i) {
                newReducedPath.add(reducedPath.get(i));
            }
            // Now add all the components after the ".." and repeat...
            for (int i = indexOfFirstDotDot + 1; i < reducedPath.size(); ++i) {
                newReducedPath.add(reducedPath.get(i));
            }
            reducedPath = newReducedPath;
        }
        return reducedPath;
    }

    private final String remainder;
    private final String scheme;
    private final String authority;
    private final String userInformation;
    private final String hostname;
    private final String port;
    private final String pathString;
    private final Path path;
    private final String directory;
    private final String filename;
    private final String urlString;
    private final String queryString;
    private final Query query;
    private final String fragmentString;
    private final Fragment fragment;
    /**
     * The mem scheme.
     */
    public final static String MEM_SCHEME = "mem";
    /**
     * The http scheme.
     */
    public final static String HTTP_SCHEME = "http";
    /**
     * The https scheme.
     */
    public final static String HTTPS_SCHEME = "https";
    /**
     * The file scheme.
     */
    public final static String FILE_SCHEME = "file";
    /**
     * The cookie scheme.
     */
    private final static String COOKIE_SCHEME = "cookie";
    /**
     * The json-file scheme.
     */
    private final static String JSON_FILE_SCHEME = "json-file";
    /**
     * The text-file scheme.
     */
    private final static String TEXT_FILE_SCHEME = "text-file";
    private final static String NO_SCHEME = "NO SCHEME";
    private final static String UNKNOWN_SCHEME = "UNKNOWN SCHEME";
    private final static Map<String, SchemeParserFactory> parserFactories =
            new HashMap<String, SchemeParserFactory>();
    private final static String[] hex =
            {"%00", "%01", "%02", "%03", "%04", "%05", "%06", "%07", "%08", "%09", "%0a", "%0b", "%0c", "%0d",
                    "%0e", "%0f", "%10", "%11", "%12", "%13", "%14", "%15", "%16", "%17", "%18", "%19", "%1a",
                    "%1b", "%1c", "%1d", "%1e", "%1f", "%20", "%21", "%22", "%23", "%24", "%25", "%26", "%27",
                    "%28", "%29", "%2a", "%2b", "%2c", "%2d", "%2e", "%2f", "%30", "%31", "%32", "%33", "%34",
                    "%35", "%36", "%37", "%38", "%39", "%3a", "%3b", "%3c", "%3d", "%3e", "%3f", "%40", "%41",
                    "%42", "%43", "%44", "%45", "%46", "%47", "%48", "%49", "%4a", "%4b", "%4c", "%4d", "%4e",
                    "%4f", "%50", "%51", "%52", "%53", "%54", "%55", "%56", "%57", "%58", "%59", "%5a", "%5b",
                    "%5c", "%5d", "%5e", "%5f", "%60", "%61", "%62", "%63", "%64", "%65", "%66", "%67", "%68",
                    "%69", "%6a", "%6b", "%6c", "%6d", "%6e", "%6f", "%70", "%71", "%72", "%73", "%74", "%75",
                    "%76", "%77", "%78", "%79", "%7a", "%7b", "%7c", "%7d", "%7e", "%7f", "%80", "%81", "%82",
                    "%83", "%84", "%85", "%86", "%87", "%88", "%89", "%8a", "%8b", "%8c", "%8d", "%8e", "%8f",
                    "%90", "%91", "%92", "%93", "%94", "%95", "%96", "%97", "%98", "%99", "%9a", "%9b", "%9c",
                    "%9d", "%9e", "%9f", "%a0", "%a1", "%a2", "%a3", "%a4", "%a5", "%a6", "%a7", "%a8", "%a9",
                    "%aa", "%ab", "%ac", "%ad", "%ae", "%af", "%b0", "%b1", "%b2", "%b3", "%b4", "%b5", "%b6",
                    "%b7", "%b8", "%b9", "%ba", "%bb", "%bc", "%bd", "%be", "%bf", "%c0", "%c1", "%c2", "%c3",
                    "%c4", "%c5", "%c6", "%c7", "%c8", "%c9", "%ca", "%cb", "%cc", "%cd", "%ce", "%cf", "%d0",
                    "%d1", "%d2", "%d3", "%d4", "%d5", "%d6", "%d7", "%d8", "%d9", "%da", "%db", "%dc", "%dd",
                    "%de", "%df", "%e0", "%e1", "%e2", "%e3", "%e4", "%e5", "%e6", "%e7", "%e8", "%e9", "%ea",
                    "%eb", "%ec", "%ed", "%ee", "%ef", "%f0", "%f1", "%f2", "%f3", "%f4", "%f5", "%f6", "%f7",
                    "%f8", "%f9", "%fa", "%fb", "%fc", "%fd", "%fe", "%ff"};
    static {
        SchemeParserFactory httpLike = new SchemeParserFactory() {
            public SchemeParser create() {
                return new HttpLikeSchemeParser();
            }
        };
        parserFactories.put(NO_SCHEME, httpLike);
        parserFactories.put(MEM_SCHEME, httpLike);
        parserFactories.put(HTTP_SCHEME, httpLike);
        parserFactories.put(HTTPS_SCHEME, httpLike);
        parserFactories.put(FILE_SCHEME, httpLike);
        parserFactories.put(COOKIE_SCHEME, httpLike);
        parserFactories.put(JSON_FILE_SCHEME, httpLike);
        parserFactories.put(TEXT_FILE_SCHEME, httpLike);
        SchemeParserFactory unknown = new SchemeParserFactory() {
            public SchemeParser create() {
                return new UnknownSchemeParser();
            }
        };
        parserFactories.put(UNKNOWN_SCHEME, unknown);
    }

    /**
     * Add a new SchemeParserFactory for a scheme.
     * 
     * @param scheme
     * @param factory
     */
    public static void addSchemeParserFactory(String scheme, SchemeParserFactory factory) {
        parserFactories.put(scheme, factory);
    }

    /**
     * Create a new Url from a string containing a URL.
     * 
     * @param urlString The string containing a URL.
     * @return The new Url object.
     */
    public static Url create(String urlString) {
        if (urlString == null) { return null; }
        // Remove leading and trailing whitespace first...
        urlString = urlString.trim();
        int startOfScheme = 0;
        int endOfScheme = 0;
        for (int i = startOfScheme; i < urlString.length(); ++i) {
            char c = urlString.charAt(i);
            if (!isSchemeChar(c)) {
                if (c == ':') {
                    endOfScheme = i;
                    break;
                } else {
                    break;
                }
            }
        }
        String scheme = null;
        if (endOfScheme > startOfScheme) {
            scheme = urlString.substring(startOfScheme, endOfScheme)
                    .toLowerCase();
        } else {
            scheme = NO_SCHEME;
        }
        if (!parserFactories.containsKey(scheme)) {
            scheme = UNKNOWN_SCHEME;
        }
        SchemeParser schemeParser = parserFactories.get(scheme)
                .create();
        return schemeParser.parse(urlString, endOfScheme);
    }

    /**
     * Create a new Url from a base URL and a relative URL.
     * 
     * @param baseUrl The base URL to work from.
     * @param relativeUrl The relative URL to add to the base URL.
     * @return The new Url object.
     */
    public static Url createRelative(String baseUrl, String relativeUrl) {
        return createRelative(create(baseUrl), create(relativeUrl));
    }

    /**
     * Create a new Url from a base Url object and a relative Url object.
     * 
     * @param baseUrl The base URL to work from.
     * @param relativeUrl The relative URL to add to the base URL.
     * @return The new Url object.
     */
    public static Url createRelative(Url baseUrl, Url relativeUrl) {
        String baseUrlString = baseUrl.toString();
        String relativeUrlString = relativeUrl.toString();
        // per RFC1808/18
        if (baseUrlString == null) {
            baseUrlString = "";
        }
        baseUrlString = baseUrlString.trim();
        if (relativeUrlString == null) {
            relativeUrlString = "";
        }
        relativeUrlString = relativeUrlString.trim();
        // If the base URL is empty, we return the relative URL as the whole URL.
        if (baseUrlString.length() == 0) { return create(relativeUrlString); }
        // If the relative URL is empty, we return the base URL as the whole URL.
        if (relativeUrlString.length() == 0) { return create(baseUrlString); }
        // If the putatively relative URL had a scheme, it wasn't actually relative, so return it as the whole URL.
        if (relativeUrl.scheme() != null) { return relativeUrl; }
        // If the base URL didn't have a scheme, uh, it wasn't a very good base URL now was it?
        if (baseUrl.scheme() == null) { throw ItemscriptError.internalError(baseUrl,
                "createRelative.baseUrl.did.not.have.a.scheme", baseUrlString); }
        // OK, so the final URL will have the scheme/hostname/port of the base URL, if it had any.
        String finalUrl = getSchemeHostnameAndPort(baseUrl);
        // If the relative URL had a path, add that path to the final URL.
        if (relativeUrl.pathString() != null && relativeUrl.pathString()
                .length() > 0) {
            finalUrl = addRelativePath(baseUrl, relativeUrl, finalUrl);
        } else {
            finalUrl = addBasePath(baseUrl, relativeUrl, finalUrl);
        }
        // Finally, if the relative URL had a fragment section, add it (the base fragment is always ignored unless the relative URL was empty, which is handled
        // above):
        if (relativeUrl.fragmentString() != null && relativeUrl.fragmentString()
                .length() > 0) {
            finalUrl += "#" + relativeUrl.fragmentString();
        }
        return create(finalUrl);
    }

    private static String getSchemeHostnameAndPort(Url url) {
        if (url.scheme()
                .equals("mem")) {
            // Sort of a hack...
            return "mem:";
        } else {
            return url.scheme()
                    + "://"
                    + (url.hostname() != null ? url.hostname() + (url.port() != null ? ":" + url.port() : "") : "");
        }
    }

    /**
     * Url constructor. Unless you are implementing a scheme parser, you almost certainly want to use the static create() method, not this constructor.
     * 
     * @param urlString
     * @param remainder
     * @param scheme
     * @param authority
     * @param userInformation
     * @param hostname
     * @param port
     * @param pathString
     * @param path
     * @param directory
     * @param filename
     * @param queryString
     * @param query
     * @param fragmentString
     * @param fragment
     */
    public Url(String urlString, String remainder, String scheme, String authority, String userInformation,
            String hostname, String port, String pathString, Path path, String directory, String filename,
            String queryString, Query query, String fragmentString, Fragment fragment) {
        this.urlString = urlString;
        this.remainder = remainder;
        this.scheme = scheme;
        this.authority = authority;
        this.userInformation = userInformation;
        this.hostname = hostname;
        this.port = port;
        this.pathString = pathString;
        this.path = path;
        this.directory = directory;
        this.filename = filename;
        this.queryString = queryString;
        this.query = query;
        this.fragmentString = fragmentString;
        this.fragment = fragment;
    }

    /**
     * Get the authority portion of the URL.
     * 
     * @return The authority portion of the URL.
     */
    public String authority() {
        return authority;
    }

    /**
     * Get the directory portion of the path from this URL.
     * 
     * @return The directory portion of the path, or null if there was no path.
     */
    public String directory() {
        return directory;
    }

    /**
     * Get the filename from the path, if any. A path that ends in "/" does not have a filename.
     * 
     * @return The filename, or null if there was no filename.
     */
    public String filename() {
        return filename;
    }

    /**
     * Get the fragment components for this URL. This divides the fragment by / characters, then un-URL-encodes each component
     * 
     * @return The Fragment object.
     */
    public Fragment fragment() {
        return fragment;
    }

    /**
     * Get the fragment, that portion of the URL after the # character. It will not be URL-decoded.
     * 
     * @return The fragment string.
     */
    public String fragmentString() {
        return fragmentString;
    }

    /**
     * Test whether this URL has a filename or not.
     * 
     * @return True if this URL has a filename, false otherwise.
     */
    public boolean hasFilename() {
        return filename != null;
    }

    /**
     * Test whether this URL has a fragment or not.
     * 
     * @return True if this URL had a fragment, false otherwise.
     */
    public boolean hasFragment() {
        return fragmentString != null;
    }

    /**
     * Test whether this URL has a path or not.
     * 
     * @return True if this URL has a path, false otherwise.
     */
    public boolean hasPath() {
        return pathString != null;
    }

    /**
     * Test whether this URL has a query or not.
     * 
     * @return True if this URL has a query, false otherwise.
     */
    public boolean hasQuery() {
        return queryString != null;
    }

    /**
     * Test whether this URL has a scheme or not.
     * 
     * @return True if this URL has a scheme, false otherwise.
     */
    public boolean hasScheme() {
        return scheme != null;
    }

    /**
     * Get the hostname.
     * 
     * @return The hostname, or null if there was no hostname.
     */
    public String hostname() {
        return hostname;
    }

    /**
     * Get the hostname and the port, if any.
     * 
     * @return The hostname and port, or null if there was no hostname and port.
     */
    public String hostnameAndPort() {
        if (hostname != null && port != null) {
            return hostname + ":" + port;
        } else {
            return hostname;
        }
    }

    /**
     * Get a list of the components in the path.
     * If the URL was absolute, the first component will be "/".
     * If the URL was relative but started with a /, the first component will be "/".
     * Otherwise the first component will be the first file/directory name in the path.
     * Each component will be URL-unencoded.
     * The last component may be a filename or a directory name; this method does not distinguish between the two. If you wish to know
     * if there was a trailing filename, call filename().
     * 
     * @return The Path object.
     */
    public Path path() {
        return path;
    }

    /**
     * Get the path portion of this URL.
     * 
     * @return The path, or null if there was no path.
     */
    public String pathString() {
        return pathString;
    }

    /**
     * Get the port, if any.
     * 
     * @return The port, or null if there was no port.
     */
    public String port() {
        return port;
    }

    /**
     * Get the components of the query. Each key corresponds to a list of values. Empty keys or values may be contained.
     * 
     * @return The Query object.
     */
    public Query query() {
        return query;
    }

    /**
     * Get the query section of the URL, that is, the section between the ? and either the end of the URL or a # character.
     * This is not URL-decoded.
     * 
     * @return The query string, or null if there was no query string.
     */
    public String queryString() {
        return queryString;
    }

    /**
     * Get the remainder of the URL string after the scheme.
     * 
     * @return The remainder of the URL string after the scheme.
     */
    public String remainder() {
        return remainder;
    }

    /**
     * Get the scheme for this URL.
     * 
     * @return The scheme for this URL.
     */
    public String scheme() {
        return scheme;
    }

    /**
     * Returns the URL string. This may not be exactly as the string passed to create().
     */
    public String toString() {
        return urlString;
    }

    /**
     * Get the user information from the URL.
     * 
     * @return The user information.
     */
    public String userInformation() {
        return userInformation;
    }

    /**
     * Get a new Url object without the fragment from this one.
     * 
     * @return The new Url object.
     */
    public Url withoutFragment() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < urlString.length(); ++i) {
            char c = urlString.charAt(i);
            if (c == '#') {
                break;
            }
            sb.append(c);
        }
        return create(sb.toString());
    }
}