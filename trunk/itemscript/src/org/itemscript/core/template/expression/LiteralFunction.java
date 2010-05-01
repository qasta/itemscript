
package org.itemscript.core.template.expression;

import org.itemscript.core.JsonSystem;
import org.itemscript.core.template.TemplateExec;
import org.itemscript.core.values.JsonString;
import org.itemscript.core.values.JsonValue;

public class LiteralFunction extends FunctionBase {
    private JsonString literal;

    public LiteralFunction(JsonSystem system, String literal) {
        super(system, null);
        this.literal = system.createString(literal);
    }

    @Override
    public JsonValue execute(TemplateExec template, JsonValue context, JsonValue value) {
        return literal;
    }
}