
package org.itemscript.examples.gwt.server;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.itemscript.core.JsonSystem;
import org.itemscript.core.values.JsonValue;
import org.itemscript.standard.MinimalConfig;

public class ReflectJsonServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPutOrPost(req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPutOrPost(req, resp);
    }

    private void doPutOrPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        JsonSystem system = MinimalConfig.createSystem();
        JsonValue value = system.parseReader(req.getReader());
        resp.setContentType("application/json");
        Writer writer = resp.getWriter();
        writer.write(value.toCompactJsonString());
    }
}
