
package org.itemscript.core.template;

import org.itemscript.core.HasSystem;
import org.itemscript.core.JsonSystem;

/**
 * The execution context for a template, providing access to the JsonSystem, Accumulator, and potentially other
 * template-specific facilities.
 * 
 * @author Jacob Davies<br/><a href="mailto:jacob@itemscript.org">jacob@itemscript.org</a>
 */
public class TemplateExec implements HasSystem {
    private final JsonSystem system;
    private final Accumulator accumulator;

    /**
     * Create a new TemplateContext.
     * 
     * @param system The associated JsonSystem.
     * @param accumulator The associated Accumulator.
     */
    public TemplateExec(JsonSystem system, Accumulator accumulator) {
        this.system = system;
        this.accumulator = accumulator;
    }

    /**
     * Get the accumulator for this template invocation.
     *  
     * @return The accumulator.
     */
    public Accumulator accumulator() {
        return accumulator;
    }

    @Override
    public JsonSystem system() {
        return system;
    }
}