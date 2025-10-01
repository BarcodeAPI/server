package org.barcodeapi.server.gen.types;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.barcodeapi.server.core.CodeType;
import org.barcodeapi.server.core.GenerationException;
import org.barcodeapi.server.core.GenerationException.ExceptionType;
import org.barcodeapi.server.gen.BarcodeRequest;
import org.barcodeapi.server.gen.CodeGenerator;
import org.json.JSONObject;

import com.google.zxing.WriterException;
import com.mclarkdev.tools.libapriltag.TagFamily;
import com.mclarkdev.tools.libapriltag.families.Tag16h5;
import com.mclarkdev.tools.libapriltag.families.Tag25h9;
import com.mclarkdev.tools.libapriltag.families.Tag36h10;
import com.mclarkdev.tools.libapriltag.families.Tag36h11;
import com.mclarkdev.tools.libapriltag.families.Tag36h9;
import com.mclarkdev.tools.libapriltag.families.TagCircle21h7;
import com.mclarkdev.tools.libapriltag.families.TagCircle49h12;
import com.mclarkdev.tools.libapriltag.families.TagCustom48h12;
import com.mclarkdev.tools.libapriltag.families.TagStandard41h12;
import com.mclarkdev.tools.libapriltag.families.TagStandard52h13;

/**
 * AztecGenerator.java
 * 
 * @author Matthew R. Clark (BarcodeAPI.org, 2017-2024)
 */
public class AprilTagGenerator extends CodeGenerator {

	private final HashMap<String, TagFamily> tagFamilies;

	public AprilTagGenerator(CodeType codeType) {
		super(codeType);

		tagFamilies = new HashMap<>();

		// Basic tag families
		tagFamilies.put("tag16h5", new Tag16h5());
		tagFamilies.put("tag25h9", new Tag25h9());
		tagFamilies.put("tag36h9", new Tag36h9());
		tagFamilies.put("tag36h10", new Tag36h10());
		tagFamilies.put("tag36h11", new Tag36h11());

		// Extended tag families
		tagFamilies.put("tagCircle21h7", new TagCircle21h7());
		tagFamilies.put("tagCircle49h12", new TagCircle49h12());
		tagFamilies.put("tagCustom48h12", new TagCustom48h12());
		tagFamilies.put("tagStandard41h12", new TagStandard41h12());
		tagFamilies.put("tagStandard52h13", new TagStandard52h13());
	}

	@Override
	public byte[] onRender(BarcodeRequest request) throws WriterException, IOException, GenerationException {

		String[] parts = request.getData().split(":");

		String family = parts[0];
		int id = Integer.parseInt(parts[1]);

		JSONObject options = request.getOptions();

		HashMap<String, Object> defaults = //
				request.getType().getDefaults();

		TagFamily tagFamily = tagFamilies.get(family);

		if (tagFamily == null) {
			throw new GenerationException(ExceptionType.INVALID, //
					new Throwable("Unsupported tag type."));
		}

		int scale = options.optInt("scale", //
				(Integer) defaults.getOrDefault("scale", 8));

		BufferedImage img = tagFamily.getLayout().renderToImage(tagFamily.getCodes()[id], scale);

		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(img, "png", baos);
			baos.close();
			return baos.toByteArray();
		} catch (Exception e) {
			return null;
		}
	}
}
