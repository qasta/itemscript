
package org.itemscript.core.template;

import org.itemscript.core.HasSystem;
import org.itemscript.core.JsonSystem;

public abstract class FunctionBase implements Function, HasSystem {
    private final JsonSystem system;

    public FunctionBase(JsonSystem system) {
        this.system = system;
    }

    public JsonSystem system() {
        return system;
    }
}