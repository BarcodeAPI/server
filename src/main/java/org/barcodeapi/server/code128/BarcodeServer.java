package org.barcodeapi.server.code128;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.barcodeapi.server.cache.ImageCache;
import org.barcodeapi.server.core.CodeType;
import org.barcodeapi.server.gen.CodeGenerator;
import org.barcodeapi.server.gen.types.Code128Generator;
import org.barcodeapi.server.gen.types.DataMatrixGenerator;
import org.barcodeapi.server.gen.types.QRCodeGenerator;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

public class BarcodeServer extends AbstractHandler {

	HashMap<CodeType, CodeGenerator> codeGenerators;

	public BarcodeServer() {

		codeGenerators = new HashMap<CodeType, CodeGenerator>();

		codeGenerators.put(CodeType.Code128, new Code128Generator());
		codeGenerators.put(CodeType.QRCode, new QRCodeGenerator());
		codeGenerators.put(CodeType.DataMatrix, new DataMatrixGenerator());
	}

	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		String data = target.substring(1, target.length());

		CodeType type;

		int typeIndex = data.indexOf("/");
		if (typeIndex > 0) {

			String typeString = target.substring(1, typeIndex + 1);

			type = CodeType.getType(typeString);

			if (type == null) {

				type = CodeType.getType(data);
			} else {

				data = data.substring(typeIndex + 1);
			}
		} else {

			type = CodeType.getType(data);
		}

		// set response okay
		baseRequest.setHandled(true);
		response.setStatus(HttpServletResponse.SC_OK);
		response.addHeader("Server", "BarcodeAPI.org");

		// add headers describing code
		response.addHeader("X-CodeType", type.toString());
		response.addHeader("X-CodeData", data);

		// load from cache
		byte[] image = ImageCache.getInstance().getImage(type, data);

		if (image != null) {

			System.out.println("Served from cache [ " + data + " ]");
		} else {

			// render image
			long start = System.currentTimeMillis();
			image = codeGenerators.get(type).generateCode(data);
			long time = System.currentTimeMillis() - start;

			System.out.println("Rendered [ " + type.toString() + " : " + data + " ] in [ " + time + "ms ]");

			ImageCache.getInstance().addImage(type, data, image);
		}

		// print to stream
		response.setHeader("Content-Type", "image/jpg");
		response.setHeader("Content-Length", Integer.toString(image.length));
		response.getOutputStream().write(image);
	}

}