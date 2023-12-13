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
import org.barcodeapi.server.gen.types.ITF14Generator;
import org.barcodeapi.server.gen.types.PDF417Generator;
import org.barcodeapi.server.gen.types.QRCodeGenerator;
import org.barcodeapi.server.gen.types.RoyalMailGenerator;
import org.barcodeapi.server.gen.types.UPCAGenerator;
import org.barcodeapi.server.gen.types.UPCEGenerator;
import org.barcodeapi.server.gen.types.USPSMailGenerator;

import com.mclarkdev.tools.liblog.LibLog;
import com.mclarkdev.tools.libmetrics.LibMetrics;
import com.mclarkdev.tools.libobjectpooler.LibObjectPooler;
import com.mclarkdev.tools.libobjectpooler.LibObjectPoolerController;

public class CodeGenerators {

	private static CodeGenerators codeGenerators;

	public static synchronized CodeGenerators getInstance() {
		if (codeGenerators == null) {
			codeGenerators = new CodeGenerators();
		}
		return codeGenerators;
	}

	private HashMap<CodeType, LibObjectPooler<CodeGenerator>> generators;

	private CodeGenerators() {

		generators = new HashMap<CodeType, LibObjectPooler<CodeGenerator>>();

		generators.put(CodeType.EAN8, createPooler(Ean8Generator.class));
		generators.put(CodeType.EAN13, createPooler(Ean13Generator.class));

		generators.put(CodeType.UPC_A, createPooler(UPCAGenerator.class));
		generators.put(CodeType.UPC_E, createPooler(UPCEGenerator.class));

		generators.put(CodeType.ITF14, createPooler(ITF14Generator.class));

		generators.put(CodeType.CODABAR, createPooler(CodabarGenerator.class));

		generators.put(CodeType.USPSMail, createPooler(USPSMailGenerator.class));
		generators.put(CodeType.RoyalMail, createPooler(RoyalMailGenerator.class));

		generators.put(CodeType.Code39, createPooler(Code39Generator.class));
		generators.put(CodeType.Code128, createPooler(Code128Generator.class));

		generators.put(CodeType.Aztec, createPooler(AztecGenerator.class));
		generators.put(CodeType.QRCode, createPooler(QRCodeGenerator.class));
		generators.put(CodeType.DataMatrix, createPooler(DataMatrixGenerator.class));

		generators.put(CodeType.PDF417, createPooler(PDF417Generator.class));
	}

	private LibObjectPooler<CodeGenerator> createPooler(final Class<? extends CodeGenerator> clazz) {
		LibObjectPooler<CodeGenerator> pooler = new LibObjectPooler<CodeGenerator>(3, //
				new LibObjectPoolerController<CodeGenerator>() {

					@Override
					public CodeGenerator onCreate() {
						LibLog._clogF("I0180", clazz.getName());
						try {
							return clazz.getConstructor().newInstance();
						} catch (Exception | Error e) {
							e.printStackTrace();
							return null;
						}
					}

					@Override
					public void onDestroy(CodeGenerator t) {
						LibLog._clogF("I0181", t.getClass().getName());
					}
				});

		pooler.setMaxLockTime(1000);

		return pooler;
	}

	public LibObjectPooler<CodeGenerator> getGeneratorPool(CodeType codeType) {
		LibMetrics.hitMethodRunCounter();

		return generators.get(codeType);
	}
}
