package org.barcodeapi.server.api;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.barcodeapi.server.core.RestHandler;
import org.barcodeapi.server.gen.CodeType;
import org.eclipse.jetty.server.Request;
import org.json.JSONArray;
import org.json.JSONObject;

public class TypesHandler extends RestHandler {

	public TypesHandler() {
		super();
	}

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		super.handle(target, baseRequest, request, response);

		JSONArray output = new JSONArray();
		for (CodeType type : CodeType.values()) {
			output.put(new JSONObject()//
					.put("name", type.name())//
					.put("target", type.getTypeStrings()[0])//
					.put("pattern", type.getFormatPattern())//
					.put("description", type.getDescription()));
		}

		response.getOutputStream().println(output.toString());
	}
}
