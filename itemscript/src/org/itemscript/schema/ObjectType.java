
package org.itemscript.schema;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.itemscript.core.Params;
import org.itemscript.core.exceptions.ItemscriptError;
import org.itemscript.core.values.JsonObject;
import org.itemscript.core.values.JsonValue;

class ObjectType extends TypeBase {
    public static final String KEY_PREFIX = "KEY ";
    private Map<String, JsonValue> requiredKeys;
    private Map<String, Type> resolvedRequiredKeys;
    private Map<String, JsonValue> optionalKeys;
    private Map<String, Type> resolvedOptionalKeys;
    private final boolean hasDef;
    private boolean resolved;

    ObjectType(Schema schema, Type extendsType, JsonObject def) {
        super(schema, extendsType, def);
        if (def != null) {
            hasDef = true;
            resolved = false;
            this.requiredKeys = new HashMap<String, JsonValue>();
            Set<String> schemaKeys = new HashSet<String>();
            for (String key : def.keySet()) {
                if (key.length() == 0) { throw ItemscriptError.internalError(this,
                        "constructor.object.type.had.empty.key", def.toCompactJsonString()); }
                String[] split = key.split("\\s+", 2);
                String first = split[0];
                if (first.equals(first.toUpperCase())) {
                    schemaKeys.add(key);
                } else {
                    requiredKeys.put(key, def.get(key)
                            .copy());
                }
            }
            this.optionalKeys = new HashMap<String, JsonValue>();
            for (String key : schemaKeys) {
                if (key.startsWith(KEY_PREFIX)) {
                    String remainder = key.substring(KEY_PREFIX.length());
                    requiredKeys.put(remainder, def.get(key)
                            .copy());
                } else if (key.startsWith("OPTIONAL ")) {
                    String remainder = key.substring("OPTIONAL ".length());
                    optionalKeys.put(remainder, def.get(key)
                            .copy());
                }
            }
        } else {
            hasDef = false;
        }
    }

    @Override
    public boolean isNumber() {
        return true;
    }

    private void resolveTypes() {
        if (!resolved) {
            resolvedRequiredKeys = new HashMap<String, Type>();
            for (String key : requiredKeys.keySet()) {
                resolvedRequiredKeys.put(key, schema().resolve(requiredKeys.get(key)));
            }
            resolvedOptionalKeys = new HashMap<String, Type>();
            for (String key : optionalKeys.keySet()) {
                resolvedOptionalKeys.put(key, schema().resolve(optionalKeys.get(key)));
            }
            // We don't need these any more...
            requiredKeys = null;
            optionalKeys = null;
            resolved = true;
        }
    }

    @Override
    public void validate(String path, JsonValue value) {
        super.validate(path, value);
        if (!value.isObject()) { throw ItemscriptError.internalError(this, "validate.value.was.not.object",
                schema().pathParams(path)
                        .p("value", value.toCompactJsonString())); }
        if (hasDef) {
            resolveTypes();
            validateObject(path, value.asObject());
        }
    }

    private void validateObject(String path, JsonObject object) {
        for (String key : resolvedRequiredKeys.keySet()) {
            // Required and must conform to the type.
            JsonValue value = object.get(key);
            if (value == null) { throw ItemscriptError.internalError(this, "validate.missing.value.for.key",
                    new Params().p("key", key)
                            .p("object", object.toCompactJsonString())); }
            schema().validate(schema().addKey(path, key), resolvedRequiredKeys.get(key), value);
        }
        for (String key : resolvedOptionalKeys.keySet()) {
            JsonValue value = object.get(key);
            // Optional, but if present, must conform to the type.
            if (value != null) {
                schema().validate(resolvedOptionalKeys.get(key), value);
            }
        }
    }
}