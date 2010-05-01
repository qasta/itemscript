
package org.itemscript.core.template.expression;

import org.itemscript.core.JsonSystem;
import org.itemscript.core.template.TemplateExec;
import org.itemscript.core.values.JsonValue;

public class LeftBraceFunction extends FunctionBase {
    public LeftBraceFunction(JsonSystem system) {
        super(system, null);
    }

    @Override
    public JsonValue execute(TemplateExec template, JsonValue context, JsonValue value) {
        return system().createString("{");
    }
}