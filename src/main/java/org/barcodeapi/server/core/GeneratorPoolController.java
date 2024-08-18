package org.barcodeapi.server.core;

import java.lang.reflect.Constructor;

import org.barcodeapi.server.gen.CodeGenerator;

import com.mclarkdev.tools.liblog.LibLog;
import com.mclarkdev.tools.libmetrics.LibMetrics;
import com.mclarkdev.tools.libobjectpooler.LibObjectPoolerController;

/**
 * GeneratorPoolController.java
 * 
 * Manages the creation and destruction of pooled code generators.
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class GeneratorPoolController implements LibObjectPoolerController<CodeGenerator> {

	private final CodeType type;
	private final Class<? extends CodeGenerator> clazz;
	private final Constructor<? extends CodeGenerator> constructor;

	@SuppressWarnings("unchecked")
	public GeneratorPoolController(CodeType type) {

		try {

			this.type = type;
			this.clazz = (Class<? extends CodeGenerator>) Class.forName(type.getGeneratorClass());
			this.constructor = clazz.getDeclaredConstructor(CodeType.class);

		} catch (Exception e) {

			// Log the initialization failure
			throw LibLog._clog("E0059", e).asException();
		}
	}

	@Override
	public CodeGenerator onCreate() {
		LibMetrics.hitMethodRunCounter();

		// Log the object creation
		LibLog._clogF("I0180", type.getName());
		LibMetrics.instance().hitCounter("generators", type.getName(), "pool", "created");

		try {

			// Create and return new object instance
			return constructor.newInstance(new Object[] { type });
		} catch (Exception | Error e) {

			// Log the failure
			LibLog._log("Failed to create generator.", e);
			return null;
		}
	}

	@Override
	public void onDestroy(CodeGenerator t) {
		LibMetrics.hitMethodRunCounter();

		// Log the object destruction
		LibLog._clogF("I0181", type.getName());
		LibMetrics.instance().hitCounter("generators", type.getName(), "pool", "destroyed");
	}
}
