
package org.itemscript.schema;

import org.itemscript.core.values.JsonValue;

public interface Type {
    public String description();

    public boolean isArray();

    public boolean isBinary();

    public boolean isBoolean();

    public boolean isInteger();

    public boolean isNull();

    public boolean isNumber();

    public boolean isObject();

    public boolean isString();
    
    public boolean isDecimal();

    public Schema schema();

    public void validate(String path, JsonValue value);
}