
package org.itemscript.template.expression;

import java.util.List;

import org.itemscript.core.JsonSystem;
import org.itemscript.core.exceptions.ItemscriptError;
import org.itemscript.core.values.JsonValue;
import org.itemscript.template.TemplateExec;

public class AndFunction extends FunctionBase {
    public AndFunction(JsonSystem system, List<Expression> args) {
        super(system, args);
    }

    //@Override
    public JsonValue execute(TemplateExec templateExec, JsonValue context, JsonValue value) {
        if (value != null && !value.isBoolean()) { throw ItemscriptError.internalError(this,
                "value.was.not.boolean", value + ""); }
        if (args().size() != 1) { throw ItemscriptError.internalError(this, "execute.only.one.arg.allowed", args()
                + ""); }
        JsonValue andValue = args().get(0)
                .interpret(templateExec, context);
        if (andValue != null && !andValue.isBoolean()) { throw ItemscriptError.internalError(this,
                "and.value.was.not.boolean", andValue + ""); }
        boolean valueBoolean;
        if (value == null) {
            valueBoolean = false;
        } else {
            valueBoolean = value.booleanValue();
        }
        boolean andValueBoolean;
        if (andValue == null) {
            andValueBoolean = false;
        } else {
            andValueBoolean = andValue.booleanValue();
        }
        return system().createBoolean(valueBoolean && andValueBoolean);
    }
}