package org.barcodeapi.server.core;

import java.lang.reflect.Constructor;
import java.util.HashMap;

import org.barcodeapi.core.AppConfig;
import org.barcodeapi.server.gen.CodeGenerator;
import org.json.JSONArray;

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

	private HashMap<String, LibObjectPooler<CodeGenerator>> generators = new HashMap<>();

	private CodeGenerators() {

		JSONArray enabled = AppConfig.get().getJSONArray("types");

		// loop all enabled types
		for (int x = 0; x < enabled.length(); x++) {

			String type = enabled.getString(x);

			// load code type from config
			CodeType config = CodeTypes.inst().loadType(type);

			// setup a pooler for code type
			generators.put(type, setupBarcodePooler(config));
		}
	}

	@SuppressWarnings("unchecked")
	private LibObjectPooler<CodeGenerator> setupBarcodePooler(CodeType codeType) {

		final Class<? extends CodeGenerator> clazz;
		final Constructor<? extends CodeGenerator> constructor;

		try {

			String genClass = codeType.getGeneratorClass();
			clazz = (Class<? extends CodeGenerator>) Class.forName(genClass);
			constructor = clazz.getDeclaredConstructor();

		} catch (Exception e) {
			throw LibLog._log("Failed setting up generator.", e).asException();
		}

		LibObjectPooler<CodeGenerator> pooler = new LibObjectPooler<CodeGenerator>(3, //
				new LibObjectPoolerController<CodeGenerator>() {

					@Override
					public CodeGenerator onCreate() {
						LibLog._clogF("I0180", clazz.getName());
						try {
							return constructor.newInstance();
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
		pooler.setMaxIdleTime(15 * 60 * 1000);
		pooler.setMaxPoolSize(codeType.getNumThreads());

		return pooler;
	}

	public LibObjectPooler<CodeGenerator> getGeneratorPool(String type) {
		LibMetrics.hitMethodRunCounter();

		return generators.get(type);
	}
}
