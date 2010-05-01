
package org.itemscript.core.template.expression;

import org.itemscript.core.JsonSystem;
import org.itemscript.core.template.TemplateExec;
import org.itemscript.core.values.JsonValue;

public class RightBraceFunction extends FunctionBase {
    public RightBraceFunction(JsonSystem system) {
        super(system, null);
    }

    @Override
    public JsonValue execute(TemplateExec template, JsonValue contextVal, JsonValue value) {
        return system().createString("}");
    }
}