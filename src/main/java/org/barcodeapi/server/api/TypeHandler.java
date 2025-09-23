package org.barcodeapi.server.api;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.http.HttpServletResponse;

import org.barcodeapi.server.core.CodeType;
import org.barcodeapi.server.core.CodeTypes;
import org.barcodeapi.server.core.RequestContext;
import org.barcodeapi.server.core.RestHandler;
import org.barcodeapi.server.core.TypeSelector;
import org.json.JSONException;

/**
 * TypeHandler.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class TypeHandler extends RestHandler {

	// Static map of JSON responses
	private static final HashMap<CodeType, String> asJSON;

	static {
		asJSON = new HashMap<>();
		CodeTypes types = CodeTypes.inst();

		// Convert each type to JSON, and cache
		for (String type : types.getTypes()) {
			CodeType t = types.getType(type);
			asJSON.put(t, CodeType.toJSON(t).toString());
		}
	}

	public TypeHandler() {
		super();
	}

	@Override
	protected void onRequest(RequestContext c, HttpServletResponse r) throws JSONException, IOException {

		String typeStr = c.getRequest().getParameter("type");
		CodeType type = TypeSelector.getTypeFromString(typeStr);

		if (type == null) {

			// Print error to client
			r.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		// Print response to client
		r.setStatus(HttpServletResponse.SC_OK);
		r.setHeader("Content-Type", "application/json");
		r.getOutputStream().println(asJSON.get(type));
	}
}
