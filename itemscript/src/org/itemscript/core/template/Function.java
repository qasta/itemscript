
package org.itemscript.core.template;

import java.util.List;

import org.itemscript.core.values.JsonValue;

public interface Function {
    public abstract JsonValue execute(JsonValue context, JsonValue value, List<JsonValue> args);
}