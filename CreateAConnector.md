## Introduction ##

The `Connector` interface allows you to create new interfaces to other systems and access them using the Itemscript API. Itemscript associates a connector with a URL scheme, and uses that connector when asked to `get`, `put`, or `remove` a value with a URL beginning with that scheme. By implementing a new `Connector`, you can handle a new type of URL.

## GWT-only: synchronous or asynchronous? ##

In the GWT environment, you can specify a connector as being either synchronous or asynchronous. Synchronous connectors perform their operation before returning control to where they were invoked. Your program cannot do anything while it waits. Asynchronous connectors return immediately and perform their operations in the background, then call a supplied callback when the operation completes. The in-memory database under the `mem:` scheme is a synchronous connector; the HTTP connector is an asynchronous connector. The interfaces are similar, but the asynchronous versions take an additional callback argument and do not return any values. All synchronous connectors can also be used asynchronously; it just means that the callback gets called immediately.

In the standard Java environment, at least for now, all connectors are synchronous. Execution waits for the completion of the operation before continuing with the statement after the operation. So, if you're retrieving a value via HTTP, your program will wait until the retrieval has completed before continuing. If support for asynchronous operations in the standard Java environment is useful to you, implementation should be fairly straightforward although cross-thread synchronization could be an issue.

We'll assume a synchronous connector from now on; for an example of an asynchronous connector, see [GwtHttpConnector.java](http://code.google.com/p/itemscript/source/browse/trunk/itemscript/src/org/itemscript/core/gwt/GwtHttpConnector.java).

## Which interfaces? ##

There are three interfaces for a synchronous connector:

  * `[http://code.google.com/p/itemscript/source/browse/trunk/itemscript/src/org/itemscript/core/connectors/SyncGetConnector.java SyncGetConnector]`
  * `[http://code.google.com/p/itemscript/source/browse/trunk/itemscript/src/org/itemscript/core/connectors/SyncPutConnector.java SyncPutConnector]`
  * `[http://code.google.com/p/itemscript/source/browse/trunk/itemscript/src/org/itemscript/core/connectors/SyncPutConnector.java SyncBrowseConnector]`

For most applications, it's sensible to start with `SyncGetConnector`, which is the interface for retrieving values. We can then move on to `SyncBrowseConnector` (for systems where browsing makes sense) and/or `SyncPutConnector` (for systems where values can be stored).

## Implementing `SyncGetConnector` ##

This interface specifies only one method:

```
    public JsonValue get(Url url);
```

Here's how the [ResourceConnector](http://code.google.com/p/itemscript/source/browse/trunk/itemscript/src/org/itemscript/standard/ResourceConnector.java) implements it:

```
    @Override
    public JsonValue get(Url url) {
        return system().createItem(url + "", getResource(url))
                .value();
    }
```

The first thing to note is that while it creates a `JsonItem`, it actually returns a `JsonValue`. The `JsonValue` it returns is one that it newly created with the call to `getResource()`; that value was then attached to a `JsonItem` using `system().createItem()`, and then the `value()` method on `JsonItem` was used to return the actual value.

The `JsonItem` had its source field set to the URL that was supplied to `get()`, converted to a string with `+ ""`. Connectors must create a new `JsonItem` and set the source field on that item to be the URL they were retrieved from; they may also want to add some metadata to that item, although we don't in this case.

## Implementing `SyncPutConnector` ##

This interface specifies two methods:

```
    public JsonValue put(Url url, JsonValue value);

    public void remove(Url url);
```

Note that the `put` method returns a `JsonValue`. This is not necessarily the same value that was supplied in the arguments; it may be transformed by the process of being stored.