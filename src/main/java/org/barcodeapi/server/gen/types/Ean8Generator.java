package org.barcodeapi.server.gen.types;

import org.barcodeapi.core.utils.CodeUtils;
import org.barcodeapi.server.core.CodeType;
import org.barcodeapi.server.core.GenerationException;
import org.barcodeapi.server.core.GenerationException.ExceptionType;
import org.barcodeapi.server.gen.impl.Barcode4JProvider;
import org.krysalis.barcode4j.impl.upcean.EAN8Bean;

/**
 * Ean8Generator.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class Ean8Generator extends Barcode4JProvider {

	public Ean8Generator(CodeType codeType) {

		// Setup EAN8 generator
		super(codeType, new EAN8Bean());
	}

	@Override
	public String onValidateRequest(String data) throws GenerationException {

		if (data.length() == 7) {

			return data;
		}

		int checksum = CodeUtils.calculateEanChecksum(data, 8);
		int provided = (data.charAt(data.length() - 1) - '0');

		if (checksum != provided) {

			throw new GenerationException(ExceptionType.CHECKSUM, //
					new Throwable("Expected checksum : " + checksum));
		}

		return data;
	}
}
