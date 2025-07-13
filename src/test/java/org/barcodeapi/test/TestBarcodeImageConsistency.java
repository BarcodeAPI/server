package org.barcodeapi.test;

import org.junit.Test;
import org.junit.Assert;
import org.barcodeapi.server.gen.BarcodeCanvasProvider;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import javax.imageio.ImageIO;

/**
 * Integration test to validate that the barcode size consistency fix works
 * in the context of actual image generation.
 */
public class TestBarcodeImageConsistency {
    
    @Test
    public void testConsistentImageDimensions() throws Exception {
        // Test that when the same parameters are used, images have consistent dimensions
        // even with different barcode content
        
        // Create canvas providers with the same settings
        BarcodeCanvasProvider provider1 = new BarcodeCanvasProvider(150, "ffffff", "000000");
        BarcodeCanvasProvider provider2 = new BarcodeCanvasProvider(150, "ffffff", "000000");
        
        // Test with the problematic dimension scenarios from the issue
        // These simulate what would happen with "12345" vs "25000"
        
        // Scenario 1: Normal case (like "12345")
        org.krysalis.barcode4j.BarcodeDimension normalDim = 
            new org.krysalis.barcode4j.BarcodeDimension(58.5, 22.0, 62.5, 30.0, 0, 0);
        
        // Scenario 2: Problematic case (like "25000") 
        org.krysalis.barcode4j.BarcodeDimension problematicDim = 
            new org.krysalis.barcode4j.BarcodeDimension(58.5, 22.0, 62.5, 38.0, 0, 0);
        
        // Establish dimensions - this is where our fix applies
        provider1.establishDimensions(normalDim);
        provider2.establishDimensions(problematicDim);
        
        // Get the resulting images (mock data since we're testing dimension consistency)
        byte[] mockImageData1 = provider1.finish();
        byte[] mockImageData2 = provider2.finish();
        
        // Convert to BufferedImages and check dimensions
        BufferedImage image1 = ImageIO.read(new ByteArrayInputStream(mockImageData1));
        BufferedImage image2 = ImageIO.read(new ByteArrayInputStream(mockImageData2));
        
        int width1 = image1.getWidth();
        int height1 = image1.getHeight();
        int width2 = image2.getWidth();
        int height2 = image2.getHeight();
        
        System.out.println("Image 1 (normal): " + width1 + "x" + height1);
        System.out.println("Image 2 (problematic): " + width2 + "x" + height2);
        
        // The heights should now be consistent due to our fix
        Assert.assertEquals("Image heights should be consistent after dimension standardization", 
                           height1, height2);
        
        // Widths should be the same since we used the same input dimensions
        Assert.assertEquals("Image widths should be the same for same input dimensions", 
                           width1, width2);
        
        System.out.println("✓ Barcode size consistency fix validated!");
    }
}