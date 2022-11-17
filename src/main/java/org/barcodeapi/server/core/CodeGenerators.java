package org.barcodeapi.server.core;

import java.util.HashMap;

import org.barcodeapi.server.gen.CodeGenerator;
import org.barcodeapi.server.gen.CodeType;
import org.barcodeapi.server.gen.types.AztecGenerator;
import org.barcodeapi.server.gen.types.CodabarGenerator;
import org.barcodeapi.server.gen.types.Code128Generator;
import org.barcodeapi.server.gen.types.Code39Generator;
import org.barcodeapi.server.gen.types.DataMatrixGenerator;
import org.barcodeapi.server.gen.types.Ean13Generator;
import org.barcodeapi.server.gen.types.Ean8Generator;
import org.barcodeapi.server.gen.types.PDF417Generator;
import org.barcodeapi.server.gen.types.QRCodeGenerator;
import org.barcodeapi.server.gen.types.RoyalMailGenerator;
import org.barcodeapi.server.gen.types.UPCAGenerator;
import org.barcodeapi.server.gen.types.UPCEGenerator;
import org.barcodeapi.server.gen.types.USPSMailGenerator;

public class CodeGenerators {

	private static CodeGenerators codeGenerators;

	private HashMap<CodeType, CodeGenerator> generators;

	public CodeGenerators() {

		generators = new HashMap<CodeType, CodeGenerator>();

		generators.put(CodeType.EAN8, new Ean8Generator());
		generators.put(CodeType.EAN13, new Ean13Generator());

		generators.put(CodeType.UPC_A, new UPCAGenerator());
		generators.put(CodeType.UPC_E, new UPCEGenerator());

		generators.put(CodeType.CODABAR, new CodabarGenerator());

		generators.put(CodeType.USPSMail, new USPSMailGenerator());
		generators.put(CodeType.RoyalMail, new RoyalMailGenerator());

		generators.put(CodeType.Code39, new Code39Generator());
		generators.put(CodeType.Code128, new Code128Generator());

		generators.put(CodeType.Aztec, new AztecGenerator());
		generators.put(CodeType.QRCode, new QRCodeGenerator());
		generators.put(CodeType.DataMatrix, new DataMatrixGenerator());

		generators.put(CodeType.PDF417, new PDF417Generator());
	}

	public CodeGenerator getGenerator(CodeType codeType) {

		return generators.get(codeType);
	}

	public static synchronized CodeGenerators getInstance() {

		if (codeGenerators == null) {

			codeGenerators = new CodeGenerators();
		}
		return codeGenerators;
	}
}
