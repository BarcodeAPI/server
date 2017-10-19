package org.barcodeapi.server.core;

import org.barcodeapi.server.gen.CodeType;

public class TypeSelector {

	/**
	 * Returns a CodeType object best suited for the given data string.
	 * 
	 * @param data
	 * @return
	 */
	public static CodeType getType(String data) {

		// Match UPC-E format
		if (data.matches(CodeType.UPC_E.getAutomatchPattern())) {

			return CodeType.UPC_E;
		}

		// Match UPC-A format
		if (data.matches(CodeType.UPC_A.getAutomatchPattern())) {

			return CodeType.UPC_A;
		}

		// Match EAN-8 format
		if (data.matches(CodeType.EAN8.getAutomatchPattern())) {

			// TODO validate checksum
			return CodeType.EAN8;
		}

		// Match EAN-13 format
		if (data.matches(CodeType.EAN13.getAutomatchPattern())) {

			// TODO validate checksum
			return CodeType.EAN13;
		}

		// Match Codabar format
		if (data.matches(CodeType.CODABAR.getAutomatchPattern())) {

			return CodeType.CODABAR;
		}

		// Match Code39 format
		if (data.matches(CodeType.Code39.getAutomatchPattern())) {

			return CodeType.Code39;
		}

		// Match Code128 format
		if (data.matches(CodeType.Code128.getAutomatchPattern())) {

			return CodeType.Code128;
		}

		// Match QR format
		if (data.matches(CodeType.QRCode.getAutomatchPattern())) {

			return CodeType.QRCode;
		}

		// Match DataMatrix format
		if (data.matches(CodeType.DataMatrix.getAutomatchPattern())) {

			return CodeType.DataMatrix;
		}

		// Return null on no matches
		return null;
	}
}
