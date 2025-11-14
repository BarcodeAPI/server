package org.barcodeapi.server.api;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.http.HttpServletResponse;

import org.barcodeapi.server.core.CodeType;
import org.barcodeapi.server.core.CodeTypes;
import org.barcodeapi.server.core.RequestContext;
import org.barcodeapi.server.core.RestHandler;
import org.barcodeapi.server.core.TypeSelector;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * TypeHandler.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class TypeHandler extends RestHandler {

	// Static map of JSON responses
	private static final HashMap<CodeType, String> typesConfig;

	private static final String typesComplete;

	static {
		typesConfig = new HashMap<>();
		CodeTypes types = CodeTypes.inst();

		// Convert each type to JSON, and cache
		JSONArray complete = new JSONArray();
		for (String type : types.getTypes()) {
			CodeType t = types.getType(type);
			JSONObject details = CodeType.toJSON(t);

			complete.put(details);

			typesConfig.put(t, details.toString());
		}

		typesComplete = complete.toString();
	}

	public TypeHandler() {
		super();
	}

	@Override
	protected void onRequest(RequestContext c, HttpServletResponse r) throws JSONException, IOException {

		// Check for user requested type string
		String typeStr = c.getRequest().getParameter("type");
		if (typeStr == null || typeStr.trim().equals("")) {

			// Print all types response to client
			r.setStatus(HttpServletResponse.SC_OK);
			r.setContentType("application/json");
			r.getOutputStream().println(typesComplete);
			return;
		}

		// Lookup requested type string
		CodeType type = TypeSelector.getTypeFromString(typeStr);

		// Fail if type not found
		if (type == null) {

			// Print error to client
			r.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		// Print single type response to client
		r.setStatus(HttpServletResponse.SC_OK);
		r.setContentType("application/json");
		r.getOutputStream().println(typesConfig.get(type));
	}
}
