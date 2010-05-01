
package org.itemscript.core.template.expression;

import org.itemscript.core.JsonSystem;
import org.itemscript.core.template.TemplateExec;
import org.itemscript.core.values.JsonString;
import org.itemscript.core.values.JsonValue;

public class B64idFunction extends FunctionBase {
    public B64idFunction(JsonSystem system) {
        super(system, null);
    }

    @Override
    public JsonString execute(TemplateExec context, JsonValue contextVal, JsonValue value) {
        return system().createString(system().util()
                .generateB64id());
    }
}