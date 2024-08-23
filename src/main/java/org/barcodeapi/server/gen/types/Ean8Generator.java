package org.barcodeapi.server.gen.types;

import org.barcodeapi.core.utils.CodeUtils;
import org.barcodeapi.server.core.CodeType;
import org.barcodeapi.server.core.GenerationException;
import org.barcodeapi.server.core.GenerationException.ExceptionType;
import org.barcodeapi.server.gen.BarcodeRequest;
import org.barcodeapi.server.gen.impl.DefaultBarcodeProvider;
import org.krysalis.barcode4j.impl.upcean.EAN8Bean;

/**
 * Ean8Generator.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class Ean8Generator extends DefaultBarcodeProvider {

	public Ean8Generator(CodeType codeType) {

		// Setup EAN8 generator
		super(codeType, new EAN8Bean());
	}

	@Override
	public void onValidateRequest(BarcodeRequest request) throws GenerationException {
		String data = request.getData();

		if (data.length() == 7) {
			return;
		}

		int provided = (data.charAt(data.length() - 1) - '0');
		int checksum = CodeUtils.calculateEanChecksum(data, 8);

		if (checksum != provided) {
			throw new GenerationException(ExceptionType.CHECKSUM, //
					new Throwable("Expected checksum: " + checksum));
		}
	}
}
