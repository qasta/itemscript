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
import org.itemscript.core.events.Event;
import org.itemscript.core.events.EventType;
import org.itemscript.core.events.Handler;
import org.itemscript.core.exceptions.ItemscriptError;
import org.itemscript.core.url.Url;
import org.itemscript.core.values.JsonArray;
import org.itemscript.core.values.JsonItem;
import org.itemscript.core.values.JsonNull;
import org.itemscript.core.values.JsonNumber;
import org.itemscript.core.values.JsonObject;
import org.itemscript.core.values.JsonString;
import org.itemscript.core.values.JsonValue;
import org.itemscript.core.values.PutResponse;
import org.itemscript.core.values.RemoveResponse;
import org.junit.Assert;
import org.junit.Test;

public class JsonSystemTest extends ItemscriptTestBase {
    final static String basePath = "/d:/workspace/Itemscript/src/org/itemscript/test/";
    //    @Test
    //    public void testTextFileGet() {
    //        JsonArray array = system().get("file:" + basePath + "test.txt")
    //                .asArray();
    //        assertEquals("two", array.getString(1));
    //    }
    //    @Test
    //    public void testBinaryFileGet() {
    //        JsonString value = system().get("file:" + basePath + "test.txt")
    //                .asString();
    //        assertEquals('o', value.binaryValue()[0]);
    //        assertEquals('n', value.binaryValue()[1]);
    //        assertEquals('e', value.binaryValue()[2]);
    //    }
    static boolean getCompleted = false;
    static boolean putCompleted = false;
    static boolean removeCompleted = false;
    private Random random = new Random();
    private char[] letters = new char[26];
    {
        for (char l = 'a'; l <= 'z'; ++l) {
            letters[l - 'a'] = l;
        }
    }

    private JsonArray createRandomArray(int maxDepth, int depth) {
        JsonArray array = system().createArray();
        if (depth < maxDepth) {
            int childNodes = Math.abs(random.nextInt()) % 10;
            for (int i = 0; i < childNodes; ++i) {
                array.add(createSomething(maxDepth, depth + 1));
            }
        }
        return array;
    }

    private JsonObject createRandomObject(int maxDepth, int depth) {
        JsonObject object = system().createObject();
        if (depth < maxDepth) {
            int childNodes = Math.abs(random.nextInt()) % 10;
            for (int i = 0; i < childNodes; ++i) {
                object.put(randomString(), createSomething(maxDepth, depth + 1));
            }
        }
        return object;
    }

    private JsonValue createSomething(int maxDepth, int depth) {
        int what = Math.abs(random.nextInt()) % 6;
        switch (what) {
            case 0 :
                return createRandomObject(maxDepth, depth);
            case 1 :
                return createRandomArray(maxDepth, depth);
            case 2 :
                return system().createNull();
            case 3 :
                return system().createBoolean(random.nextBoolean());
            case 4 :
                return system().createString(randomString());
            case 5 :
            default :
                return system().createNumber(random.nextDouble());
        }
    }

