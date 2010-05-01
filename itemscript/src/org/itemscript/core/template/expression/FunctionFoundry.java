
package org.itemscript.core.template.expression;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.itemscript.core.JsonSystem;
import org.itemscript.core.exceptions.ItemscriptError;

/**
 * NOTE: This class is experimental and the interface <i>will</i> change.
 *  
 * @author Jacob Davies<br/><a href="mailto:jacob@itemscript.org">jacob@itemscript.org</a>
 */
public class FunctionFoundry {
    private static final Map<String, FunctionFactory> factories = new HashMap<String, FunctionFactory>();
    static {
        // FIXME - these should be static instances for functions that do not take arguments.
        factories.put("html", new FunctionFactory() {
            @Override
            public Function create(JsonSystem system, List<Expression> args) {
                return new HtmlEscapeFunction(system);
            }
        });
        FunctionFactory urlEncodeFunctionFactory = new FunctionFactory() {
            @Override
            public Function create(JsonSystem system, List<Expression> args) {
                return new UrlEncodeFunction(system);
            }
        };
        factories.put("url", urlEncodeFunctionFactory);
        factories.put("uri", urlEncodeFunctionFactory);
        factories.put("substring", new FunctionFactory() {
            @Override
            public Function create(JsonSystem system, List<Expression> args) {
                return new SubstringFunction(system, args);
            }
        });
        factories.put("b64id", new FunctionFactory() {
            @Override
            public Function create(JsonSystem system, List<Expression> args) {
                return new B64idFunction(system);
            }
        });
        factories.put("dataUrl", new FunctionFactory() {
            @Override
            public Function create(JsonSystem system, List<Expression> args) {
                return new DataUrlFunction(system);
            }
        });
        factories.put("left", new FunctionFactory() {
            public Function create(JsonSystem system, java.util.List<Expression> args) {
                return new LeftBraceFunction(system);
            }
        });
        factories.put("right", new FunctionFactory() {
            @Override
            public Function create(JsonSystem system, List<Expression> args) {
                return new RightBraceFunction(system);
            }
        });
        factories.put("uuid", new FunctionFactory() {
            @Override
            public Function create(JsonSystem system, List<Expression> args) {
                return new UuidFunction(system);
            }
        });
        factories.put("prettyHtml", new FunctionFactory() {
            @Override
            public Function create(JsonSystem system, List<Expression> args) {
                return new PrettyHtmlFunction(system);
            }
        });
    }

    public static Function create(JsonSystem system, String functionName, List<Expression> args) {
        FunctionFactory factory = factories.get(functionName);
        if (factory == null) { throw new ItemscriptError(
                "error.itemscript.FunctionFoundry.create.factory.not.found", functionName); }
        return factory.create(system, args);
    }

    public static void put(String name, FunctionFactory factory) {
        factories.put(name, factory);
    }
}