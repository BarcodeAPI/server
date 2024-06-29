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

		// loop all supported types
		CodeTypes types = CodeTypes.inst();
		JSONArray output = new JSONArray();
		for (String type : types.getTypes()) {

			output.put(CodeType.toJSON(types.getType(type)));
		}

		// convert to string and cache
		TYPES = output.toString(4);
	}

	public TypesHandler() {
		super(false, false);
	}

	@Override
	protected void onRequest(RequestContext ctx, HttpServletResponse response) throws JSONException, IOException {

		// print types string to client
		response.setHeader("Content-Type", "application/json");
		response.getOutputStream().println(TYPES);
	}
}
