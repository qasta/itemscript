package org.itemscript.jacksonJsonWrapper;


import org.codehaus.jackson.JsonNode;
import org.itemscript.jsonWrapper.JsonArrayWrapper;
import org.itemscript.jsonWrapper.JsonObjectWrapper;
import org.itemscript.jsonWrapper.JsonType;


public class JacksonArrayWrapper extends JacksonBase implements JsonArrayWrapper {

	private JsonNode node;
	
	public JacksonArrayWrapper(JsonNode n){
		this.node = n;
	}

	public JsonArrayWrapper getArray(int index) {
		return getArray(node.get(index));
	}

	public Double getDouble(int index) {
		return getDouble(node.get(index));
	}

	public Integer getInteger(int index) {
		return getInteger(node.get(index));
	}

	public Boolean getBoolean(int index) {
		return getBoolean(node.get(index));
	}

	public JsonType getJsonType(int index) {
		return getJsonType(node.get(index));
	}

	public int getLength() {
		return node.size();
	}

	public String getString(int index) {
		return getString(node.get(index));
	}

	public JsonObjectWrapper getObject(int index) {
		return new JacksonObjectWrapper(node.get(index));
	}
}
