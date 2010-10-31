package org.itemscript.jacksonJsonWrapper;

import org.codehaus.jackson.JsonNode;
import org.itemscript.jsonWrapper.JsonArrayWrapper;
import org.itemscript.jsonWrapper.JsonObjectWrapper;
import org.itemscript.jsonWrapper.JsonType;

public class JacksonBase {

	protected JsonType getJsonType(JsonNode n){
		if(n !=null){
			if(n.isArray()){
				return JsonType.ARRAY;
			}else if(n.isBoolean()){
				return JsonType.BOOLEAN;
			}else if(n.isNull()){
				return JsonType.NULL;
			}else if(n.isNumber()){
				return JsonType.NUMBER;
			}else if(n.isObject()){
				return JsonType.OBJECT;
			}else if(n.isTextual()){
				return JsonType.STRING;
			}
		}
		return null;
	}
	
	protected JsonObjectWrapper getObjectCallback(JsonNode n) {
		if(n !=null && n.isObject()){
			return new JacksonObjectWrapper(n);
		}
		return null;
	}
	
	protected JsonArrayWrapper getArray(JsonNode n) {
		if(n !=null && n.isArray()){
			return new JacksonArrayWrapper(n);
		}
		return null;
	}

	protected Double getDouble(JsonNode n) {
		if(n !=null && n.isNumber()){
			return n.getDoubleValue();
		}
		return null;
	}

	protected Integer getInteger(JsonNode n) {
		if(n !=null && n.isNumber()){
			return n.getIntValue();
		}
		return null;
	}

	protected Boolean getBoolean(JsonNode n) {
		if(n !=null && n.isBoolean()){
			return n.isBoolean();
		}
		return null;
	}

	protected String getString(JsonNode n) {
		if(n !=null && n.isTextual()){
			return n.getValueAsText();
		}
		return null;
	}

}
