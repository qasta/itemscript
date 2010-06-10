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

package test.org.itemscript;

import junit.framework.Assert;

import org.itemscript.core.exceptions.ItemscriptError;
import org.itemscript.core.url.Url;
import org.junit.Test;

public class UrlTest extends ItemscriptTestBase {
    private void assertPathDirectoryFilename(Url url, String path, String directory, String filename) {
        assertEquals(url.pathString(), path);
        assertEquals(url.directory(), directory);
        assertEquals(url.filename(), filename);
    }

    private void assertQueryFragment(Url url, String query, String fragment) {
        assertEquals(url.queryString(), query);
        assertEquals(url.fragmentString(), fragment);
    }

    private void assertRelativeUrl(String baseUrl, String relativeUrl, String finalUrl) {
        Url finalUrlObj = system().util()
                .createRelativeUrl(baseUrl, relativeUrl);
        assertEquals(finalUrlObj.toString(), finalUrl);
    }

    private void assertSchemeHostnamePort(Url url, String scheme, String hostname, String port) {
        assertEquals(url.scheme(), scheme);
        assertEquals(url.hostname(), hostname);
        assertEquals(url.port(), port);
    }

    @Test
    public void testBadPercentEscape() {
        boolean failed = false;
        try {
            system().util()
                    .createUrl("broken/escape/%Y");
        } catch (ItemscriptError e) {
            failed = true;
        }
        Assert.assertTrue(failed);
    }

    @Test
    public void testDecodingFragment() {
        Url url = system().util()
                .createUrl("#%26/%27");
        assertEquals("&", url.fragment()
                .get(0));
        assertEquals("'", url.fragment()
                .get(1));
    }

    @Test
    public void testDecodingPath() {
        Url url = system().util()
                .createUrl("file:///%26");
        assertEquals(url.path()
                .get(1), "&");
    }

    @Test
    public void testDecodingQuery() {
        Url url = system().util()
                .createUrl("file:///foo?key=%27");
        assertEquals(url.query()
                .get("key")
                .get(0), "'");
    }

    @Test
    public void testEverything() {
        Url url = system().util()
                .createUrl("http://test.com:123/one/two/three.foo?key=value&key=anothervalue&key2=value2#a/b/c");
        assertSchemeHostnamePort(url, "http", "test.com", "123");
        assertPathDirectoryFilename(url, "/one/two/three.foo", "/one/two/", "three.foo");
        assertQueryFragment(url, "key=value&key=anothervalue&key2=value2", "a/b/c");
        assertEquals("a", url.fragment()
                .get(0));
        assertEquals("b", url.fragment()
                .get(1));
        assertEquals("c", url.fragment()
                .get(2));
    }

    @Test
    public void testFileUrl() {
        Url url = system().util()
                .createUrl("file://somehost/a/path/filename.txt");
        assertSchemeHostnamePort(url, "file", "somehost", null);
        assertPathDirectoryFilename(url, "/a/path/filename.txt", "/a/path/", "filename.txt");
        assertQueryFragment(url, null, null);
    }

    @Test
    public void testFragmentOnly() {
        Url url = system().util()
                .createUrl("#fragment/one/two/three");
        assertSchemeHostnamePort(url, null, null, null);
        assertPathDirectoryFilename(url, null, null, null);
        assertQueryFragment(url, null, "fragment/one/two/three");
        assertEquals(url.fragment()
                .get(0), "fragment");
        assertEquals(url.fragment()
                .get(1), "one");
    }

    public void testMissingColonSlashSlash() {
        boolean failed = false;
        try {
            system().util()
                    .createUrl("http:willfail");
        } catch (ItemscriptError e) {
            failed = true;
        }
        Assert.assertTrue(failed);
    }

    @Test
    public void testMultiValueKey() {
        Url url = system().util()
                .createUrl("?key=value&key=anotherValue");
        assertEquals(url.query()
                .get("key")
                .size(), 2);
        assertEquals(url.query()
                .get("key")
                .get(0), "value");
        assertEquals(url.query()
                .get("key")
                .get(1), "anotherValue");
    }

    @Test
    public void testParseSpeed() {
        for (int i = 0; i < 1000; ++i) {
            Url it = system().util()
                    .createUrl("http://pathendswithadotdot.com/?ojiojo=jnnjkjn&knin=kjniknikn");
        }
    }

    @Test
    public void testPathComponents() {
        Url url = system().util()
                .createUrl("http://host.com/one.dir/two.dir/three.file");
        assertSchemeHostnamePort(url, "http", "host.com", null);
        assertPathDirectoryFilename(url, "/one.dir/two.dir/three.file", "/one.dir/two.dir/", "three.file");
        assertEquals(url.path()
                .get(0), "/");
        assertEquals(url.path()
                .get(1), "one.dir");
        assertEquals(url.path()
                .get(2), "two.dir");
        assertEquals(url.path()
                .get(3), "three.file");
    }

    @Test
    public void testQueryAndFragment() {
        Url url = system().util()
                .createUrl("?key=value#one/two/three");
        assertQueryFragment(url, "key=value", "one/two/three");
    }

    @Test
    public void testQueryOnly() {
        Url url = system().util()
                .createUrl("?key1=value1&key2=value2");
        assertSchemeHostnamePort(url, null, null, null);
        assertPathDirectoryFilename(url, null, null, null);
        assertQueryFragment(url, "key1=value1&key2=value2", null);
        assertEquals(url.query()
                .get("key1")
                .get(0), "value1");
        assertEquals(url.query()
                .get("key2")
                .get(0), "value2");
    }

