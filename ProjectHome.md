<p align='center'><a href='http://itemscript.org/'><img src='http://itemscript.org/images/itemscript-logo-200px-wide.png' /></a></p>

This is the **Itemscript JSON Toolkit** for standard Java and GWT Java.

Main components:
  1. A cross-platform GWT & standard Java [JSON](http://json.org) [library](http://itemscript.googlecode.com/svn/trunk/itemscript/doc/org/itemscript/core/values/package-summary.html), with convenient  classes, parsers, and utilities.
  1. A [RESTful](http://en.wikipedia.org/wiki/Representational_State_Transfer) connector API for retrieval of data (JSON, text & small binary files) over a variety of protocols.
  1. A simple in-memory database with a RESTful interface, useful as a mock server for testing & development, and for managing application state.
  1. A validator for the Itemscript Schema JSON schema language.
  1. The JAM template language, using JSON values, usable in both GWT & standard Java.

Please feel free to log any issues, or contact us directly with questions or comments.

### News ###

  * 1.0.0 release - July 29th, 2010 - Added schema and template modules, numerous bug fixes, some minor incompatible interface changes with previous versions.
  * 0.9.11 release - March 26th, 2010 - Content-type bug-fixes for HTTP connectors; added HTML pretty-printing of JSON values to `StaticJsonUtil`; `pagedKeys` and `pagedItems` queries to `mem:` URLs now support `orderBy` and `ascending` parameters for sorting results by field or in reverse order; adding an event handler now returns a `HandlerReg` that can be used to remove the `Handler` later.

### 1. JSON library features ###

  * The same JSON API can be used in both standard Java and in GWT Java.
  * In standard Java the JSON parser is derived from the [org.json.simple](http://code.google.com/p/json-simple/) parser by FangYidong.
  * In GWT Java the browser-native `JSON.parse()` is used where available, or `eval()` if not.
  * `JsonObject` implements `Map<String,JsonValue>`.
  * `JsonArray` implements `List<JsonValue>`.
  * Typed get and put methods for containers that convert to & from Java types - e.g. `getString(String key)`, `put(String key, Long value)`.
  * Get methods for containers that throw exceptions when values are missing or of the wrong type - e.g. `getRequiredString()`.
  * Check methods for containers that test for existence and type - e.g. `hasString()`
  * Stores Java `longs` safely in `JsonString` values.
  * Incorporates transparent/lazy base64 encoding & decoding for storing binary values in `JsonString` values.
  * Chainable put & add methods for containers, allowing for inline initialization - `p(key, value)` for objects, `a(value)` for arrays.
  * Convenience methods for creating an object or array inside another object or array.
  * Event handlers allow for actions to be taken when a value changes.
  * Enforced consistency relationships: a value may only be in one container; cycles in parent relationships (X->Y->X) are not permitted.

## 2. RESTful data retrieval API ##

This provides a simple, extensible RESTful interface to data storage. The [API](http://itemscript.googlecode.com/svn/trunk/itemscript/doc/org/itemscript/core/JsonSystem.html) consists of Map-style `get/put/delete` operations that accept URLs as keys; they allow you to treat the web as a hashtable with URLs for keys. For GWT, asynchronous versions of these operations that accept callbacks are provided for retrieval via HTTP or JSONP.

In GWT Java there are connectors for:
  * `http:` for same-domain REST operations.
  * `jsonp:` for cross-domain requests.
  * `file:` URLs for testing locally.
  * `cookie:` for simple access to cookies.
  * `mem:` for in-memory storage & server mock-ups.

In standard Java there are connectors for:
  * `http:` & `file:` URLs.
  * `mem:` for in-memory storage.
  * `classpath:` for loading bundled resources.

This API is easily extensible using the pluggable [connector](http://itemscript.googlecode.com/svn/trunk/itemscript/doc/org/itemscript/core/connectors/package-summary.html) interface.

### 3. In-memory RESTful database ###

This simple database provides storage and retrieval of resources via HTTP-style hierarchical URLs. It is intended primarily for mocking-up & testing of REST applications without needing to configure a server, and so it imitates the operations a typical REST server might provide.

It can also be used as a configuration database, a cache, or to mediate interactions between various sub-systems in an application; sub-systems may attach event handlers to resources and be notified when they change, allowing decoupling of interactions. It can be used in both browser and standard Java environments.

[Getting started guide to the in-memory database](GettingStartedRESTfulJSONDatabase.md)

### 4. Itemscript Schema validator ###

The toolkit includes a validator for the [Itemscript Schema](ItemscriptSchema.md) language, allowing you to validate JSON values against types defined using that language, usable for both immediate client-side validation and server-side validation.

### 5. JAM template language ###

The [JAM](JAMtemplates.md) template language allows you to use JSON values to interpret templates; templates can produce text or JSON values as output. The template language can be efficiently parsed and compiled even in browser environments.


### Examples ###

Examples of many of these features can be found [here](http://code.google.com/p/itemscript/source/browse/trunk/itemscript/src/org/itemscript/examples/Examples.java); a worked example of the `Foundry`/`Factory` system can be found [here](http://code.google.com/p/itemscript/source/browse/trunk/itemscript/src/org/itemscript/test/JsonFoundryTest.java).

### Getting started ###

[Getting started guide for standard Java](GettingStartedStandardJava.md)

[Getting started guide for GWT Java](GettingStartedGwtJava.md)

[Getting started with the in-memory database](GettingStartedRESTfulJSONDatabase.md)

### Other projects ###

The [JAM (JSON Application Markup)](JAMtemplates.md) language describes applications, interfaces, events and data in a simple language based on JSON name:value pairs.  JAM extends the Itemscript specification.

The [Web Data Model](WebDataModel.md) is a description of the implicit data model of the RESTful web; the in-memory database in Itemscript tries to emulate a simple, standard set of operations on it.

Some notes on [making a Java library that can be used both in standard Java environments and GWT](MakingACrossPlatformStandardJavaGwtJavaLibrary.md).

Read the [FAQ](http://code.google.com/p/itemscript/wiki/itemscriptfaq) for more information.

http://itemscript.org/.

Itemscript is a registered trademark of Database Architects, Inc.  Itemscript is an open source work published under the new BSD license.