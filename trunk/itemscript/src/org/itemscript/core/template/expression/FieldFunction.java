
package org.itemscript.core.template.expression;

import org.itemscript.core.JsonSystem;
import org.itemscript.core.Params;
import org.itemscript.core.exceptions.ItemscriptError;
import org.itemscript.core.template.TemplateExec;
import org.itemscript.core.values.JsonValue;

public class FieldFunction extends FunctionBase {
    private final String path;

    public FieldFunction(JsonSystem system, String path) {
        super(system, null);
        this.path = path;
    }

    @Override
    public JsonValue execute(TemplateExec template, JsonValue context, JsonValue value) {
        if (path.length() == 0) {
            return context;
        } else {
            if (context != null && context.isContainer()) {
                return context.asContainer()
                        .getByPath(path);
            } else {
                throw ItemscriptError.internalError(this, "execute.context.was.not.a.container", new Params().p(
                        "context", context)
                        .p("path", path));
            }
        }
    }
}