    @Test
    public void testRelativePath() {
        Url url = system().util()
                .createUrl("one/two/three");
        assertPathDirectoryFilename(url, "one/two/three", "one/two/", "three");
    }

    @Test
    public void testRelativeUrls() {
        assertRelativeUrl("", "http://emptybasepath.com/", "http://emptybasepath.com/");
        assertRelativeUrl("http://emptyrelativepath.com", "", "http://emptyrelativepath.com");
        assertRelativeUrl("http://withafullurl.com/some/path", "http://foo.com/bar/", "http://foo.com/bar/");
        assertRelativeUrl("file:/some/path/to/a/filename.txt", "some/relative/path/file.html",
                "file:///some/path/to/a/some/relative/path/file.html");
        assertRelativeUrl("file:/some/path/to/a/filename.txt", "/an/absolute/path/file.html",
                "file:///an/absolute/path/file.html");
        assertRelativeUrl("http://nopath.com", "some/relative/path/file.html",
                "http://nopath.com/some/relative/path/file.html");
        assertRelativeUrl("http://nopath.com", "/an/absolute/path/file.html",
                "http://nopath.com/an/absolute/path/file.html");
        assertRelativeUrl("http://withportandslash.com:8080/", "some/relative/path/file.html",
                "http://withportandslash.com:8080/some/relative/path/file.html");
        assertRelativeUrl("http://withportandslash.com:8080/", "/an/absolute/path/file.html",
                "http://withportandslash.com:8080/an/absolute/path/file.html");
        assertRelativeUrl("http://withadirectory.com/some/directory/", "some/relative/path/file.html",
                "http://withadirectory.com/some/directory/some/relative/path/file.html");
        assertRelativeUrl("http://withadirectory.com/some/directory/", "/an/absolute/path/file.html",
                "http://withadirectory.com/an/absolute/path/file.html");
        assertRelativeUrl("http://withajustaslash.com/some/path", "/", "http://withajustaslash.com/");
        assertRelativeUrl("http://withafragment.com/some/path", "#fragment",
                "http://withafragment.com/some/path#fragment");
        assertRelativeUrl("http://withafragment.com/some/path#onefragment", "#twofragment",
                "http://withafragment.com/some/path#twofragment");
        assertRelativeUrl("http://basehasaquery.com/something?query=value", "",
                "http://basehasaquery.com/something?query=value");
        assertRelativeUrl("http://basehasaquery.com/something?query=value", "?a=different&query=string",
                "http://basehasaquery.com/something?a=different&query=string");
        assertRelativeUrl("http://basehasaquery.com/something?query=value", "#a/fragment",
                "http://basehasaquery.com/something?query=value#a/fragment");
        assertRelativeUrl("http://basehasaquery.com/something?query=value", "/an/absolute/path",
                "http://basehasaquery.com/an/absolute/path");
        assertRelativeUrl("http://basehasaquery.com/something?query=value", "a/relative/path",
                "http://basehasaquery.com/a/relative/path");
        assertRelativeUrl("http://basehasaquery.com/something?query=value",
                "?a=different&query=string#andafragment",
                "http://basehasaquery.com/something?a=different&query=string#andafragment");
        assertRelativeUrl("http://basehasaquery.com/something?query=value",
                "/some/path?a=different&query=string#andafragment",
                "http://basehasaquery.com/some/path?a=different&query=string#andafragment");
        assertRelativeUrl("http://basehasaquery.com/something?query=value#fragment", "",
                "http://basehasaquery.com/something?query=value#fragment");
        assertRelativeUrl("http://basehasaquery.com/something?query=value#fragment", "?a=different&query=string",
                "http://basehasaquery.com/something?a=different&query=string");
        assertRelativeUrl("http://dotslashpath.com/some/./directory/", "some/relative/./path/file.html",
                "http://dotslashpath.com/some/directory/some/relative/path/file.html");
        assertRelativeUrl("http://dotslashpath.com/some/./directory/", "/an/absolute/./path/file.html",
                "http://dotslashpath.com/an/absolute/path/file.html");
        assertRelativeUrl("http://pathendswithadot.com/foo/.", "some/relative/./path/file.html",
                "http://pathendswithadot.com/foo/some/relative/path/file.html");
        assertRelativeUrl("http://pathendswithadot.com/foo/.", "/an/absolute/./path/file.html",
                "http://pathendswithadot.com/an/absolute/path/file.html");
        assertRelativeUrl(
                "http://pathendswithadotdot.com/keepthisone/foo/bar/../baz/../../?with.a.query#andafragment",
                "some/relative/./path/file.html?withaquery#andanotherfragment",
                "http://pathendswithadotdot.com/keepthisone/some/relative/path/file.html?withaquery#andanotherfragment");
        assertRelativeUrl("http://pathendswithadotdot.com/foo/..", "/an/absolute/./path/file.html",
                "http://pathendswithadotdot.com/an/absolute/path/file.html");
        assertRelativeUrl("http://pathendswithadotdot.com/foo/..", "a/directory/path/",
                "http://pathendswithadotdot.com/a/directory/path/");
    }

    @Test
    public void testShortMemUrl() {
        Url url = system().util()
                .createUrl("mem:/something/something/something");
        assertSchemeHostnamePort(url, "mem", null, null);
        assertPathDirectoryFilename(url, "/something/something/something", "/something/something/", "something");
        assertQueryFragment(url, null, null);
    }

    @Test
    public void testUnknownScheme() {
        Url url = system().util()
                .createUrl("unknownscheme:somethingsomethingsomething");
        assertEquals(url.scheme(), "unknownscheme");
        assertEquals(url.remainder(), "somethingsomethingsomething");
    }
}