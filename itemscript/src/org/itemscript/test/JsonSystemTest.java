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

package org.itemscript.test;

import java.util.Random;

import org.itemscript.core.connectors.GetCallback;
import org.itemscript.core.connectors.PutCallback;
import org.itemscript.core.connectors.RemoveCallback;
import org.itemscript.core.exceptions.ItemscriptError;
import org.itemscript.core.url.Url;
import org.itemscript.core.values.JsonArray;
import org.itemscript.core.values.JsonNull;
import org.itemscript.core.values.JsonNumber;
import org.itemscript.core.values.JsonObject;
import org.itemscript.core.values.JsonString;
import org.itemscript.core.values.JsonValue;
import org.junit.Assert;
import org.junit.Test;

public class JsonSystemTest extends ItemscriptTestBase {
    static Random random = new Random();
    static char[] letters = new char[26];
    static {
        for (char l = 'a'; l <= 'z'; ++l) {
            letters[l - 'a'] = l;
        }
    }

    private String randomString() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i <= (Math.abs(random.nextInt()) % 20); ++i) {
            sb.append(letters[Math.abs(random.nextInt()) % 26]);
        }
        return sb.toString();
    }

    @Test
    public void testCopy() {
        system().put("/1", true);
        system().copy("/1", "/2");
        assertEquals(system().get("/1")
                .booleanValue(), system().get("/2")
                .booleanValue());
    }

    @Test
    public void testLoad() {
        system().put("/xyz", "abc");
        system().put("/xyz/one", "123");
        system().put("/xyz/two", "456");
        JsonValue dump = system().get("/xyz?dump");
        system().put("/foo?load", dump);
        assertEquals("abc", system().getString("/foo"));
        assertEquals("123", system().getString("/foo/one"));
        assertEquals("456", system().getString("/foo/two"));
    }

    @Test
    public void testDump() {
        JsonObject object = system().getObject("/itemscript/connectors?dump");
        assertEquals(2, object.size());
        assertNotNull(object.getObject("value"));
        assertTrue(object.getObject("value")
                .containsKey("mem"));
    }

    @Test
    public void testPagedItems() {
        system().put("1", "x");
        system().put("2", "y");
        system().put("j", "z");
        JsonArray array = system().get("/?pagedItems")
                .asArray();
        assertEquals(4, array.size());
        assertEquals("x", array.getArray(0)
                .getString(1));
        assertEquals("y", array.getArray(1)
                .getString(1));
        assertEquals("z", array.getArray(3)
                .getString(1));
        JsonString key = system().get("/?pagedItems#0/0")
                .asString();
        assertEquals("mem:/?pagedItems", key.item()
                .source() + "");
        assertEquals("1", key.stringValue());
        JsonArray array2 = system().get("?pagedItems&numRows=1")
                .asArray();
        assertEquals(1, array2.size());
        assertEquals("x", array2.getArray(0)
                .getString(1));
        JsonArray array3 = system().get("?pagedItems&numRows=1&startRow=1")
                .asArray();
        assertEquals(1, array3.size());
        assertEquals("y", array3.getArray(0)
                .getString(1));
    }

    @Test
    public void testPagedKeys() {
        system().put("1", "x");
        system().put("2", "y");
        system().put("j", "z");
        JsonArray array = system().get("/?pagedKeys")
                .asArray();
        assertEquals(4, array.size());
        assertEquals("x", array.item()
                .getString(array.getString(0)));
        assertEquals("y", array.item()
                .getString(array.getString(1)));
        assertEquals("z", array.item()
                .getString(array.getString(3)));
        JsonString key = system().get("/?pagedKeys#0")
                .asString();
        assertEquals("mem:/?pagedKeys", key.item()
                .source() + "");
        assertEquals("1", key.stringValue());
        JsonArray array2 = system().get("?pagedKeys&numRows=1")
                .asArray();
        assertEquals(1, array2.size());
        assertEquals("1", array2.getString(0));
        JsonArray array3 = system().get("?pagedKeys&numRows=1&startRow=1")
                .asArray();
        assertEquals(1, array3.size());
        assertEquals("2", array3.getString(0));
    }

    @Test
    public void testCountItems() {
        system().put("1", "x");
        system().put("2", "y");
        system().put("j", "z");
        JsonNumber number = system().get("?countItems#count")
                .asNumber();
        assertEquals((Integer) 4, number.intValue());
        assertEquals("mem:/?countItems", number.item()
                .source() + "");
    }

    @Test
    public void testRemove() {
        system().put("/abc/def", "123");
        system().put("/abc/ghi", "456");
        assertEquals("123", system().getString("/abc/def"));
        assertEquals("456", system().getString("/abc/ghi"));
        system().remove("/abc");
        assertNull(system().get("/abc/def"));
        assertNull(system().get("/abc/ghi"));
        system().put("/abc#def", "123");
        system().put("/abc#ghi", "456");
        assertEquals(2, system().getObject("/abc")
                .size());
        assertEquals("123", system().getString("/abc#def"));
        system().remove("/abc#def");
        assertEquals(1, system().getObject("/abc")
                .size());
        assertEquals("456", system().getString("/abc#ghi"));
    }

    private String randomUrl() {
        StringBuffer sb = new StringBuffer("mem:/foo");
        //        for (int i = 0; i <= (Math.abs(random.nextInt()) % 20); ++i) {
        //            sb.append("/" + randomString());
        //        }
        sb.append("#" + randomString());
        for (int i = 0; i <= 1000; ++i) {
            sb.append("." + randomString());
        }
        return sb.toString();
    }

    @Test
    public void testClasspathGet() {
        JsonValue value = system().get("classpath:org/itemscript/test/test.json");
        assertEquals("bar", value.asObject()
                .getString("foo"));
    }

    @Test
    public void testCtor() {
        Assert.assertNotNull(system());
    }

    @Test
    public void testGet() {
        Assert.assertNotNull(system().get("mem:/itemscript"));
    }

    //    @Test
    //    public void testStress() {
    //        String url = randomUrl();
    //        System.err.println("url: " + url);
    //        system().put(url, randomString());
    //        System.err.println(system().get("mem:/foo")
    //                .toCompactJsonString());
    //        System.err.println(system().get(url)
    //                .fragment());
    //    }
    //
    @Test
    public void testMemPut() {
        system().put("mem:/one/two/three", system().createArray()
                .a("foo"));
        Assert.assertEquals("foo", system().get("one/two/three#0")
                .asString()
                .stringValue());
        system().put("mem:/foo/bar/baz#one.two.three", "test");
        Assert.assertEquals("test", system().get("mem:/foo/bar/baz#one.two.three")
                .asString()
                .stringValue());
    }

    @Test
    public void testParse() {
        JsonObject obj = system().get("classpath:org/itemscript/test/test3.json")
                .asObject();
        assertNotNull(obj);
        assertEquals("value", obj.getString("string"));
        assertEquals((Boolean) true, obj.getBoolean("boolean"));
        assertTrue(obj.get("null")
                .isNull());
        assertEquals("1.3", obj.getFloat("number")
                .floatValue() + "");
        assertEquals((Integer) 1, obj.getArray("array")
                .getInt(0));
        assertEquals("value", obj.getObject("object")
                .getString("key"));
    }

    @Test
    public void testParseFail() {
        boolean failed = false;
        try {
            system().parse("fail!");
        } catch (ItemscriptError e) {
            failed = true;
        }
        Assert.assertTrue(failed);
    }

    @Test
    public void testPutReturn() {
        JsonValue val = system().put("mem:/one/two/three", system().createString("foo"));
        assertEquals("foo", val.stringValue());
        assertEquals("mem:/one/two/three", val.item()
                .source() + "");
        assertEquals("#", val.fragment());
    }

    @Test
    public void testQuery() {
        system().put("/foo/1", "foo");
        system().put("/foo/2", "bar");
        system().get("/foo?countItems#count", new GetCallback() {
            public void onError(Throwable e) {
                fail(e.getMessage());
            }

            public void onSuccess(JsonValue value) {
                assertEquals((Integer) 2, value.intValue());
            }
        });
    }

    @Test
    public void testQuerySync() {
        system().put("/foo/1", "foo");
        system().put("/foo/2", "bar");
        assertEquals((Integer) 2, system().getInt("/foo?countItems#count"));
        JsonValue val = system().get("/foo?countItems#count");
        assertEquals("mem:/foo?countItems", val.item()
                .source() + "");
    }

    @Test
    public void testRootItem() {
        assertTrue(system().get("mem:/") instanceof JsonNull);
    }

    @Test
    public void testUuidPut() {
        JsonValue value = system().put("/foo?uuid", "foo");
        Url source = Url.create(value.item()
                .source());
        assertEquals("/foo/", source.pathString()
                .substring(0, 5));
        String filename = source.filename();
        assertEquals(36, filename.length());
    }

    @Test
    public void testValidate() {
        system().get("classpath:org/itemscript/test/validate.json");
    }

    final static String basePath = "/d:/workspace/Itemscript/src/org/itemscript/test/";

    @Test
    public void testJsonFileGet() {
        JsonArray array = system().get("json-file:" + basePath + "?keys")
                .asArray();
        assertTrue(array.contains(system().createString("test.json")));
        JsonObject value = system().get("json-file:" + basePath + "test.json")
                .asObject();
        assertEquals("bar", value.getString("foo"));
        assertEquals("bar", system().getString("json-file:" + basePath + "test.json#foo"));
    }

    @Test
    public void testTextFileGet() {
        JsonArray array = system().get("text-file:" + basePath + "test.txt")
                .asArray();
        assertEquals("two", array.getString(1));
    }

    @Test
    public void testBinaryFileGet() {
        JsonString value = system().get("file:" + basePath + "test.txt")
                .asString();
        assertEquals('o', value.binaryValue()[0]);
        assertEquals('n', value.binaryValue()[1]);
        assertEquals('e', value.binaryValue()[2]);
    }

    static boolean getCompleted = false;
    static boolean putCompleted = false;
    static boolean removeCompleted = false;

    @Test
    public void testAsyncUseOfSyncConnector() {
        system().put("/foo", system().createString("bar"), new PutCallback() {
            @Override
            public void onSuccess(JsonValue value) {
                assertEquals("bar", value.stringValue());
                putCompleted = true;
            }

            @Override
            public void onError(Throwable e) {
                throw new RuntimeException(e);
            }
        });
        assertTrue(putCompleted);
        system().get("/foo", new GetCallback() {
            @Override
            public void onSuccess(JsonValue value) {
                assertEquals("bar", value.stringValue());
                getCompleted = true;
            }

            @Override
            public void onError(Throwable e) {
                throw new RuntimeException(e);
            }
        });
        assertTrue(getCompleted);
        system().remove("/foo", new RemoveCallback() {
            @Override
            public void onSuccess() {
                removeCompleted = true;
            }

            @Override
            public void onError(Throwable e) {
                throw new RuntimeException(e);
            }
        });
        assertTrue(removeCompleted);
        assertNull(system().get("/foo"));
    }

//    @Test
//    public void testHttpPut() {
//        JsonValue value = system().get("http://127.0.0.1:8888/test.json");
//        JsonValue ret = system().put("http://127.0.0.1:8888/ReflectJson", value);
//        system().remove("http://127.0.0.1:8888/ReflectJson");
//    }
}