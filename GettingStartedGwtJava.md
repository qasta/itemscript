## Add the JAR to your project's build path ##

Download the Itemscript distribution archive, extract it, and copy the JAR itemscript-gwt-x.x.x.jar into your project, and add it to your build path.

## Add the Itemscript module to your GWT module config ##

In your module's `.gwt.xml` configuration file, add the following line above your module's entry-point:

```
<inherits name="org.itemscript.Itemscript" />
```

This will allow you to use the Itemscript classes, and the entry-point for the Itemscript module will create a single static `JsonSystem` instance at `GwtSystem.SYSTEM`. Because the GWT environment is single-threaded, you probably don't need to create any new instances of `JsonSystem`, although if you need to, you can do so like this:

```
    JsonSystem system = new ItemscriptSystem(new GwtConfig());
```

## Create and populate a `JsonObject` ##

```
    JsonSystem system = GwtSystem.SYSTEM;
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

## Parse a JSON value loaded via HTTP ##

Note that in the GWT environment, you don't want to wait until a value loaded from HTTP is fully loaded before continuing execution. So, you need to supply a callback that will be invoked when the `get` operation completes.

```
    system.get("http://itemscript.org/test.json", new GetCallback() {
        @Override
        public void onSuccess(JsonValue value) {
            System.out.println(value);
        }

        @Override
        public void onError(Throwable e) {
            System.err.println("error: " + e);
        }
    });
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

## PUT a value via HTTP ##

```
 GwtSystem.SYSTEM.put("/path/to/value", value, new PutCallback() {
                    @Override
                    public void onError(Throwable e) {
                        throw new RuntimeException(e);
                    }

                    @Override
                    public void onSuccess(PutResponse putResponse) {
                        System.err.println("put complete");
                    }
                });
```


## POST a value via HTTP ##

Support for PUT is unfortunately not universal in older browsers. You can force a request to be a POST request by adding a query string, which the server should ignore.

This mechanism is probably going to change; a general way of detecting whether the browser supports PUT and DELETE and a standard encapsulation of PUT and DELETE requests in POST requests would be more useful, or just a flag that switches everything to PUT-in-POST/DELETE-in-POST.

```
 GwtSystem.SYSTEM.put("/path/to/value?post", value, new PutCallback() {
                    @Override
                    public void onError(Throwable e) {
                        throw new RuntimeException(e);
                    }

                    @Override
                    public void onSuccess(PutResponse putResponse) {
                        RootPanel.get()
                                .add(new HTML(StaticJsonUtil.toHtmlJson(putResponse.value())));
                    }
                });
```


Itemscript is a registered trademark of Data Base Architects, Inc. The Itemscript specification, the Itemstore API specification and the JAM template language specification are open source works published under the new BSD license.