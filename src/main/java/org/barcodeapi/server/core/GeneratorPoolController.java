package org.barcodeapi.server.core;

import java.lang.reflect.Constructor;

import org.barcodeapi.server.gen.CodeGenerator;

import com.mclarkdev.tools.liblog.LibLog;
import com.mclarkdev.tools.libmetrics.LibMetrics;
import com.mclarkdev.tools.libobjectpooler.LibObjectPoolerController;

public class GeneratorPoolController implements LibObjectPoolerController<CodeGenerator> {

	private final CodeType type;
	private final Class<? extends CodeGenerator> clazz;
	private final Constructor<? extends CodeGenerator> constructor;

	@SuppressWarnings("unchecked")
	public GeneratorPoolController(CodeType type) {
		this.type = type;

		try {

			this.clazz = (Class<? extends CodeGenerator>) Class.forName(type.getGeneratorClass());
			this.constructor = clazz.getDeclaredConstructor(CodeType.class);

		} catch (Exception e) {

			throw LibLog._clog("E0059", e).asException();
		}
	}

	@Override
	public CodeGenerator onCreate() {

		LibLog._clogF("I0180", type.getName());
		LibMetrics.instance().hitCounter("generators", type.getName(), "pool", "created");

		try {

			return constructor.newInstance(new Object[] { type });
		} catch (Exception | Error e) {

			LibLog._log("Failed to create generator.", e);
			return null;
		}
	}

	@Override
	public void onDestroy(CodeGenerator t) {

		LibLog._clogF("I0181", type.getName());
		LibMetrics.instance().hitCounter("generators", type.getName(), "pool", "destroyed");
	}
}
