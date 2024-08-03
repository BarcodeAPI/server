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

	private static final String TYPES;

	static {

		// Loop all supported types
		CodeTypes types = CodeTypes.inst();
		JSONArray output = new JSONArray();
		for (String type : types.getTypes()) {

			output.put(CodeType.toJSON(types.getType(type)));
		}

		// Convert to string, save for later
		TYPES = output.toString();
	}

	public TypesHandler() {
		super();
	}

	@Override
	protected void onRequest(RequestContext c, HttpServletResponse r) throws JSONException, IOException {

		// Print response to client
		r.setStatus(HttpServletResponse.SC_OK);
		r.setHeader("Content-Type", "application/json");
		r.getOutputStream().println(TYPES);
	}
}
