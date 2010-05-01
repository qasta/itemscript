
package org.itemscript.core.template.text;

import org.itemscript.core.HasSystem;
import org.itemscript.core.JsonSystem;
import org.itemscript.core.template.TemplateExec;
import org.itemscript.core.template.expression.Expression;
import org.itemscript.core.values.JsonValue;

public abstract class Segment implements HasSystem, TextElement {
    private final JsonSystem system;

    public Segment(JsonSystem system) {
        this.system = system;
    }

    public Expression asExpression() {
        return null;
    }

    public abstract JsonValue interpret(TemplateExec templateExec, JsonValue context);

    public boolean isExpression() {
        return false;
    }

    @Override
    public JsonSystem system() {
        return system;
    }
}