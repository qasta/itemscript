To use the Itemscript library with JavaScript, include the org.itemscript.Itemscript
directory and its contents in your web project.

Then include the library with a script tag like this:

<script type="text/javascript" language="javascript"
    src="org.itemscript.Itemscript/org.itemscript.Itemscript.nocache.js"></script>

Now, in your code, you can use these methods to access the Itemscript system:

  itemscript.get(url)
  itemscript.getAsync(url, callback)
  itemscript.put(url, value)
  itemscript.putAsync(url, value, callback)
  itemscript.remove(url)
  itemscript.removeAsync(url, callback)
  
The argument "callback" should be a JavaScript object with two fields "onSuccess"
and "onError", for instance:

    var callback =  {
            "onSuccess" : function(value) {
                window.alert("value: " + JSON.stringify(value));
            },
            "onError" : function(exception) {
                window.alert("error: " + JSON.stringify(exception));
            }
        };

If the request succeeds, the "onSuccess" function will be called.
For get calls, "value" will be the actual value retrieved.
For put and remove calls, "value" will be a JavaScript object containing information
about the request.

When an error occurs, the "onError" function will be called.
"exception" will be a JavaScript object containing information about the failure.

You should check to make sure the global itemscript object is available before using any of these
calls, as it may take a little while to initialize.

The JavaScript interface to this library is experimental. Please report bugs, issues, and suggestions
at the project site:

  http://code.google.com/p/itemscript/