
package org.itemscript.schema;

import org.itemscript.core.values.JsonValue;

public interface Type {
    public void validate(String path, JsonValue value);

    public Schema schema();

    public boolean isArray();

    public boolean isBoolean();

    public boolean isBinary();

    public boolean isInteger();

    public boolean isNumber();

    public boolean isString();

    public boolean isObject();

    public String description();

    public boolean isNull();
}