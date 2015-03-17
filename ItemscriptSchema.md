# Itemscript Schema #

_Authors: Eileen Bai & Jacob Davies_

Itemscript Schema is a very simple schema language for describing JSON values.

It aims to be extremely concise, self-explanatory, and minimal. Other schema languages allow much more precision in specifying what values are acceptable, but are also much more complex and less concise.

Schema languages are primarily for people; they're used to communicate expectations between people programming different parts of a system, or to document the external interfaces of a system.

As such, the most important aspect of a schema language is that it be human-readable and human-writable, and that it be concise enough to be useful in documentation. No schema language can hope to convey all of the subtleties of a system's interfaces, but it can clarify things like the names and spellings of keys in objects, what types are expected (and not expected) in what places, and what fixed constraints exist on the size or range of values.

Validation of instance values is not nearly as important in most real situations. Validation with Itemscript Schema does not change the instance value in any way, and does not assign types to values.
<br>All it does is confirm that what was supplied matches the types it knows about.<br>
<br>
<h2>A Schema</h2>

A schema is a JSON object whose keys are the names of types, and whose values are the type definitions.<br>
<br>
There are about a dozen "core" types, with short names like "string", "object", and "any". Those are added into all schemas, so your schema definition doesn't need to include them.<br>
<br>
You should name your own types with a unique prefix; a good one is your domain name, reversed, like: "org.itemscript.Type". That way your schema can be combined with someone else's schema later, without the names interfering with each other. In this document, we'll include type definitions alone, without the rest of the schema.<br>
<br>
<h3>An example</h3>

Let's start with an object:<br>
<br>
<pre><code>{<br>
  "name" : "Bella",<br>
  "age" : 2,<br>
  "owner" : "Vera",<br>
  "breed" : "Cavalier King Charles"<br>
}<br>
</code></pre>

Here's a type that describes that object:<br>
<br>
<pre><code>"com.petstore.Dog" :<br>
{<br>
  "name" : "string",<br>
  ".optional age" : "integer"<br>
  "owner" : "string",<br>
  "breed" : "string"<br>
}<br>
</code></pre>

That type also matches this object:<br>
<br>
<pre><code>{<br>
  "name" : "Fido",<br>
  "owner" : "Steve",<br>
  "breed" : "mutt",<br>
  "siblings" : ["Rex"]<br>
}<br>
</code></pre>

Note that the existence of extra values under keys not specified in the original type is no problem.<br>
<br>By default, we just ignore any keys & values we don't know about. This is a good default, as it lets other people extend those objects without having to consult with us.<br>
<br>
That type doesn't match this object:<br>
<br>
<pre><code>{<br>
  "name" : "Loki",<br>
  "species" : "cat",<br>
  "owner" : "Jacob"<br>
}<br>
</code></pre>

The fact that it's the missing "breed" value is the problem with this object.<br>
<br>
It doesn't match this object either:<br>
<br>
<pre><code>{<br>
  "name" : "Rex",<br>
  "age" : "6 months",<br>
  "owner" : "Steve",<br>
  "breed" : "mutt"<br>
}<br>
</code></pre>

The "age" value is optional, so it's okay if it's not present at all.<br>
<br>But if it's present, it has to be an integer number, not a string.<br>
<br>
To distinguish between type definitions and objects you're describing, the former will be called "<b>types</b>" and the latter "<b>instances</b>".<br>
<br>
<h4>The basic types</h4>

<ul><li>any<br>
</li><li>object<br>
</li><li>array<br>
</li><li>string<br>
<ul><li>binary<br>
</li><li>decimal<br>
<ul><li>long<br>
</li></ul></li></ul></li><li>number<br>
<ul><li>integer<br>
</li></ul></li><li>boolean</li></ul>

<h2>Type definitions</h2>

