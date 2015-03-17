The Itemscript library exposes only a few methods to JavaScript allowing basic back-and-forth communication between JS and Itemscript.

## Include the Itemscript library ##

Inside the itemscript-javascript-x.x.x.zip archive is a directory called `org.itemscript.Itemscript`. Copy this directory and its contents to the same directory as your HTML file.

Then include the script file with a line like this:
```
<script type="text/javascript" language="javascript"
        src="org.itemscript.Itemscript/org.itemscript.Itemscript.nocache.js"></script>
```

## The Itemscript API for JavaScript ##

These are the functions made available in JavaScript by the library:

  * `itemscript.get`
  * `itemscript.getAsync`
  * `itemscript.put`
  * `itemscript.putAsync`
  * `itemscript.remove`
  * `itemscript.removeAsync`
  * `itemscript.copy`
  * `itemscript.copyAsync`

The versions suffixed with "Async" operate asychronously; unless you know that the call will return immediately (e.g. that it is to a `mem:` or `cookie:` URL) the Async versions are probably the ones you need to use, as any attempt to store or retrieve using HTTP must be performed asynchronously.

## `itemscript.get` ##

Example:
```
var cat = itemscript.get("mem:/cat");
```

Takes one argument - the URL to retrieve - and returns a JavaScript value with a copy of the resource referred to. You can use a fragment identifier to retrieve only part of a value:

```
var name = itemscript.get("mem:/cat#name");
```

Values returned will be one of: a JavaScript object, a JavaScript array, a string, a number, a boolean, or null.

## `itemscript.getAsync` ##

Example:
```
    var url = "http://127.0.0.1:8888/test.json"
    var callback =  {
        "onSuccess" : function(value) {
            window.alert("getAsync url: " + url + " value: " + JSON.stringify(value));
	    },
	    "onError" : function(exception) {
	        window.alert("error: " + JSON.stringify(exception));
	    }
	};
    itemscript.getAsync(url, callback);
```

Takes two arguments. The first is the URL to retrieve; the second is a JavaScript object containing the callback functions to call when the get operation completes. The field "onSuccess" must contain a function that will be called with a single argument containing the value returned when the operation completes successfully. The field "onError" must contain a function that will be called with a single argument containing a JavaScript object representation of an exception when the operation encounters an error.


---


See also the README.txt file included in the JavaScript ZIP file, and for some examples: [javascript-example.html](http://code.google.com/p/itemscript/source/browse/trunk/itemscript/war/javascript-example.html)


Itemscript is a registered trademark of Data Base Architects, Inc. The Itemscript specification, the Itemstore API specification and the JAM template language specification are open source works published under the new BSD license.