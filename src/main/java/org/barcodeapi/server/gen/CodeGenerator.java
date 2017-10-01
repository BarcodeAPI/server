package org.barcodeapi.server.gen;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.barcodeapi.server.cache.BarcodeCache;
import org.barcodeapi.server.statistics.StatsCollector;

public abstract class CodeGenerator {

	private final CodeType codeType;

	public CodeGenerator(CodeType type) {

		this.codeType = type;

		BarcodeCache.getInstance().createCache(type);
	}

	public CodeType getType() {

		return codeType;
	}

	public byte[] getCode(String data) {

		StatsCollector.getInstance().incrementCounter("render." + getType().toString());

		String fileName = data.replace(File.separatorChar, '-');
		fileName = getType().toString() + File.separator + fileName;
		fileName = "cache" + File.separator + fileName + ".png";

		File outputFile = new File(fileName);

		if (!onRender(data, outputFile)) {

			return null;
		}

		Path path = Paths.get(fileName);

		try {

			return Files.readAllBytes(path);
		} catch (IOException e) {

			e.printStackTrace();
			return null;
		}
	}

	public abstract boolean onRender(String data, File outputFile);
}
