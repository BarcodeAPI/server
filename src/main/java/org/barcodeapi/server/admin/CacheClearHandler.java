package org.barcodeapi.server.admin;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.barcodeapi.server.cache.BarcodeCache;
import org.barcodeapi.server.core.RestHandler;
import org.barcodeapi.server.gen.CodeType;
import org.eclipse.jetty.server.Request;

public class CacheClearHandler extends RestHandler {

	public CacheClearHandler() {
		super();
	}

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		super.handle(target, baseRequest, request, response);

		for (CodeType type : CodeType.values()) {
			BarcodeCache.getCache(type).clearCache();
		}
	}
}
