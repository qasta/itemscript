
package org.itemscript.core.template.text;

import org.itemscript.core.JsonSystem;
import org.itemscript.core.template.TemplateExec;
import org.itemscript.core.values.JsonString;
import org.itemscript.core.values.JsonValue;

public class Text extends Segment {
    private final JsonString text;

    public Text(JsonSystem system, String text) {
        super(system);
        this.text = system.createString(text);
    }

    @Override
    public JsonValue interpret(TemplateExec templateExec, JsonValue context) {
        return text;
    }
}