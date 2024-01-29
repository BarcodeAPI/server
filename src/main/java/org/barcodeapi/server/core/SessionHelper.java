package org.barcodeapi.server.core;

import java.util.Base64;

import javax.servlet.http.Cookie;

import org.barcodeapi.core.AppConfig;
import org.barcodeapi.server.session.CachedSession;
import org.barcodeapi.server.session.SessionCache;
import org.eclipse.jetty.server.Request;
import org.json.JSONObject;

import com.mclarkdev.tools.libextras.LibExtrasHashes;

public class SessionHelper {

	public static CachedSession getSession(Request request) {

		// get existing user session
		CachedSession session = null;
		if (request.getCookies() != null) {
			for (Cookie cookie : request.getCookies()) {
				if (cookie.getName().equals("session")) {
					session = SessionCache.getSession(cookie.getValue());
				}
			}
		}

		// new session if none existing
		if (session == null) {
			session = SessionCache.createNewSession();
		}

		return session;
	}

	public static boolean validateAdmin(Request request) {

		// false if no authentication
		String auth = request.getHeader("Authorization");
		if (auth == null || !auth.startsWith("Basic")) {
			return false;
		}

		String authString = auth.substring(6);
		String decode = new String(Base64.getDecoder().decode(authString));
		String[] unpw = decode.split(":");

		String uName = unpw[0];
		String passHash = LibExtrasHashes.sumSHA256(unpw[1].getBytes());

		JSONObject admins = AppConfig.get().getJSONObject("admins");
		return admins.getString(uName).equals(passHash);
	}
}
