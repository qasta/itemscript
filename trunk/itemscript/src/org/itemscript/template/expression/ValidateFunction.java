
package org.itemscript.template.expression;

import java.util.List;

import org.itemscript.core.JsonSystem;
import org.itemscript.core.Params;
import org.itemscript.core.exceptions.ItemscriptError;
import org.itemscript.core.values.JsonObject;
import org.itemscript.core.values.JsonValue;
import org.itemscript.schema.Schema;
import org.itemscript.schema.Type;
import org.itemscript.template.TemplateExec;

public class ValidateFunction extends FunctionBase {
	Schema schema;
    public ValidateFunction(JsonSystem system, List<Expression> args) {
        super(system, args);
        schema = new Schema(system);
    }

    @Override
    public JsonValue execute(TemplateExec templateExec, JsonValue context, JsonValue value) {
        if (value == null) { throw ItemscriptError.internalError(this,
                "execute.value.was.null", value + ""); }
        if (!(value.isString() || value.isNumber() || value.isBoolean() || value.isArray()
        		|| value.isObject() || value.isNull())) {
        	throw ItemscriptError.internalError(this,
        		"execute.value.was.not.of.valid.format", value + ""); }
        
        if (args().size() != 1) { throw ItemscriptError.internalError(this,
        		"execute.only.one.arg.allowed"); }
        
        JsonValue typeValue = args().get(0)
        	.interpret(templateExec, context);
    	Type type;
    	try {
    		type = schema.resolve(typeValue);
    	} catch (ItemscriptError e) {
    		return onError(e);
    	}
    	try {
    		schema.validate(type, value);
    		return onSuccess();
    	} catch (ItemscriptError e) {
    		return onError(e);
    	}
    }
        
    private JsonObject onError(Throwable e) {
        JsonObject errorObject = system().createObject();
        String errorMessage = e.getMessage();
        errorObject.put("message", errorMessage);
        errorObject.put("class", e.getClass() + "");
        errorObject.put("valid", false);
        Params params = (Params) ((ItemscriptError) e).params();
        errorObject.put("correctValue", params.get("correctValue"));
        errorObject.put("incorrectValue", params.get("incorrectValue"));
        errorObject.put("value", params.get("value"));
        errorObject.put("parseValue", params.get("parseValue"));
        errorObject.put("keyValue", params.get("key"));
        if (e instanceof ItemscriptError) {
            errorObject.put("message", ((ItemscriptError) e).key());
        }
        return errorObject;
    }
    
    private JsonObject onSuccess() {
    	JsonObject successObject = system().createObject();
        successObject.put("valid", true);
        successObject.put("message", "validated successfully");
        return successObject;
    }
}