A type definition can either be a JSON object or a JSON array. If it's an object, it contains keys that define the type, and it can be any kind of type, with the default being an object type if no “.extends” value is supplied. If it's an array, it always means an array type, and the allowable values for that array are given by the contents of the array. Until we get to talking about array type definitions, we'll assume that we're talking about type definitions in a JSON object.<br>
<br>
An empty object as a definition just means the basic "object" type that can contain any kind of values.<br>
<br>An empty array as a definition just means the basic "array" type that can contain any kind of values.<br>
<br>
<h2>Type reference</h2>

A type reference is a string giving the name of another type. The other type has to exist in the same schema. You use type references with “.extends” to say what type your type is extending.<br>
<br>
<h2>Type specification</h2>

A type specification is used when you need to say what kind of type is allowed in an object or array.<br>
<br>A type specification can either be a type reference referring to another type, or a type definition itself.<br>
<br>So, you can write things like this:<br>
<br>
<pre><code>{<br>
  "name" : "string",<br>
  "address" :<br>
  {<br>
    "street" : "string",<br>
    "city" : "string",<br>
    "zip" : "string"<br>
  }<br>
}<br>
</code></pre>

A type specification can therefore be a JSON string, a JSON object, or a JSON array.<br>
<br>
<h2>All type definitions that are JSON objects</h2>

Inside a type definition, all keys that start with a dot are treated as schema-related.<br>
<br>
All type definitions can contain these two keys:<br>
<ul><li><b>.extends</b> – has a string value that is a type reference giving the name of the type that this type is extending. The instance must match the type that this type extends (and the type that <b>that</b> type extends, and so on) as well as the type being specified. There is no way to "loosen" restrictions made by a type that you are extending.<br>
</li><li><b>.description</b> – has a string value that contains a description of the type.</li></ul>

<h2>Object type definitions</h2>

The default kind of type definition is an Object type definition.<br>
<br>Object type definitions are a JSON object whose keys are the names of keys that are required to appear in the matching instance, and whose corresponding values are type specifications for the types that the corresponding values in the instance must match.<br>
<br>
<ul><li><b>.optional</b> <a href='key.md'>name</a> – a key starting with the string ".optional" has a value that is a type specification. The remainder of the key after ".optional" is used as the name of a key that may (but does not have to) occur in the instance object. If it occurs, it must match the type specification.<br>
</li><li><b>.key</b> <a href='key.md'>name</a> – a key starting with the string ".key" has a value that is a type specification. The remainder of the key after ".key" is used as the name of a key that must occur in the instance object and match the type specification. This is useful when you have an all-caps key you wish to specify as being required.<br>
</li><li><b>.pattern</b> <a href='key.md'>pattern</a> – a key starting with the string ".pattern" has a value that is a type specification. The remainder of the key after ".pattern" is used as the pattern that will be used to match against keys in the instance object; any matching keys must match the type specification. If more than one .pattern key exists and a key in the instance object matches more than one of the patterns, it must match the type specifications of all the patterns it matches.<br>
</li><li><b>.wildcard</b> – a key named ".wildcard" means that any instance value identified by a key not matching another key specification in the type (a fixed key or a pattern) must match the corresponding type. This can be used to specify something like a Java Map. It's most useful without any other keys, but can be useful for a more compact syntax by allowing a mixture of special fixed keys and other keys (as for instance in an object definition in this schema language), although care should be taken that the two (or more) interpretations of keys do not overlap one another.<br>
</li><li><b>.inArray</b> – a key named ".inArray" has a value that is an array value providing an enumerable array of object values that the instance object can be equal to.<br>
</li><li><b>.notInArray</b> – a key named ".notInArray" has a value that is an array value providing an enumerable array of object values that the instance object can not be equal to.</li></ul>

<h2>String type definitions</h2>

