package org.governerp.prototype.account.service;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/a/")
public class TestService {

	@GET
	@Path("zzz")
	public TemplateModelHolder<Map<String,String>> zzz(){
		Map<String,String> model = new HashMap<String,String>();
		model.put("name", "Dave");
		return new TemplateModelHolder<Map<String,String>>("test",model);
	}
}
