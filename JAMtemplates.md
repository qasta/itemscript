**JAM** (JSON Application Markup) is an application development language based on itemscript (http://itemscript.org).  **JAM** extends **Itemscript** to express data, events and interfaces using a common language based on name:value pairs expressed as JSON data.

This document describes JAM templates and the JAM template language.

# Introduction #

The JAM language can be used directly to describe data, events and interfaces, or you can use a simple notation to build re-usable templates.

This is a simple JAM declaration that displays an image:

{
> "widget" : "Image",
> "url" : "PetBase.png"
}

Using JAM template notation, you can bind the "url" value from a configuration file:

{
> "widget" : "Image",
> "url" : ":image",
}

At runtime, the :image resolves to the object at that location.

JAM templates make it easy to write JAM applications from building blocks.  This enables easier customization of interfaces and reduces the amount of new code needed to build applications that share a basic design.

# JAM template notation #

## This is template text: ##

```
This is a number: - {:number} -
This is a string: - {:text} -
This is a boolean: - {:trueBoolean} -

This is a field reference: {:text}

This is a URL reference: {@classpath:org/itemscript/test/templateInclude.txt}

This is a fragment reference: {@#object/a}

This is a comment: {# This is some comment text. }

This is an encoded literal: {&b%3D%7B%25}

These are some braces: {'{'} {'}'}

This a value HTML-encoded: {:containsHtmlChars html}

This is a value URL-encoded: {:containsUrlChars url}

Section:
{.section :object}
A: {:a}
B: {:b}
C: {:c}
Missing field: {d}
{.end}

Foreach:
{.foreach :array}Entry: {:}{.join} - {.end}

{# Note the .join section.}
{# Note also the empty field used when the values in the array are not themselves container objects.}

If:
{.if :trueBoolean}Yes{.else}No{.end}
{.if :falseBoolean}Yes{.else}No{.end}

Missing section:
{.section :doesntExist}
This won't show up.
{.or}
This will show up.
{.end}

Nested directives:
{.foreach :arrayOfObjects}
    Name: {:name}
    {.section :address}
        Street: {:street}
        Zip: {:zip}
    {.end}
{.end}
```

## JAM Template context ##

Templates apply properties from a context.

We'll use this context on the above JAM template notation:

```
{
    "text" : "xyz",
    "number" : "1.5",
    "trueBoolean" : true,
    "falseBoolean" : false,
    "object" : {
        "a" : "A",
        "b" : "B",
        "c" : "C"
    },
    "array" : [
        "a",
        "b",
        "c"
    ],
    "arrayOfObjects" : [
        {
            "name" : "Jacob",
            "address" : {
                "street" : "10 A Street",
                "zip" : "54321"
            }
        },
        {
            "name" : "Loki",
            "address" : {
                "street" : "20 B Street",
                "zip" : "75633"
            }
        },
        {
            "name" : "Victoria",
            "address" : {
                "street" : "40 C Street",
                "zip" : "34391"
            }
        }
    ],
    "containsHtmlChars" : "<hi> &",
    "containsUrlChars" : "foo&bar:"
}
```

## JAM language for this context ##

With the above context, the JAM template produces this result:

```
This is a number: - 1.5 -
This is a string: - xyz -
This is a boolean: - true -

This is a field reference: xyz

This is a URL reference: Included text. Note, tags are {not} interpreted.

This is a fragment reference: A

This is a comment: 

This is an encoded literal: b={%

These are some braces: { }

This a value HTML-encoded: &lt;hi&gt; &amp;

This is a value URL-encoded: foo%26bar%3a

Section:

A: A
B: B
C: C
Missing field: 


Foreach:
Entry: a - Entry: b - Entry: c




If:
Yes
No

Missing section:

This will show up.


Nested directives:

    Name: Jacob
    
        Street: 10 A Street
        Zip: 54321
    

    Name: Loki
    
        Street: 20 B Street
        Zip: 75633
    

    Name: Victoria
    
        Street: 40 C Street
        Zip: 34391
```

## The Java API ##

(See also `Template` JavaDoc.)
```
    Template template = Template.create(system(), text);
    JsonObject context = system().getObject(contextUrl);
    String output = template.interpretToString(context);
```

## JAM template basics ##

A JAM template has text mixed with tags. Tags starting with a dot (like `{.foreach}`) are directives that mark sections of the template.  The statements that follow may change the context of the contents. Other tags can be used to include values from the context, or to load them from the Itemscript system.

### Ordinary text ###

The text outside of any template tags can contain any characters except `{` and `}`. All characters in the text will be included, including carriage returns and other whitespace.

### {tag} - Tags ###

A tag is contained within curly brackets, like this: `{:fieldName}`. It can contain any characters except carriage returns and curly brackets. Leading and trailing whitespace inside the tag is ignored. The contents are divided by spaces into a series of tokens.

### {.tag} - Directives ###

Tags whose first token is a dot `.` are treated as directives and interpreted in a special way. Otherwise the contents of the tag are evaluated left-to-right as a series of tokens.

Usually the first token causes a value to be retrieved (either from the surrounding context or from the system) and subsequent tokens cause that value to be changed in some way, for instance, by being HTML-escaped.

### {:field} - Field references ###

A token whose first character is `:` is treated as a field reference. The portion after the `:` is treated as the field name in the surrounding context. If the portion after the `:` is empty, the entire context is returned.

Field names are URL-decoded before being used, so you should URL-encode any special character you need to. There is currently no quoting syntax so encoding is the only way to include special characters in field names, string literals, and so on.

For instance, `{:name}`

### {# comment} - Comments ###

A token whose first character is a `#` sign ends the interpretation of the token sequence. Note that comments may not include carriage returns or curly braces.

For instance, `{# This is a comment }`

### {@url} - URL references ###

A token whose first character is an `@` sign is treated as a URL reference. The value referred to by the URL is returned. If the URL is relative, it is treated as relative to the system base URL or to the base URL supplied when interpreting the template.

For instance: `{@http://itemscript.org/templateInclude.txt}` or `{@file.txt}` or `{@#fragment}`.

Note that in the last example, `#fragment` will be interpreted as relative to the JsonItem of the surrounding context; if the context has no attached JsonItem, it is an error to use a fragment URL.

### {&literal} - Literal tokens ###

A token whose first character is a `&` sign is treated as a string literal. The remainder of the token is URL-decoded and included in the template at that point.

For instance: `{&b%3D%7B%25}`

### {**Itemscript.CONSTANT} - Constant token ###**

A token whose first character is a `*` sign is treated as a constant reference. The remainder of the token is URL-decoded and used to look up a constant from the JsonSystem.

### {b64id} - Function tokens ###

Tokens whose first character is a letter will be treated as the name of a function chosen from a limited subset. The functions implemented at present are:

  * `b64id`, which generates a unique random ID using only URL-safe characters, for instance "`H2eBRN-55bZsRzM6xCdU6Q`".
  * `uuid`, which generates a new UUID, for instance. `"3d9f9533-c32f-4183-b78c-f01f32609de4"`.
  * `html`, which HTML-escapes its input.
  * `url`/`uri`, which URL-encodes its input.
  * `dataUrl`, which produces a `data:` URL from its input, which for best results should be a value loaded from a URL so that content-type information is included, for instance, `{@http://www.mozilla.org/images/home/icons/planet.png dataUrl}` turns into a string starting `data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf...`

## Evaluating {.directive} Directive tags ##

Directives are tags that startw with a period or dot (`.`).

The template portion between an opening directive and its corresponding `{.end}` directive belongs to a segment attached to the opening directive.

When you nest directives, you can interpret enclosed template segments in different contexts. JAM expressions typically operate on multiple contexts.  For example, a form might operate in the context of a given customer while it's data validation operates in the context of the itemscript schema definition of application metadata.

A directive that opens a section usually specifies a context to operate on. The context value is provided by the remaining tokens in the opening directive tag after the first. Often the context is a field value, but it can also be any other token or token sequence, including a URL reference.

For instance:

`{.foreach :array}` or `{.end}` or `{.section @http://itemscript.org/test.json#test-object}`

### {.section} directives ###

The `{.section}` directive lets you create a new context for the contained content based on the value returned by the second token in the tag. This lets you "move into" nested objects in your context to access their fields, or open a new context from a value stored in memory or on the network. The field is specified in the token following the `.section` token, and is URL-decoded before being used to look up the value.

For instance, the template:

```
Name: {:name}
{.section :address}
Street: {:street}
{.end}
```

interpreted with the context:

```
{
    "name" : "Jacob",
    "address" : {
        "street" : "10 A Street"
    }
}
```

produces the output:

```
Name: Jacob

Street: 10 A Street
```

You can specify a section to be included if the field to move into is missing or null, by including an `{.or}` directive before the `{.end}` directive.

For instance:

```
{.section :address}
(Address stuff)
{.or}
(No address given)
{.end}
```

### {.if} directive ###

The `{.if}` directive lets you test for the existence and "truth" of a value specified by the second token, then branch to different template sements depending on the result.

If no field is specified, the surrounding context itself is tested. This is useful, for instance, if you are iterating through a list of boolean values.

The optional `{.else}` directive, if included before the `{.end}` directive, can be used to specify a section that is included if the value is false.

Values are true if they exist in the surrounding context and:
  * are boolean and true
  * are numbers and not zero
  * are strings of length greater than zero

Otherwise they are false.

Objects and arrays are false; if you want to test for their existence, use the `{.section}` directive.

For instance, the template:

```
{.foreach :people}
{.if :alive}Alive{.else}Dead{.end}
{.end}
```

interpreted with the context:

```
{
    "people" : [
        {
            "alive" : true
        },
        {
            "alive" : false
        }
    ]
}
```

produces the output:

```
Alive

Dead
```

### {.foreach} directive ###

The `{.foreach}` directive can be used to apply the contained template segment on each element in an array.

The context for each expression will be the corresponding element in the array.

The field is specified in the token following the `.foreach` token, and is URL-decoded before being used to look up the value.  If no field is specified, the surrounding context itself is used; this is useful when iterating through an array of arrays.

For example:

```
{.foreach :letters}"{}"{.join} {.end}
```

interpreted with the context:

```
{
    "letters" : ["a", "b", "c"]
}
```

produces the output:

```
    "a" "b" "c"
```

You can include a section to be included between entries by including a `{.join}` directive before the `{.end}` directive. If no entries or only one are included, the join section will not be used.

At present there is no way to retrieve the array index or to iterate over a JsonObject.

## Security notes ##

Remember to use the `html` or `url` parameters in tags as appropriate to make sure content included in a template is correctly escaped. User-supplied input included in web pages should almost always be HTML-escaped. Data included in a URL query-string should almost always be URL-encoded.

At present, permission to write a template should be treated as permission to execute arbitrary code as the current user. So, user-supplied templates should not be used except where that condition is acceptable.

Let us know if you have use for a restricted subset of JAM template notation that can interpret templates without giving so much access.

This page at the doctype project has a good overview of the potential security problems when displaying user-supplied content: http://code.google.com/p/doctype/wiki/ArticlesXSS

## Future development ##

Planned but not scheduled for the near future:
  * Inclusion of other template files
  * An expression language for testing and manipulating values
  * Number-formatting parameter for numeric values
  * Substring parameter for string values
  * Restartable interpretation for asynchronous inclusion of values in GWT/JavaScript
  * Automatic caching of compiled templates


Itemscript and JAM are trademarks of Data Base Architects, Inc.  The Itemcript and JAM specifications are published as open source under the new BSD license.