The String type just represents the standard string value. Most values retrieved from widgets such as TextBox and ListBox are of the String type.<br>
<br>
<ul><li><b>.equals</b> – has a string value giving the exact string the instance string must equal.<br>
</li><li><b>.isLength</b> – has an integer value giving the exact length of an instance string, in characters.<br>
</li><li><b>.minlength</b> – has an integer value giving the minimum length of an instance string, in characters.<br>
</li><li><b>.maxlength</b> – has an integer value giving the maximum length of an instance string, in characters.<br>
</li><li><b>.pattern</b> – has a value that is either a single string or an array of strings; each is a pattern, and an instance string must match one of these to match this type.<br>
</li><li><b>.regExPattern</b> – has a string value that is a regular expression used to validate the format of the instance string with.<br>
</li><li><b>.inArray</b> – has an array value providing an enumerable array of string values that the instance string can be equal to.<br>
</li><li><b>.notInArray</b> – has an array value providing an enumerable array of string values that the instance string can not be equal to.</li></ul>

<h2>Decimal type definitions</h2>

Decimal values represent an exact decimal number in a JSON string. The Decimal type extends the basic String type. They are useful when working with currencies or other values where standard floating-point numbers are not appropriate. The representation is:<br>
<br>
decimal<br>
<ul><li>int<br>
</li><li>int frac</li></ul>

int<br>
<ul><li>(Digit.)<br>
</li><li>(Digit 1-9.) digits<br>
</li><li>- (Digit.)<br>
</li><li>- (Digit 1-9.) digits</li></ul>

frac<br>
<ul><li>. digits</li></ul>

No whitespace is permitted. Any string that does not match this pattern fails to match a decimal type.<br>
<br>
<ul><li><i>See <b>number type definitions</b> for list of generic numerical parameters, all number instance values must be of type decimal.</i>
</li><li><b>.fractionDigits</b> – has an integer value giving the maximum number of digits after the decimal point in an instance number.</li></ul>

(Validator implementations that cannot handle very large numbers should be careful to ignore MIN and MAX specifications that are outside of their number range, and allow any decimal instance to match, rather than reject values that may be valid but uncheckable because of implementation limitations.)<br>
<br>
<h2>Long type definition</h2>

Long values represent a 64-bit signed integer value with the same range as a Java long with a minimum value of -2<sup>63</sup> and a maximum value of 2<sup>63 - 1</sup>. The long type extends the decimal type, so they are also stored in a JSON string, and they implicitly specify the following restrictions:<br>
<br>
<ul><li>“.min” : "-2^63",<br>
</li><li>".max" : "2^63 – 1",<br>
</li><li>".fractionDigits" : 0</li></ul>

No whitespace is permitted. Any string that does not match this pattern fails to match a long type.<br>
<br>
<ul><li><i>See <b>number type definitions</b> for list of generic numerical parameters, all number instance values must be of type long.</i></li></ul>

This type is useful when you are transferring Java long values (or other 64-bit signed integers), because the JSON number type cannot represent the full range of a Java long value.<br>
<br>
<h2>Binary type definitions</h2>

Binary values represent an array of bytes that can be converted to a base-64-encoded string for transport in JSON values. The binary type extends the string type.<br>
<br>
<ul><li><b>.maxBytes</b> – has an integer value giving the maximum length of a binary instance, in bytes.</li></ul>

(You should avoid using the string restrictions on binary values, since they will be applied to the base-64-encoded representation of the value, which is not very useful, and checking them may trigger the base-64 conversion unnecessarily.)<br>
<br>
<h2>Number type definitions</h2>

The Number type represents all standard numerical values.<br>
<br>
<ul><li><b>.equalTo</b> – has a value giving the exact number the instance number can be equal to.<br>
</li><li><b>.greaterThan</b> – has a value giving the minimum number for an instance number, must be greater than this number.<br>
</li><li><b>.greaterThanOrEqualTo</b> – has a value giving the minimum value for an instance number, must be greater than or equal to this number.<br>
</li><li><b>.lessThan</b> – has a value giving the maximum value for an instance number, must be less than this number.<br>
</li><li><b>.lessThanOrEqualTo</b> – has a value giving the maximum value for an instance number, must be less than or equal to this number.<br>
</li><li><b>.even</b> – has a boolean value indicating whether or not the instance number needs to be even or not even.<br>
</li><li><b>.odd</b> – has a boolean value indicating whether or not the instance number needs to be odd or not odd.<br>
</li><li><b>.inArray</b> – has an array value providing an enumerable array of number values that the instance number can be equal to.<br>
</li><li><b>.notInArray</b> – has an array value providing an enumerable array of number values that the instance number can not be equal to.</li></ul>

