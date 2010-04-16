
package org.itemscript.core.template;

import java.util.List;

import org.itemscript.core.JsonSystem;
import org.itemscript.core.url.Url;
import org.itemscript.core.values.JsonValue;

public class DataUrlFunction extends FunctionBase {
    public DataUrlFunction(JsonSystem system) {
        super(system);
    }

    @Override
    public JsonValue execute(JsonValue context, JsonValue value, List<JsonValue> args) {
        if (value.isString()) {
            String contentType = value.item()
                    .meta()
                    .getString("Content-Type");
            // Make sure that any spaces between the content-type and charset are removed...
            contentType = contentType.replaceAll(" ", "");
            if (contentType.startsWith("text")) {
                return system().createString("data:" + contentType + "," + Url.encode(value.stringValue()));
            } else {
                return system().createString("data:" + contentType + ";base64," + value.stringValue());
            }
        } else {
            return system().createNull();
        }
    }
}