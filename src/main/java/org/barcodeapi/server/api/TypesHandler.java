package org.barcodeapi.server.api;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.barcodeapi.server.core.CodeType;
import org.barcodeapi.server.core.CodeTypes;
import org.barcodeapi.server.core.RequestContext;
import org.barcodeapi.server.core.RestHandler;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * TypesHandler.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class TypesHandler extends RestHandler {

	public TypesHandler() {
		super(false, false);
	}

	@Override
	protected void onRequest(RequestContext ctx, HttpServletResponse response) throws JSONException, IOException {

		CodeTypes types = CodeTypes.inst();

		// loop all supported types
		JSONArray output = new JSONArray();
		for (String type : types.getTypes()) {

			output.put(CodeType.toJSON(types.getType(type)));
		}

		// print response to client
		response.setHeader("Content-Type", "application/json");
		response.getOutputStream().println(output.toString(4));
	}
}
