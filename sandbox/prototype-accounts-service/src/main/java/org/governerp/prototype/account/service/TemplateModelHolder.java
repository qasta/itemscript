package org.governerp.prototype.account.service;

import java.util.Map;

import org.antlr.stringtemplate.AttributeRenderer;

public class TemplateModelHolder<M> {

	private String name;
	private M model;
	private Map<Class<?>,AttributeRenderer> attributeRenderers;
	
	public TemplateModelHolder(String name, M model){
		this(name,model,null);
	}
	
	public TemplateModelHolder(String name, M model, Map<Class<?>,AttributeRenderer> attrRenders) {
		this.name = name;
		this.model = model;
		this.attributeRenderers = attrRenders;
	}

	public String getName() {
		return name;
	}

	public M getModel() {
		return model;
	}

	public Map<Class<?>, AttributeRenderer> getAttributeRenderers() {
		return attributeRenderers;
	}

}