<h2>Integer type definitions</h2>

Integers extend the number type, and are JSON numbers, but cannot have any non-zero fractional digits.<br>
<br>
<ul><li><i>See <b>number type definitions</b> for list of generic numerical parameters, all number instance values must be of type integer.</i></li></ul>

<h2>Boolean type definitions</h2>

Represents the basic boolean structure.<br>
<br>
<ul><li><b>.booleanValue</b> – has a boolean value specifying what the instance boolean must be in order to pass.</li></ul>

<h2>Array type definitions</h2>

Array type definitions can be either a JSON object like other type definitions, or a JSON array.<br>
<br>
When they're a JSON object, they can contain the following keys:<br>
<br>
<ul><li><b>.exactSize</b> – has an integer value giving the exact number of elements that an instance array must contain.<br>
</li><li><b>.minSize</b> – has an integer value giving the minimum number of elements that an instance array can contain.<br>
</li><li><b>.maxSize</b> – has an integer value giving the maximum number of elements that an instance array can contain.<br>
</li><li><b>.contains</b> – has a value that is a type specification giving the type of value that an instance array can contain. If you need them to be able to contain more than one type of value, specify an "any" type reference.<br>
</li><li><b>.inArray</b> – has an array value providing an enumerable array of array values that the instance array can be equal to.<br>
</li><li><b>.notInArray</b> – has an array value providing an enumerable array of array values that the instance array can not be equal to.</li></ul>

When they're a JSON array, they can either be empty, or contain a single value that is a type specification giving the type of value that an instance array can contain. So, ["string"] defines an array type that can only contain strings and is exactly equivalent to an object type definition of:<br>
<br>
<pre><code>{<br>
  ".extends" : "array",<br>
  ".contains" : "string"<br>
}<br>
</code></pre>

<h2>Any type definitions</h2>

An "any" type allows you to specify that a value can be one of several different types depending on what JSON type it is. This is often useful when you want to be able to supply different JSON types under the same key and trigger different behavior. For instance, a type specification is itself an instance of "any" type, as it can be either a JSON string, a JSON array, or a JSON object, each of which is treated differently.<br>
<br>
<ul><li><b>.string</b> – has a value that is a type reference to a string type, specifies the type to match if the value is a JSON string.<br>
</li><li><b>.number</b> – has a value that is a type reference to a number type, specifies the type to match if the value is a JSON number.<br>
</li><li><b>.boolean</b> – has a value that is a type reference to a boolean type, specifies the type to match if the value is a JSON boolean.<br>
</li><li><b>.array</b> – has a value that is a type specification of an array type, specifies the type to match if the value is a JSON array.<br>
</li><li><b>.object</b> – has a value that is a type specification of an object type, specifies the type to match if the value is a JSON object.<br>
</li><li><b>.inArray</b> – has an array value providing an enumerable array of "Any" values that the instance Any Type can be equal to.<br>
</li><li><b>.notInArray</b> – has an array value providing an enumerable array of "Any" values that the instance Any Type can not be equal to.</li></ul>

If any of these keys are present in a type extending "any", an instance value must be of one of the types that is specified. For instance, if .number and .array are present, an instance value must be a JSON number or JSON array.<br>
<br>
If none of the keys is present, any value type at all can be present.<br>
<br>
<b>Recipes</b>

<ul><li>A key with a fixed string value<br>
</li><li>A 2d array<br>
</li><li>Use of domain name for namespaces</li></ul>

