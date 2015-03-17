# The Itemstore API specification #

The Itemstore specification provides a simple RESTful API to JSON values stored in memory or persisted to a data source.  Read [this](http://code.google.com/p/itemscript/wiki/CreateAConnector) for information on writing a connector.

The Itemscript JSON library provides an in-memory database that implements the Itemstore API with a REST-style interface. Items in the database are accessed using hierarchical URLs, as are queries.

The API supports three basic operations:

  * `get`
  * `put`
  * `remove`

These correspond to the REST/HTTP methods `GET`, `PUT`, and `DELETE`. The same API can be used to access values via HTTP or other protocols.

The result of `get` and `put` operations is always a `JsonValue` (unless an exception is thrown). `remove` does not return a result. All the methods are idempotent; that is, repeating an operation has the same result. (The one exception is that `put` operations with a URL that contain a query string are mapped to HTTP `POST` operations and may not always return the same result.)

For a description of the abstract data model that we're attempting to emulate, see [WebDataModel](WebDataModel.md).

## Create a `JsonSystem` ##

See one of the general Getting Started pages: GettingStartedStandardJava for a standard Java environment, or GettingStartedGwtJava for the GWT Java environment.

We're going to use the `StandardConfig` here; if you're using GWT, just use the `GwtSystem.SYSTEM` instance. The database facilities are identical in either environment.

```
    JsonSystem system = StandardConfig.createSystem();
```

## Store some values ##

Any type of `JsonValue` can be stored, not just `JsonObjects`. You can also store native Java objects.

If no scheme is supplied to `put` or `get`, the supplied URL is considered to be relative to `mem:/`. So, `/Test/a` will end up being `mem:/Test/a`.

```
        system.put("/Test/a", "A string value");
        system.put("/Test/b", 123);
        system.put("/Test/c", false);
        system.put("/Test/d", system.createObject()
                .p("x", "y"));
        system.put("/Test/e", system.createArray()
                .a("one")
                .a("two")
                .a("three"));
        system.putNative("/Test/f", new Date());
```

## Retrieve some values ##

We'll use some of the null-safe conversion methods to access the values we just stored.

```
        String stringValue = system.getString("/Test/a");
        System.out.println("stringValue: " + stringValue);
        int intValue = system.getInt("/Test/b");
        System.out.println("intValue: " + intValue);
        boolean booleanValue = system.getBoolean("/Test/c");
        System.out.println("booleanValue: " + booleanValue);
        JsonObject objectValue = system.getObject("/Test/d");
        System.out.println("objectValue: " + objectValue);
        JsonArray arrayValue = system.getArray("/Test/e");
        System.out.println("arrayValue: " + arrayValue);
        Date dateValue = (Date) system.getNative("/Test/f");
        System.out.println("dateValue: " + dateValue);
```

prints:

```
stringValue: xyz
intValue: 123
booleanValue: false
objectValue: {
    "x" : "y"
}

arrayValue: [
    "one",
    "two",
    "three"
]

dateValue: Thu Mar 04 17:45:16 PST 2010
```

## Automatically create intermediate database items ##

We actually relied on this feature above, but this is an explicit example. The following fragment creates the items "/Test/one", "/Test/one/two", and "/Test/one/two/three", then sets the value "qwerty" in the node "/Test/one/two/three".

```
        system.put("/Test/one/two/three", "qwerty");
```

## Retrieve some values using fragments ##

You can supply a fragment to the URL being retrieved, and it will be used to navigate the value retrieved from the database. (You can also use this when loading data from a non-`mem:` scheme URL, since it relates strictly to navigating the structure.)

```
        String objectStringValue = system.getString("/Test/d#x");
        System.out.println("objectStringValue: " + objectStringValue);
        String arrayStringValue = system.getString("/Test/e#2");
        System.out.println("arrayStringValue: " + arrayStringValue);
```

prints:

```
objectStringValue: y
arrayStringValue: three
```

Of course, if the value is not an object or an array, supplying a fragment is an error, since they don't have any internal structure to navigate.

## Store a new value using a fragment ##

Note that when a value is put with a fragment when the item did not already exist, the item is created with a `JsonObject` value and the value supplied to put is set inside that object using the fragment. The fragment is divided into separate keys by slashes, those keys are URL-decoded, and the resulting list of keys is used to navigate inside the object. Missing intermediate values inside the object are created as new `JsonObjects` too.

```
        system.put("/Test/g#foo/bar", "xyz");
        JsonObject fragmentPutObject = system.getObject("/Test/g");
        System.out.println("fragmentPutObject: " + fragmentPutObject);
```

prints:

```
fragmentPutObject: {
    "foo" : {
        "bar" : "xyz"
    }
}
```

You can only `put` a value with a fragment URL to a `mem:` scheme URL; for any other scheme, you must `put` the entire new value for a resource, not just one sub-part, since fragments apply only to navigating local resources.

## Query the database ##

The `mem:` scheme allows certain kinds of queries to be performed on the database.

```
       int count = system.getInt("/Test/?countItems");
       System.out.println("count: " + count);
```

prints:

```
count: 8
```

You can also get a list of the keys of the sub-items of an item in the database:

```
        JsonArray keys = system.getArray("/Test?keys");
        System.out.println("keys: " + keys);
```

prints:

```
keys: [
    "f",
    "g",
    "d",
    "e",
    "b",
    "c",
    "one",
    "a"
]
```

Or a sorted sub-set of those keys:

```
        JsonArray pagedKeys = system.getArray("/Test?pagedKeys&startRow=0&numRows=4");
        System.out.println("pagedKeys: " + pagedKeys);
```

prints:

```
pagedKeys: [
    "a",
    "b",
    "c",
    "d"
]
```

Or a sorted sub-set of the keys and the values of those items (useful for a paged table of results, for instance):

```
        JsonArray pagedItems = system.getArray("/Test?pagedItems&startRow=0&numRows=4");
        System.out.println("pagedItems: " + pagedItems);
```

prints:

```
pagedItems: [
    [
        "a",
        "xyz"
    ],
    [
        "b",
        123
    ],
    [
        "c",
        false
    ],
    [
        "d",
        {
            "x" : "y"
        }
    ]
]
```

Each of the items in the returned array is itself an array; each of those sub-arrays has as its first element the key of the sub-item, and as the second element the actual value of that sub-item.

You can retrieve paged results (either `pagedKeys` or `pagedItems`) sorted in descending order:

```
        JsonArray pagedKeys = system.getArray("/Test?pagedKeys&startRow=0&numRows=4&ascending=false");
        System.out.println("pagedKeys: " + pagedKeys);
```

prints:

```
pagedKeys: [
    "d",
    "c",
    "b",
    "a"
]
```

You can also supply a parameter `orderBy` to `pagedKeys` or `pagedItems` queries to sort by a particular field of those sub-items that have `JsonObject` values.

By implementing the same queries on an HTTP REST server, you can interchangeably switch between accessing a database locally using the `mem:` scheme, and a remote database using the `http:` scheme.

## Remove an entire item ##

You can remove an entire item (which also removes any sub-items of that item).

```
        system.put("/Test/removeMe", "foo");
        system.put("/Test/removeMe/subItem", "foo");
        system.remove("/Test/removeMe");
        String removedValue = system.getString("/Test/removeMe");
        System.out.println("removedValue: " + removedValue);
        String removedSubValue = system.getString("/Test/removeMe/subItem");
        System.out.println("removedSubValue: " + removedSubValue);
```

prints:

```
removedValue: null
removedSubValue: null
```

## Remove part of a value using a fragment ##

When dealing with `mem:` URLs in the in-memory database, you can supply a fragment to `remove` and remove just part of the value of an item, as long as it was a container - either an object or an array.

```
        system.put("/Test/array", system.createArray().a("x").a("y").a("z"));
        JsonArray array = system.getArray("/Test/array");
        System.out.println("array, pre-remove: " + array);
        system.remove("/Test/array#1");
        System.out.println("array, post-remove: " + array);
```

prints:

```
array, pre-remove: [
    "x",
    "y",
    "z"
]

array, post-remove: [
    "x",
    "z"
]
```

Note that the actual `JsonArray` value we retrieved from the database _is_ the value that is changed in the subsequent `remove` operation. The values returned from the database are "live" - you can change them directly.

## Store a value under a generated UUID ##

You can store a value under a UUID that will be generated randomly for you by supplying the query parameter `uuid` to a `put` operation. This is useful when your data has no natural key, or you want to make sure you don't overwrite any existing data with the same key - overwriting an existing value using `put` does not generate an error.

In order to determine what UUID was returned, you need to examine the result of the `put` operation. Values stored in the database have an associated `JsonItem` object, which has a method `source()` that returns the URL they are associated with.

```
        for (int i = 0; i < 10; ++i) {
            JsonValue uuidValue = system.put("/Test/?uuid", "xyz");
            System.out.println("i: " + i + " uuidValue.item().source(): " + uuidValue.item()
                    .source());
        }
```

prints, for example:

```
i: 0 uuidValue.item().source(): mem:/Test/927c599c-2f1f-44ba-9238-146178c45ae0
i: 1 uuidValue.item().source(): mem:/Test/647b7580-dbd3-4c51-9cf3-a53e8e24e16a
i: 2 uuidValue.item().source(): mem:/Test/d1a7c820-4cf9-4610-aee1-827e2c2b1466
i: 3 uuidValue.item().source(): mem:/Test/3e328a2e-ebde-460b-95d1-6f3901595363
i: 4 uuidValue.item().source(): mem:/Test/8b621cd3-3add-400b-8181-54ff388ae3fb
i: 5 uuidValue.item().source(): mem:/Test/8eebca9d-9373-4860-b25b-1030e927b530
i: 6 uuidValue.item().source(): mem:/Test/7567def8-e13e-4fa1-bac0-088d5590c112
i: 7 uuidValue.item().source(): mem:/Test/0c3ce92e-1b90-4999-9ac0-1ef8e4c6840e
i: 8 uuidValue.item().source(): mem:/Test/e674bbcd-5d75-43a6-ad1f-1c5d45e066bf
i: 9 uuidValue.item().source(): mem:/Test/1682f797-a7bc-47c1-911a-0c9861cfe136
```

You can also supply the query parameter `b64id` for a shorter, 22-character 128-bit random ID, which is recommended where shorter URLs are important.

## Dump and load parts of the database ##

You can dump parts of the database by adding the query-string "dump" to a base URL supplied to the `get()` method. This will copy the entire tree below the item pointed to by the URL into a `JsonObject` in a special format that includes both the value of that item under the key `value`, and under the key `subItems`, a map of the keys of the sub-items of that item to a dump of those items, and so on recursively.

You can then load a dump object created in this way by adding the query-string "load" to a base URL giving the place to begin the loading at, and supplying that URL and the dump object to a call to `put()`. The items listed in the dump will be added relative to that location.

When using a `?dump` URL, the values of the items under the given URL will be copied. This means that dumping cannot exactly duplicate an area that contains native objects, because native objects are not copied by the `copy()` method on `JsonValue`. The purpose of `dump` is to provide a serialized form of portions of the database, and since the Itemscript library does not attempt to serialize native values, it is not useful to copy their values.

Here's an example that adds some values to a section of the database under `/foo`, then dumps them, then loads that dump back into the system under the URL `/bar/one/two/three`. The structure created under `/foo` will be duplicated there.

The standalone code:
```
    public static void main(String[] args) {
        JsonSystem system = StandardConfig.createSystem();
        system.put("/foo", 1);
        system.put("/foo/a", 2);
        system.put("/foo/a/x", true);
        system.put("/foo/b", 3);
        JsonValue fooDump = system.get("/foo?dump");
        System.out.println("fullDump: " + fooDump);
        system.put("/bar/one/two/three?load", fooDump);
        JsonValue barDump = system.get("/bar?dump");
        System.out.println("barDump: " + barDump);
    }
```

prints:

```
fullDump: {
    "subItems" : {
        "b" : {
            "subItems" : {},
            "value" : 3
        },
        "a" : {
            "subItems" : {
                "x" : {
                    "subItems" : {},
                    "value" : true
                }
            },
            "value" : 2
        }
    },
    "value" : 1
}

barDump: {
    "subItems" : {
        "one" : {
            "subItems" : {
                "two" : {
                    "subItems" : {
                        "three" : {
                            "subItems" : {
                                "b" : {
                                    "subItems" : {},
                                    "value" : 3
                                },
                                "a" : {
                                    "subItems" : {
                                        "x" : {
                                            "subItems" : {},
                                            "value" : true
                                        }
                                    },
                                    "value" : 2
                                }
                            },
                            "value" : 1
                        }
                    },
                    "value" : {}
                }
            },
            "value" : {}
        }
    },
    "value" : {}
}
```


Itemscript is a registered trademark of Data Base Architects, Inc.  The Itemscript and Itemstore specifications are open source works published under the new BSD license.