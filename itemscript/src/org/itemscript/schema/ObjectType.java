
package org.itemscript.schema;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.itemscript.core.Params;
import org.itemscript.core.exceptions.ItemscriptError;
import org.itemscript.core.values.JsonObject;
import org.itemscript.core.values.JsonValue;

class ObjectType extends TypeBase {
    public static final String KEY_KEY = ".key ";
    public static final String OPTIONAL_KEY = ".optional ";
    public static final String PATTERN_KEY = ".pattern ";
    public static final String WILDCARD_KEY = ".wildcard";
    private final boolean hasDef;
    private boolean resolved;
    private Map<String, JsonValue> optionalKeys;
    private Map<String, JsonValue> requiredKeys;
    private Map<String, JsonValue> patterns;
    private Map<String, Type> resolvedOptionalKeys;
    private Map<String, Type> resolvedRequiredKeys;
    private JsonValue wildcard;

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
                if (first.startsWith(".")) {
                    schemaKeys.add(key);
                } else {
                    requiredKeys.put(key, def.get(key)
                            .copy());
                }
            }
            this.optionalKeys = new HashMap<String, JsonValue>();
            this.patterns = new HashMap<String, JsonValue>();
            for (String key : schemaKeys) {
                if (key.startsWith(KEY_KEY)) {
                    String remainder = key.substring(KEY_KEY.length());
                    requiredKeys.put(remainder, def.get(key)
                            .copy());
                } else if (key.startsWith(OPTIONAL_KEY)) {
                    String remainder = key.substring(OPTIONAL_KEY.length());
                    optionalKeys.put(remainder, def.get(key)
                            .copy());
                } else if (key.startsWith(PATTERN_KEY)) {
                	String remainder = key.substring(PATTERN_KEY.length());
                	patterns.put(remainder, def.get(key)
                			.copy());
                } else if (key.startsWith(WILDCARD_KEY)) {
                	wildcard = def.getValue(WILDCARD_KEY);
                }
            }
        } else {
            hasDef = false;
            optionalKeys = null;
            patterns = null;
            requiredKeys = null;
            resolved = false;
            resolvedOptionalKeys = null;
            resolvedRequiredKeys = null;
            wildcard = null;
        }
    }

    @Override
    public boolean isObject() {
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
            //requiredKeys = null;
            //optionalKeys = null;
            resolved = true;
        }
    }
    
    private Params pathValueParams(String path, JsonObject object) {
        return schema().pathParams(path)
                .p("value", object);
    }
    
    @Override
    public void validate(String path, JsonValue value) {
        super.validate(path, value);
        if (!value.isObject()) { throw ItemscriptError.internalError(this,
        		"validate.value.was.not.object",
                schema().pathParams(path)
                .p("value", value.toCompactJsonString())); }
        if (hasDef) {
            resolveTypes();
            validateObject(path, value.asObject());
        }
    }

    private void validateObject(String path, JsonObject object) {  
    	ArrayList<JsonValue> checkWildcardList = new ArrayList<JsonValue>();
    	
    	if (wildcard != null) {
    		for (String instanceKey : object.keySet()) {
	    		if (!requiredKeys.containsKey(instanceKey)) {
	    			if (!optionalKeys.containsKey(instanceKey)) {
	    				checkWildcardList.add(object.get(instanceKey));
	    			}
	    		}
    		}
	    	if (!validWildcardTypes(checkWildcardList, path)) {
	    		throw ItemscriptError.internalError(this,
	            		"validateObject.extra.instance.keys.did.not.all.match.wildcard.type",
	            		pathValueParams(path, object));
	    	}
    	}
        for (String key : resolvedRequiredKeys.keySet()) {
            // Required and must conform to the type.
            JsonValue value = object.get(key);
            if (value == null) { throw ItemscriptError.internalError(this,
            		"validate.missing.value.for.key",
                    new Params().p("key", key)
                    .p("object", object.toCompactJsonString()));
            }

            if (!patterns.isEmpty()) {
            	for (String patternKey : patterns.keySet()) {
                    if (schema().match(patternKey, key)) {
                    	Type resolvedPatternValue = schema().resolve(patterns.get(patternKey));
                		schema().validate(schema().addKey(path, key), resolvedPatternValue, value);
                    }
                }
            }
            schema().validate(schema().addKey(path, key), resolvedRequiredKeys.get(key), value);
        }
        for (String key : resolvedOptionalKeys.keySet()) {
            JsonValue value = object.get(key);
            // Optional, but if present, must conform to the type.
            if (value != null) {
            	if (!patterns.isEmpty()) {
            		for (String patternKey : patterns.keySet()) {
	            		if (schema().match(patternKey, key)) {
	                    	Type resolvedPatternValue = schema().resolve(patterns.get(patternKey));
                    		schema().validate(schema().addKey(path, key), resolvedPatternValue, value);
                    	}
            		}
            	}
                schema().validate(resolvedOptionalKeys.get(key), value);
            }   
        }
    }
    
    /**
     * Validates all of the values in the list using the wildcard's Type validator.
     * @param wildcardList
     * @param path
     * @return true if all are valid, false if not
     */
    private boolean validWildcardTypes(ArrayList<JsonValue> wildcardList, String path) {
		boolean useSlash = path.length() > 0;
    	
    	if (!wildcardList.isEmpty()) {
    		Type wildcardType = schema().resolve(wildcard);
    		for (int i = 0; i < wildcardList.size(); i++) {
	    		JsonValue listValue = wildcardList.get(i);
	    		try {
	    			wildcardType.validate(path + (useSlash ? "/" : ""), listValue);
	    		} catch (ItemscriptError e) {
	    			return false;
	    		}
    		}
    	}
	    return true;
    }
}