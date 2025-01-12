package org.barcodeapi.server.core;

import java.util.HashMap;

import org.barcodeapi.core.AppConfig;
import org.barcodeapi.server.gen.CodeGenerator;
import org.json.JSONArray;

import com.mclarkdev.tools.liblog.LibLog;
import com.mclarkdev.tools.libmetrics.LibMetrics;
import com.mclarkdev.tools.libobjectpooler.LibObjectPooler;

/**
 * CodeGenerators.java
 * 
 * Initializes all supported CodeTypes from configuration and provides access to
 * their associated generation object pools.
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class CodeGenerators {

	private static CodeGenerators codeGenerators;

	/**
	 * Returns an instance of the CodeGenerators map.
	 * 
	 * @return an instance of the CodeGenerators map
	 */
	public static synchronized CodeGenerators getInstance() {
		if (codeGenerators == null) {
			codeGenerators = new CodeGenerators();
		}
		return codeGenerators;
	}

	/**
	 * A map of generation pools for all supported CodeTypes.
	 */
	private HashMap<CodeType, LibObjectPooler<CodeGenerator>> generators = new HashMap<>();

	private CodeGenerators() {

		JSONArray enabled = AppConfig.get().getJSONArray("types");

		// Loop all enabled types
		for (int x = 0; x < enabled.length(); x++) {

			try {

				// Load type from config and setup pooler
				String type = enabled.getString(x);
				CodeType config = CodeTypes.inst().loadType(type);
				generators.put(config, setupBarcodePooler(config));

			} catch (Exception | Error e) {

				// Log initialization failure
				LibLog._clog("E0051", e);
			}
		}
	}

	/**
	 * Creates a new object pool for the given CodeType.
	 * 
	 * @param codeType type of pooler to create
	 * @return the created pooler
	 */
	private LibObjectPooler<CodeGenerator> setupBarcodePooler(final CodeType codeType) {

		LibObjectPooler<CodeGenerator> pooler = //
				new LibObjectPooler<CodeGenerator>(3, new GeneratorPoolController(codeType));

		pooler.setMaxLockTime(1000);
		pooler.setMaxIdleTime(15 * 60 * 1000);
		pooler.setMaxPoolSize(codeType.getNumThreads());

		return pooler;
	}

	/**
	 * Returns the generation pool for the given CodeType.
	 * 
	 * @param type type of pooler to get
	 * @return the generation pool
	 */
	public LibObjectPooler<CodeGenerator> getGeneratorPool(CodeType type) {
		LibMetrics.hitMethodRunCounter();

		return generators.get(type);
	}
}