<h4>The schema definition for a schema:</h4>
<pre><code>{<br>
	“org.itemscript.Schema” :<br>
	{<br>
		“.description” : “A schema.”,<br>
		“.pattern *” : “org.itemscript.EitherTypeDef”<br>
	},<br>
	“org.itemscript.EitherTypeDef” :<br>
	{<br>
		“.extends” : “any”,<br>
		“.description” : “A type definition that is either a JSON object or a JSON array.”<br>
		“.array” : “org.itemscript.InlineArrayTypeDef”,<br>
		“.object” : “org.itemscript.TypeDef”<br>
	},<br>
	“org.itemscript.TypeDef” :<br>
	{<br>
		“.extends” : “object”,<br>
		“.description” : “A type definition that is a JSON object.”,<br>
		“.optional .extends” : “org.itemscript.TypeRef”,<br>
		“.optional .description” : “string”<br>
	},<br>
	“org.itemscript.InlineArrayTypeDef” :<br>
	{<br>
		“.extends” : “array”,<br>
		“.description” : “A type definition that is a JSON array.”,<br>
		“.size” : 1,<br>
		“.optional .contains” : “org.itemscript.TypeSpec”<br>
	},<br>
	“org.itemscript.TypeRef” :<br>
	{<br>
		“.extends” : “string”,<br>
		“.description” : “A reference to a type.”<br>
	},<br>
	“org.itemscript.TypeSpec” :<br>
	{<br>
		“.extends” : “any”,<br>
		“.description” : “Either a type reference or a type definition.”,<br>
		“.string” : “org.itemscript.TypeRef”,<br>
		“.object” : “org.itemscript.TypeDef”,<br>
		“.array” : “org.itemscript.InlineArrayTypeDef”<br>
	},<br>
	“org.itemscript.NullTypeDef” :<br>
	{<br>
		“.extends” : “org.itemscript.TypeDef”,<br>
		“.description” : “A definition of a null type.”<br>
	},<br>
	“org.itemscript.AnyTypeDef” :<br>
	{<br>
		“.extends” : “org.itemscript.TypeDef”,<br>
		“.description” : “A definition of an any type.”<br>
		“.string” : “org.itemscript.TypeSpec”,<br>
		“.number” : “org.itemscript.TypeSpec”,<br>
		“.boolean” : “org.itemscript.TypeSpec”,<br>
		“.array” : “org.itemscript.TypeSpec”,<br>
		“.object” : “org.itemscript.TypeSpec”,<br>
		“.optional .inArray” : “array”,<br>
		“.optional .notInArray” : “array”<br>
	},<br>
	“org.itemscript.ObjectTypeDef” :<br>
	{<br>
		“.extends” : “org.itemscript.TypeDef”,<br>
		“.description” : “A definition of an object type.”,<br>
		“.pattern .optional *” : “org.itemscript.TypeSpec”,<br>
		“.pattern .key *” : “org.itemscript.TypeSpec”,<br>
		“.pattern .pattern *” : “org.itemscript.TypeSpec”,<br>
		“.optional .wildcard” : “org.itemscript.TypeSpec”,<br>
		“.optional inArray” : “array”,<br>
		“.optional .notInArray” : “array”<br>
	},<br>
	“org.itemscript.ArrayTypeDef” :<br>
	{<br>
		“.extends” : “org.itemscript.TypeDef”,<br>
		“.description” : “A definition of an array type.”,<br>
		“.optional .contains” : “org.itemscript.TypeSpec”,<br>
		“.optional .exactSize” : “integer”,<br>
		“.optional .minSize” : “integer”,<br>
		“.optional .maxSize” : “integer”,<br>
		“.optional .inArray” : “array,<br>
		“.optional .notInArray” : “array”<br>
<br>
	},<br>
	“org.itemscript.StringTypeDef” :<br>
	{<br>
		“.extends” : “org.itemscript.TypeDef”,<br>
		“.description” : “A definition of a string type.”,<br>
		“.optional .equals” : “string”,<br>
		“.optional .isLength” : “integer”,<br>
		“.optional .minLength” : “integer”,<br>
		“.optional .maxLength” : “integer”,<br>
		“.optional .pattern” :<br>
		{<br>
			“.extends” : “any”,<br>
			“.string” : “string”,<br>
			“.array” : [“string”]<br>
		},<br>
		“.optional .regExPattern” : “string”,<br>
		“.optional .inArray” : “array”,<br>
		“.optional .notInArray” : “array”<br>
	},<br>
	“org.itemscript.DecimalTypeDef” :<br>
	{<br>
		“.extends” : “org.itemscript.StringTypeDef”,<br>
		“.description” : “A definition of a decimal type.”,<br>
		“.optional .equalTo” : “decimal”,<br>
		“.optional .greaterThan” : “decimal”,<br>
		“.optional .lessThan” : “decimal”,<br>
		“.optional .greaterThanOrEqualTo” : “decimal”,<br>
		“.optional .lessThanOrEqualTo” : “decimal”,<br>
		“.optional .fractionDigits” : “integer”,<br>
		“.optional .inArray” : “array”,<br>
		“.optional .notInArray” : “array”,<br>
		“.optional .even” : “boolean”,<br>
		“.optional .odd” : “boolean”<br>
	},<br>
	“org.itemscript.LongTypeDef” :<br>
	{<br>
		“.extends” : “org.itemscript.DecimalTypeDef”,<br>
		“.description” : “A definition of a long type.”,<br>
		“.optional .equalTo” : “long”,<br>
		“.optional .greaterThan” : “long”,<br>
		“.optional .lessThan” : “long”,<br>
		“.optional .greaterThanOrEqualTo” : “long”,<br>
		“.optional .lessThanOrEqualTo” : “long”,<br>
		“.optional .inArray” : “array”,<br>
		“.optional .notInArray” : “array”,<br>
		“.optional .even” : “boolean”,<br>
		“.optional .odd” : “boolean”<br>
	},<br>
	“org.itemscript.BinaryTypeDef” :<br>
	{<br>
		“.extends” : “org.itemscript.StringTypeDef”,<br>
		“.description” : “A definition of a binary type.”,<br>
		“.optional .maxBytes” : "integer"<br>
	},<br>
	“org.itemscript.NumberTypeDef” :<br>
	{<br>
		“.extends” : “org.itemscript.TypeDef”,<br>
		“.description” : “A definition of a number type.”,<br>
		“.optional .greaterThan” : “number”,<br>
		“.optional .lessThan” : “number”,<br>
		“.optional .greaterThanOrEqualTo” : “number”,<br>
		“.optional .lessThanOrEqualTo” : “number”,<br>
		“.optional .equalTo” : “number”,<br>
		“.optional .inArray” : “array”,<br>
		“.optional .notInArray” : “array”,<br>
		“.optional .even” : “boolean”,<br>
		“.optional .odd” : “boolean”<br>
	},<br>
	“org.itemscript.IntegerTypeDef” :<br>
	{<br>
		“.extends” : “org.itemscript.NumberTypeDef”,<br>
		“.description” : “A definition of an integer type.”,<br>
		“.optional .greaterThan” : “integer”,<br>
		“.optional .lessThan” : “integer”,<br>
		“.optional .greaterThanOrEqualTo” : “integer”,<br>
		“.optional .lessThanOrEqualTo” : “integer”,<br>
		“.optional .equalTo” : “integer”,<br>
		“.optional .inArray” : “array”,<br>
		“.optional .notInArray” : “array”,<br>
		“.optional .even” : “boolean”,<br>
		“.optional .odd” : “boolean”<br>
	},<br>
	“org.itemscript.BooleanTypeDef” :<br>
	{<br>
		“.extends” : “org.itemscript.TypeDef”,<br>
		“.description” : “A definition of a boolean type.”,<br>
		“.optional .booleanValue” : “boolean”<br>
	}<br>
}<br>
</code></pre>


Itemscript is a registered trademark of Data Base Architects, Inc, sponsors of the Itemscript open source project.  Itemscript is open source, published under the new BSD license.