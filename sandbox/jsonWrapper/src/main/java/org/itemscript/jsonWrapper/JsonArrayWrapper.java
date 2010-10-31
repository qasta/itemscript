package org.itemscript.jsonWrapper;


public interface JsonArrayWrapper {
	public abstract int getLength();
	public abstract JsonType getJsonType(int index);
	public abstract String getString(int index);
	public abstract Boolean getBoolean(int index);
	public abstract Double getDouble(int index);
	public abstract Integer getInteger(int index);
	public abstract JsonArrayWrapper getArray(int index);
	public abstract JsonObjectWrapper getObject(int index);
}
