## Introduction ##

The Schema Validator is used to validate JSON values against JAMtemplate defined types.
The following guide will provide examples on how to create type definitions and validate input against it.

Please refer to the [Itemscript Schema](ItemscriptSchema.md) page to see a list of the default types and their specifications.

## Simple Validation ##
Let's start out by setting up the JAMTemplate and creating a simple TextBox widget with a field parameter called "name."

```
  {
    "widget" : "TextBox",
    "field" : "name"
  }
```

We want to validate the input from the TextBox to make sure it is a String. In order to do this, we need to call the validate function which always takes a value to be validated (`name`) and an argument which is a Type reference (`string`). So our Template would look something like this:
```
  {:name validate('string')}
```

Every time `validate` is called, a JSON Object is returned which contains helpful keys to aid in the validation process.

The most important keys in the returned object are:
  * valid - a boolean that returns true if the value is valid, false if not
  * message - the error message describing what exactly went wrong if the validation resulted in an error, or else the default success message if the validation was a success.

Now we want to display a message on screen if the validation failed.
So in order to do this we will need to "move into" the object that is returned by the validation, test its `valid` field, and then grab the `message` to display.

The [directive](http://code.google.com/p/itemscript/wiki/JAMtemplates) that let's us move-into an object is `.section` so the Template would now look like:

```
  {.section :name validate('string')}
  {.if :valid}
  //do nothing
  {.else}
  {:message}
  {.end}
  {.end}
```

Remember we need two closing `.end` tags; one for the `.section` directive and one for the `.if` directive.

(Because input from a TextBox is always of the String Type, this particular validate call will always result in a success).

## Validating against user-defined types ##
Now let's say you want to create your own type to validate with. You can do this by writing up a type definition in your Configuration file under the key "types."

Let's say that you want to validate that the `name` input is a string with a minimum length of 5 characters and begins with the letter "A." We will call this type: "AName."

So inside the Configuration file we would write:
```
  {
    "types" :
    {
      "org.itemscript.AName" :
      {
        ".extends" : "string",
        ".minLength" : 5,
        ".pattern" : "A*"
      }
    }
  }
```

And now we can validate our name field in the Template just like before with this new type.
```
  {:name validate('org.itemscript.AName')}
```