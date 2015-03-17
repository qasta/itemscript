# Introduction #

Making Java code that can be compiled in both the GWT and standard Java environments can be a bit of a challenge. Here are a few simple things I learned while writing the Itemscript JSON library.

## The problems ##

  * Code that will be compiled by GWT cannot refer to any class or interface that is not emulated in GWT.
  * Code that will be compiled by GWT cannot refer to any other code that is outside of a GWT client package. (Really the same problem as the above.)
  * Standard Java environments will not have the GWT libraries available and cannot run any code from the GWT package.
  * Standard Java environments cannot run JSNI.
  * GWT does not emulate many standard, common Java interfaces like Reader or InputStream.
  * GWT has classes that substitute for certain core Java classes - for instance, related to random number generation - but they don't implement the exact same interface.

At first glance, these would seem to reduce you to a very limited, lowest-common-denominator library that cannot use many of the standard Java interfaces and therefore is of very limited use; or two completely different codebases for a library for the standard and GWT versions. But it doesn't have to be that bad.

## Some solutions ##

### Divide & conquer ###

Plan to divide your code into three segments. One segment should contain the core functionality of your library to be shared between both the standard-Java and GWT environments. Another segment should contain everything for the standard-Java environment and all the references to standard Java interfaces. The third segment should contain everything for the GWT-Java environment and all the references to GWT interfaces.

The first segment - the shared one - will be a GWT module, but won't contain any JSNI or any GWT-specific code, nor will it contain any references to standard-Java interfaces, nor can it contain any "outward" references to the other two segments. The other two segments can contain "inward" references to the shared segment API, but not to each other.

In the Itemscript project, those packages are as follows:

  * Shared - [org.itemscript.core](http://code.google.com/p/itemscript/source/browse/#svn/trunk/itemscript/src/org/itemscript/core)
  * Standard-Java - [org.itemscript.standard](http://code.google.com/p/itemscript/source/browse/#svn/trunk/itemscript/src/org/itemscript/standard)
  * GWT-Java - [org.itemscript.core.gwt](http://code.google.com/p/itemscript/source/browse/#svn/trunk/itemscript/src/org/itemscript/core/gwt)

Note that in this case the GWT-Java package is a sub-package of the shared package. This is to slightly simplify the process of including the itemscript GWT module by making the GWT-specific code part of the same module. It could just as easily be outside of the `core` package and included as a referenced module by that package. On the other hand, the standard-Java package _must_ be outside of the `core` package because otherwise GWT will complain that it cannot find the referenced standard-Java interfaces when it tries to compile that module. (Because `org.itemscript.core` is a GWT `client` package.)

### Use a configuration interface to supply the platform-specific functions to your library ###

Of course, the shared library is likely to need to interact with platform specific code. But it can't make "outward" references to it. So how does it get to it? The answer is to write an interface (or probably more than one) that wraps all the platform-specific features in a generic fashion. This interface still cannot refer to any of the platform-specific classes or interfaces, though, but the implementation of the interface can.

This interface is then implemented in a platform-specific fashion, and supplied to the library during initialization.

In the Itemscript project, the main configuration interfaces are [JsonConfig](http://code.google.com/p/itemscript/source/browse/trunk/itemscript/src/org/itemscript/core/config/JsonConfig.java) and [JsonCreator](http://code.google.com/p/itemscript/source/browse/trunk/itemscript/src/org/itemscript/core/values/JsonCreator.java). The `JsonConfig` interface is a required initialization argument when creating a new `ItemscriptSystem`, in other words when initializing the library. `JsonConfig` wraps a few fairly trivial functions whose implementation differs from one platform to another, for instance, getting a new random int value, and generating a UUID. `JsonCreator` wraps the platform-native JSON parser (`JSON.parse` or `eval` in GWT, a JFlex parser in standard Java).

### Substitute Object for platform-specific interfaces or classes in the core API ###

Just separating out the platform-specific parts may not be enough. You may need to provide a method in the core API that requires a platform-specific interface be supplied. In those cases, you have to do what would normally not be a great practice: specifying the type of that argument as `Object`. Naturally, that method will not work on the platform where that interface is not available - but it will at least compile there.

For instance, in the Itemscript project, one important feature is the ability to parse JSON that comes in a `Reader` as well as in a `String`. GWT does not contain the `Reader` interface, so the shared core library cannot directly refer to `Reader`, but we want to have a method available alongside `parse(String json)` on `JsonSystem` that will parse a `Reader`.

So, we have a method on `JsonSystem` with the signature `parseReader(Object reader)`. This method just calls the method `parseReader(Object reader)` on `JsonCreator`; for the GWT implementation, we just throw an unsupported operation exception. For the standard Java implementation, we just have to cast the `Object` argument to `Reader`, then supply it a method on the JSON parser that expects a `Reader`. It's not too pretty, but it works.

### Provide a registry for platform-specific extensions ###

For some kinds of functionality, you want to be able to allow a fairly wide range of platform-specific behavior, but you don't want to have to write all of that into the core library API. So, the common extension pattern can help a lot here by allowing you to  keep all of the platform-specific code in extension modules that are plugged in when the library is initialized. This can allow you to provide very different functions on different platforms without importing the platform-specific APIs into your core library.

In the Itemscript project we do this with the various `Connector` interfaces in [org.itemscript.core.connectors](http://code.google.com/p/itemscript/source/browse/#svn/trunk/itemscript/src/org/itemscript/core/connectors) that provide a generic interface for loading data from various sources. During initialization of the library in each environment, the correct `Connectors` are created and stored in a registry under the URL schemes that they correspond to.  For instance, in the GWT environment the HTTP connector is implemented using the GWT library's Request functions that call XmlHttpRequest; in the standard Java environment the HTTP connector is implemented using the native URLConnection interface, and various other connectors for files and classpath resources are also provided.

### Exclude GWT-specific files and classes from the standard-Java JAR ###

To keep your standard-Java JAR safe for non-GWT environments, you should keep all of the GWT-specific code out of it, and provide a different JAR for GWT users. You don't need to worry too much about excluding standard-Java APIs from the latter; it's perfectly OK for the GWT-Java JAR to contain standard-Java-specific interfaces and libraries, as long as none of them are referred to inside either the core segment or the GWT-specific segment of your code.

For instance, in the Itemscript project we build two different JARs with ant like this:

```
    <target name="gwt-jar" description="Create the itemscript-gwt JAR file">
        <jar destfile="dist/itemscript-gwt-${version}.jar" basedir="war/WEB-INF/classes" excludes="**/test/**,**/examples/**">
            <fileset dir="src/" excludes="**/Itemscript.gwt.xml,**/examples/**,**/test/**" />
        </jar>
    </target>
    <target name="standard-jar" description="Create the itemscript-standard JAR file">
        <jar destfile="dist/itemscript-standard-${version}.jar" basedir="war/WEB-INF/classes" excludes="**/test/**,**/examples/**,**/gwt/**,**/Itemscript.gwt.xml" />
    </target>
```

The standard JAR contains no GWT-specific-classes and none of the gwt.xml files, making it safe to use in an environment where the GWT libraries are not installed. The GWT JAR can be used both on the server side and to compile GWT code for the client side.

The other advantage of two JARs is that the standard JAR does not need to contain the source code, whereas the GWT JAR does. You can see that in the `<fileset>` line in the above ant script.

Itemscript is a registered trademark of Data Base Architects, Inc. The Itemscript specification, the Itemstore API specification and the JAM template language specification are open source works published under the new BSD license.