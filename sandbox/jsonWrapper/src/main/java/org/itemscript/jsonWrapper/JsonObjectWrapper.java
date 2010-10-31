package org.itemscript.jsonWrapper;

import java.util.Collection;


public interface JsonObjectWrapper {
	public abstract Collection<String> getKeys();
	public abstract JsonObjectWrapper getObject(String key);
	public abstract JsonType getJsonType(String key);
	public abstract String getString(String key);
	public abstract Boolean getBoolean(String key);
	public abstract Double getDouble(String key);
	public abstract Integer getInteger(String key);
	public abstract JsonArrayWrapper getArray(String key);
}
