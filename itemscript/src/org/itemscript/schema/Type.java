
package org.itemscript.schema;
/**
 * @author Jacob Davies
 */
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
    
    public boolean isLong();
    
    public boolean isAny();

    public Schema schema();

    public void validate(String path, JsonValue value);
}