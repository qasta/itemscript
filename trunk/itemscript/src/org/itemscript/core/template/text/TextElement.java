
package org.itemscript.core.template.text;

import org.itemscript.core.template.Element;
import org.itemscript.core.template.expression.Expression;

public interface TextElement extends Element {
    public Expression asExpression();

    public boolean isExpression();
}