## Add the JAR to your project's build path ##

Download the Itemscript distribution archive, extract it, and copy the JAR itemscript-standard-x.x.x.jar into your project, and add it to your build path.

## Create a new `JsonSystem` ##

In your code, create a new `JsonSystem`:

```
   JsonSystem system = StandardConfig.createSystem();
```

You may want to store this in a static variable or in a `ThreadLocal` if you want to keep it around. I generally keep it as an instance variable and pass it to every class that needs to deal with it in their constructor; that overhead is worthwhile, because I can use the in-memory database and ability to store native objects to get dependencies out to other parts of my code. If you already use a dependency injection framework, you could use that.

Creation of a `JsonSystem` is pretty lightweight, so in non-performance critical applications where you don't want to use it to share data between different parts of the application, you can just create a new `JsonSystem` where you need it.

Note that it is not thread-safe, so if you are going to use it in a multi-threaded application, you need to either synchronize access to an instance that would be shared between threads, or use a `ThreadLocal` to create a new instance for each thread.

## Create and populate a `JsonObject` ##

```
    JsonObject object = system.createObject();
    object.put("abc", "xyz");
    object.put("def", 123);
    object.put("ghi", true);
    JsonArray array = object.createArray("jkl");
    array.add("one");
    array.add(2);
    array.add(true);
```

## Convert a `JsonObject` to a JSON string ##

```
    String json = object.toString();
    System.out.println(json);
```

prints:

```
{
    "jkl" : [
        "one",
        2,
        true
    ],
    "abc" : "xyz",
    "ghi" : true,
    "def" : 123
}
```

## Parse a JSON string ##

```
    String json2 = "{\"foo\" : \"bar\"}";
    JsonObject object2 = system.parse(json2).asObject();
    String fooValue = object2.getString("foo");
    System.out.println(fooValue); // prints "bar"
```

## Parse a JSON file loaded from the classpath ##

```
    JsonValue value = system.get("classpath:org/itemscript/test/test3.json");
    System.out.println(value);
```

## Parse a JSON value loaded via HTTP ##

```
    JsonObject object = system.get("http://itemscript.org/test.json");
    System.out.println(object);
```

prints:

```
{
    "test-boolean" : true,
    "test-object" : {
        "abc" : "def",
        "foo" : [
            "x",
            "y",
            "z"
        ]
    },
    "test-int" : 1,
    "test-null" : null,
    "test-string" : "value",
    "test-array" : [
        "one",
        "two",
        "three",
        true,
        1,
        null,
        1.5,
        {
            "xyz" : "123"
        }
    ],
    "test-float" : 1.5
}
```

## Copy a value from HTTP to the in-memory database ##

```
    system.copy("http://itemscript.org/test.json", "mem:/Test/test");
    System.out.println(system.get("mem:/Test/test#test-string")); // prints "value"
```

## Query the in-memory database ##

```
    System.out.println(system.get("mem:/Test/?countItems").intValue()); // prints 1
```

## PUT a value to a URL ##

```
    PutResponse put = system.put("http://127.0.0.1:8888/service", value);
```


Itemscript is a registered trademark of Data Base Architects, Inc.  The Itemscript specification, the Itemstore API specification and the JAM template language specification are open source works published under the new BSD license.