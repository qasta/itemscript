package org.governerp.prototype.account.service;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.servlet.ServletContext;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;

@Provider
@Produces("*/*")
public class StringTemplateBodyWriter implements MessageBodyWriter<TemplateModelHolder<?>> {

	@Context
	private ServletContext context;
	private StringTemplateGroup stringTemplateGroup;
	private String groupDir;
	
	public StringTemplateBodyWriter(String dir){
		groupDir = dir;
	}
	
	public long getSize(TemplateModelHolder<?> modelHolder, Class<?> type, Type genericType, Annotation[] annotations,
			MediaType mediaType) {
		return -1;
	}

	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations,
			MediaType mediaType) {
		return type.isAssignableFrom(TemplateModelHolder.class);
	}
	
	private void setup(){
		if(stringTemplateGroup == null){
			String templatePath = context.getRealPath("/"+groupDir);
			stringTemplateGroup = new StringTemplateGroup("underwebinf", templatePath);
		}
	}

	public void writeTo(TemplateModelHolder<?> modelHolder, Class<?> type, Type genericType, Annotation[] annotations,
			MediaType mediaType, MultivaluedMap<String, Object> httpHeaders,
			OutputStream out) throws IOException, WebApplicationException {
		setup();
		StringTemplate tmp = stringTemplateGroup.getInstanceOf(modelHolder.getName());
		tmp.setAttribute("model", modelHolder.getModel());
		if(modelHolder.getAttributeRenderers() != null){
			for(Class<?> c:modelHolder.getAttributeRenderers().keySet()){
				tmp.registerRenderer(c, modelHolder.getAttributeRenderers().get(c));
			}
		}
		out.write(tmp.toString().getBytes());	
	}

}
