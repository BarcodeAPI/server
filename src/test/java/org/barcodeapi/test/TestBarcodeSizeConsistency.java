package org.barcodeapi.test;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import javax.imageio.ImageIO;

import org.barcodeapi.server.gen.BarcodeGenerator;
import org.barcodeapi.server.cache.CachedBarcode;
import org.junit.Test;

public class TestBarcodeSizeConsistency {
    
    @Test
    public void testCode128SizeConsistency() throws Exception {
        // Test the problematic cases mentioned in the issue
        String[] testCases = {
            "/api/128/12345",
            "/api/128/25000", 
            "/api/128/25000?height=22"
        };
        
        System.out.println("Testing barcode size consistency...");
        
        for (String uri : testCases) {
            System.out.println("Testing URI: " + uri);
            
            try {
                CachedBarcode barcode = BarcodeGenerator.requestBarcode(uri);
                byte[] imageBytes = barcode.getBarcodeData();
                
                // Convert bytes to BufferedImage to get dimensions
                BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
                int width = image.getWidth();
                int height = image.getHeight();
                
                System.out.println("  Dimensions: " + width + "x" + height);
                
                // Save for manual inspection
                File tmpDir = new File("/tmp");
                if (!tmpDir.exists()) {
                    tmpDir.mkdirs();
                }
                String filename = "/tmp/barcode_" + uri.replaceAll("[^a-zA-Z0-9]", "_") + ".png";
                FileOutputStream fos = new FileOutputStream(filename);
                fos.write(imageBytes);
                fos.close();
                System.out.println("  Saved to: " + filename);
                
            } catch (Exception e) {
                System.out.println("  Error: " + e.getMessage());
                e.printStackTrace();
                throw e;
            }
            System.out.println();
        }
    }
}