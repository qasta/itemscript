# What is Itemscript? #

The **Itemscript project** intends to provide a set of technologies and standard conventions for building applications from simple name:value pairs that direct Itemscript components.

**Itemscript** is a declarative provisioning language for JSON data.  Itemscript gives [JSON](http://json.org/) a language for expressing data, metadata, and applications.

Itemscript expresses design independent of technical implementation. Interfaces and the services that power them can iterate independently to improve agility and reduce time to solution.

# Frequently Asked Questions #

## Why use JSON instead of XML?  Wasn't XML designed to do this? ##

**XML** (eXtensible Markup Language) developed as a simplified expression of **SGML** (Standard Generalized Markup Language).  XML has gained wide adoption as a document interchange standard.  It was not designed as an application provisioning language though, and does not present a friendly interface to humans (except when you hide it from them).

XML parsing is an expensive overhead to **AJAX** style applications.  **JSON** was designed to work with Javascript.  Torn between the nested elegance of XML and the pedal to the metal feel of JSON, we decided to take the best of XML and apply it to JSON.

JSON is easy to write and understand and lends itself to declarative programming.

We think it is an excellent syntax for a declarative language that's understood by programmers, interface designers, browsers, column stores, relational databases and web services.  What's good for Javascript is good for all of us.

## What's needed to build applications using Itemscript? ##

At its most basic level, Itemscript provides a schema definition for JSON data.  If your applications are already handling JSON, you can use Itemscript to add a schema to your data.  This documents your data.  You can use the Itemscript validator to control what's moving through the tubes.

If you're building new applications, remodeling into AJAX style UIs, adding cloud services or building mashboards, the Itemscript JSON library provides a common API for Java and GWT development.

JAM (JSON Application Markup) extends Itemscript to express data, interfaces and events using a common language based on name:value pairs.  JAM templates are described [here](http://code.google.com/p/itemscript/wiki/JAMtemplates).

## What about my stuff that already works? ##

An "Itemscript system" is one that uses some or all of these building blocks.   An Itemscript system can interact with any component or service that knows how to read or write JSON, especially over RESTful connections.

It's possible to consume SOAP to build an Itemscript payload.  It's possible to provision interfaces built with HTML using an Itemscript system.  In these use cases, Itemscript provides a layer of isolation between the declarative functionality of the service or component and its internal complexity.  In that context, Itemscript enables the UI and the services it consumes to iterate independently of each other.

## What levels of use are available? ##

### Level 0 Marshalling JSON data ###

The lowest level of compliance is any system that uses JSON to marshall data from RESTful services and provision interfaces using Java or Javascript APIs.

A level 0 application might use no Itemscript technologies. It may simply connect to an Itemscript system or adopt an Itemscript schema without using any code.

Many existing systems that use JSON over REST are already available for integration.

### Level 1 Writing services with JSON payloads ###

A level 1 Itemscript system uses itemscript JSON libraries to process JSON values and expose JSON APIs either in Java or over a RESTful service.

If you have a system architecture that works for you, you can use Itemscript to expose JSON APIs that wrap its technical details and expose controls using itemscript declarations.

### Level 2 Managing JSON data ###

A level 2 Itemscript system uses Itemscript schema to describe its metadata.  It may also use the Itemscript protocol to persist items.

At this level, the Itemscript schema provides data validation and the Itemscript library provides an in memory object model and a RESTful proxy for data sources.

### Level 3 Managing rich web interactions ###

A level 3 Itemscript system uses an Itemscript object model to manage its internal workings. The application is entirely configured using JSON structures that are processed
by the Itemscript libraries.

At this level, the Itemscript system model may represent all your data as JSON structures. You are able to configure your application using JSON.

At level 3, the Itemscript system mediates communication between different parts of your system using a shared data space and event model.  At this level, the components of your system communicate without directly linking to one another.

## What are the benefits of using Itemscript? ##

With Itemscript, your application is dynamically configurable, your interfaces are extremely extensible, and your components are hot swappable.  Your cost of ownership will be lower and your responsiveness to change will be higher.

The Itemscript design exploits a loosely coupled architecture.  During  development, your datastores can be stubbed out to use simple services.  Your technical developers can  expose controls while hiding complexity.

This allows a wider group of people to contribute to application development efforts. Casual programmers can read and write Itemscript.

Instead of spending their time creating and compiling superficial variations of existing code, programmers can publish re-usable components that can be configured and connected using simple declarative statements, expressed in JSON.

Instead of changing code to remodel an interface, or generating new code to re-use a component, your interface developers will be composing from re-usable components.

## What are the pieces and parts of Itemscript? ##

### Itemscript JSON library ###

This is a Java library for working with JSON data.  It's designed to provide convenient services to JSON objects. There are convenience methods for null-safe conversion of stored values to native Java types. There are methods for easily creating sub-objects, and for accessing deeply-nested values by path.

This library is compatible with a conventional Java stack (for instance, web applications on an application server).  We developed it for use with Google's GWT, which compiles Java to concise Javascript for use in web browsers.

For advanced users, this library provides a sophisticated in-memory data model.  You can include connections to external persistent data sources or stores. You can attach event handlers to values.  This enables a loosely coupled relationship between the parts of your system mediated through the Itemscript data model.

You can even attach native Java values to parts of the data model. Many of the kinds of things you might do with a dependency-injection framework or JNDI can be done using the Itemscript library.

### Itemscript Schema specification ###

[Itemscript schema language](http://itemscript.org/ItemscriptSchema.html) is a simple standard way to describe JSON data. It aims to be a
human-readable, human-writable way to share the contents of a JSON structure.  In a loosely typed environment, it can also be used to validate data and catch errors.

Any schema language intends to support human-to-human communication. Domain experts should not need to be experts in a schema language to communicate their
understanding of the structure and content of shared information.

### JAM specification ###

[JAM (JSON Application Markup)](http://code.google.com/p/itemscript/wiki/JAMtemplates) is a simple standard way to describe content, events and interactions using Itemscript declarations.

### Item Store specification ###

[http://code.google.com/p/itemscript/w/edit/GettingStartedRESTfulJSONDatabase Itemstore is a RESTful API specification for JSON data.

Relational databases are not going away.  Legacy data is not going away.  Messaging is not going away.

The Itemstore API provides a simple agnostic interface to data sources that hides the complex technical details. This gives the interface designer the ability to mash disparate data into intelligent interfaces without need to master a separate API for each data source.

Software developers publish data sources that expose the Itemstore API that can wrap a database query, url, SOAP service or message queue, then support that interface for application development.  This reduces support costs, but more importantly it gives the publisher of the API the ability to remodel internals without affecting applications.

Itemscript is a registered trademark of Data Base Architects, Inc. The Itemscript specification is an open source work published under the new BSD license.