    private String randomString() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i <= (Math.abs(random.nextInt()) % 20); ++i) {
            sb.append(letters[Math.abs(random.nextInt()) % 26]);
        }
        return sb.toString();
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
    public void testAsyncUseOfSyncConnector() {
        system().put("/foo", system().createString("bar"), new PutCallback() {
            @Override
            public void onError(Throwable e) {
                throw new RuntimeException(e);
            }

            @Override
            public void onSuccess(PutResponse p) {
                assertEquals("bar", p.value()
                        .stringValue());
                putCompleted = true;
            }
        });
        assertTrue(putCompleted);
        system().get("/foo", new GetCallback() {
            @Override
            public void onError(Throwable e) {
                throw new RuntimeException(e);
            }

            @Override
            public void onSuccess(JsonValue value) {
                assertEquals("bar", value.stringValue());
                getCompleted = true;
            }
        });
        assertTrue(getCompleted);
        system().remove("/foo", new RemoveCallback() {
            @Override
            public void onError(Throwable e) {
                throw new RuntimeException(e);
            }

            @Override
            public void onSuccess(RemoveResponse r) {
                removeCompleted = true;
            }
        });
        assertTrue(removeCompleted);
        assertNull(system().get("/foo"));
    }

    @Test
    public void testB64idPut() {
        PutResponse response = system().put("/foo?b64id", "foo");
        Url source = system().util()
                .createUrl(response.url());
        assertEquals("/foo/", source.pathString()
                .substring(0, 5));
        String filename = source.filename();
        assertEquals(22, filename.length());
        JsonValue get = system().get(response.url());
        assertEquals(response.url(), get.item()
                .source());
    }

    //    @Test
    //    public void testHttpPut() {
    //        JsonValue value = system().get("http://127.0.0.1:8888/test.json");
    //        JsonValue ret = system().put("http://127.0.0.1:8888/ReflectJson", value);
    //        system().remove("http://127.0.0.1:8888/ReflectJson");
    //    }
    @Test
    public void testClasspathGet() {
        JsonValue value = system().get("classpath:org/itemscript/test/test.json");
        assertEquals("bar", value.asObject()
                .getString("foo"));
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
    public void testCreate() {
        JsonObject object = createRandomObject(15, 0);
        String string = object.toJsonString();
        JsonValue parsed = system().parse(string);
        assertNotNull(parsed);
    }

    @Test
    public void testCtor() {
        Assert.assertNotNull(system());
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
    public void testFileGet() {
        JsonArray array = system().get("file:" + basePath + "?keys")
                .asArray();
        assertTrue(array.contains(system().createString("test.json")));
        JsonObject value = system().get("file:" + basePath + "test.json")
                .asObject();
        assertEquals("bar", value.getString("foo"));
        assertEquals("bar", system().getString("file:" + basePath + "test.json#foo"));
        String textValue = system().getString("file:" + basePath + "test.txt");
        assertTrue(textValue.startsWith("one"));
        JsonString imageValue = system().get("file:" + basePath + "test.png")
                .asString();
        assertNotNull(imageValue);
    }

    @Test
    public void testGet() {
        Assert.assertNotNull(system().get("mem:/itemscript"));
    }

    @Test
    public void testHttpGet() {
        JsonObject value = system().get("http://itemscript.org/test.json")
                .asObject();
        assertEquals("value", value.getString("test-string"));
        assertEquals("value", system().getString("http://itemscript.org/test.json#test-string"));
        String textValue = system().getString("http://itemscript.org/test.txt");
        assertTrue(textValue.startsWith("one"));
        JsonString imageValue = system().get("http://itemscript.org/test.png")
                .asString();
        assertNotNull(imageValue);
    }

    @Test
    public void testHttpMeta() {
        JsonValue val = system().get("http://itemscript.org/test.json");
        assertNotNull(val.item()
                .meta()
                .getString("Content-Type"));
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
        // reverse sort.
        JsonArray array4 = system().get("?pagedKeys&ascending=false")
                .asArray();
        assertEquals("z", array4.item()
                .getString(array4.getString(0)));
        // skip "itemscript" entry...
        assertEquals("y", array4.item()
                .getString(array4.getString(2)));
        assertEquals("x", array4.item()
                .getString(array4.getString(3)));
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
        JsonValue val = system().put("mem:/one/two/three", system().createString("foo"))
                .value();
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

    @Test
    public void testRootItem() {
        assertTrue(system().get("mem:/") instanceof JsonNull);
    }

    @Test
    public void testUuidPut() {
        JsonValue value = system().put("/foo?uuid", "foo")
                .value();
        Url source = system().util()
                .createUrl(value.item()
                        .source());
        assertEquals("/foo/", source.pathString()
                .substring(0, 5));
        String filename = source.filename();
        assertEquals(36, filename.length());
        PutResponse putResponse = system().put("/bar?uuid", "bar");
        Url source2 = system().util()
                .createUrl(putResponse.url());
        assertEquals("/bar/", source2.pathString()
                .substring(0, 5));
        String filename2 = source2.filename();
        assertEquals(36, filename2.length());
    }

    @Test
    public void testValidate() {
        system().get("classpath:org/itemscript/test/validate.json");
    }

    @Test
    public void testOrderBy() {
        JsonObject o1 = system().createObject()
                .p("x", "a");
        JsonObject o2 = system().createObject()
                .p("x", "b");
        JsonObject o3 = system().createObject()
                .p("x", "c");
        system().put("/Sort/?b64id", o1);
        system().put("/Sort/?b64id", o2);
        system().put("/Sort/?b64id", o3);
        JsonArray array = system().getArray("/Sort/?pagedItems&numRows=3&orderBy=x");
        assertEquals(3, array.size());
        assertEquals("a", array.getArray(0)
                .getObject(1)
                .getString("x"));
        assertEquals("b", array.getArray(1)
                .getObject(1)
                .getString("x"));
        assertEquals("c", array.getArray(2)
                .getObject(1)
                .getString("x"));
        JsonArray array2 = system().getArray("/Sort/?pagedItems&numRows=3&orderBy=x&ascending=false");
        assertEquals(3, array2.size());
        assertEquals("c", array2.getArray(0)
                .getObject(1)
                .getString("x"));
        assertEquals("b", array2.getArray(1)
                .getObject(1)
                .getString("x"));
        assertEquals("a", array2.getArray(2)
                .getObject(1)
                .getString("x"));
    }

    private static boolean nestedRemoveHandlerCalled1 = false;
    private static boolean nestedRemoveHandlerCalled2 = false;

    @Test
    public void testNestedRemoveHandler() {
        system().put("/a/b/c", "x");
        JsonItem item = system().get("/a/b/c")
                .item();
        item.addHandler(new Handler() {
            @Override
            public void handle(Event event) {
                if (event.eventType()
                        .equals(EventType.REMOVE)) {
                    if (event.fragment()
                            .equals("#")) {
                        nestedRemoveHandlerCalled1 = true;
                    }
                }
            }
        });
        JsonItem item2 = system().get("/a/b")
                .item();
        item2.addHandler(new Handler() {
            @Override
            public void handle(Event event) {
                if (event.eventType()
                        .equals(EventType.REMOVE)) {
                    if (event.fragment()
                            .equals("#")) {
                        nestedRemoveHandlerCalled2 = true;
                    }
                }
            }
        });
        system().remove("/a");
        assertTrue(nestedRemoveHandlerCalled1);
        assertTrue(nestedRemoveHandlerCalled2);
    }
}