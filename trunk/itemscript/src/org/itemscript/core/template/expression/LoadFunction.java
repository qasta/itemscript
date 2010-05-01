
package org.itemscript.core.template.expression;

import org.itemscript.core.JsonSystem;
import org.itemscript.core.template.TemplateExec;
import org.itemscript.core.values.JsonValue;

public class LoadFunction extends FunctionBase {
    private final String url;

    public LoadFunction(JsonSystem system, String url) {
        super(system, null);
        this.url = url;
    }

    @Override
    public JsonValue execute(TemplateExec template, JsonValue context, JsonValue value) {
        return system().get(url);
    }
}