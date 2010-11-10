package org.itemscript.jacksonJsonWrapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.codehaus.jackson.JsonNode;

import org.itemscript.jsonWrapper.JsonObjectWrapper;
import org.itemscript.jsonWrapper.JsonArrayWrapper;
import org.itemscript.jsonWrapper.JsonType;

public class JacksonObjectWrapper extends JacksonBase implements
		JsonObjectWrapper {

	private JsonNode node;

	public JacksonObjectWrapper(JsonNode n) {
		this.node = n;
	}

	public JsonArrayWrapper getArray(String key) {
		return getArray(node.get(key));
	}

	public Double getDouble(String key) {
		return getDouble(node.get(key));
	}

	public Integer getInteger(String key) {
		return getInteger(node.get(key));
	}

	public Boolean getBoolean(String key) {
		return getBoolean(node.get(key));
	}

	public JsonType getJsonType(String key) {
		return getJsonType(node.get(key));
	}

	public Collection<String> getKeys() {
		List<String> result = new ArrayList<String>();
		Iterator<String> iterator = node.getFieldNames();
		while (iterator.hasNext()) {
			result.add(iterator.next());
		}
		return Collections.unmodifiableList(result);
	}

	public JsonObjectWrapper getObject(String key) {
		return getObjectCallback(node.get(key));
	}

	public String getString(String key) {
		return getString(node.get(key));
	}

}
