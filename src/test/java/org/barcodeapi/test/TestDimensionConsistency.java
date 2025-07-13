package org.barcodeapi.test;

import org.barcodeapi.server.gen.BarcodeCanvasProvider;
import org.krysalis.barcode4j.BarcodeDimension;
import org.junit.Test;
import org.junit.Assert;

/**
 * Test for the barcode size consistency fix.
 */
public class TestDimensionConsistency {
    
    @Test
    public void testDimensionStandardization() {
        // Create test dimensions that simulate the problematic case
        // Original issue: different content resulted in different heights
        
        // Simulate dimension for "12345" - 234x130 (total height ~30)
        BarcodeDimension dim1 = new BarcodeDimension(58.5, 22.0, 62.5, 30.0, 0, 0);
        
        // Simulate dimension for "25000" - 234x168 (total height ~38) 
        BarcodeDimension dim2 = new BarcodeDimension(58.5, 22.0, 62.5, 38.0, 0, 0);
        
        // Create canvas provider and test standardization
        BarcodeCanvasProvider provider = new BarcodeCanvasProvider(150, "ffffff", "000000");
        
        // Use reflection to call the private standardization method
        try {
            java.lang.reflect.Method method = BarcodeCanvasProvider.class
                .getDeclaredMethod("createStandardizedDimension", BarcodeDimension.class);
            method.setAccessible(true);
            
            BarcodeDimension standardized1 = (BarcodeDimension) method.invoke(provider, dim1);
            BarcodeDimension standardized2 = (BarcodeDimension) method.invoke(provider, dim2);
            
            System.out.println("Original dim1 (12345): " + dim1.getHeightPlusQuiet());
            System.out.println("Original dim2 (25000): " + dim2.getHeightPlusQuiet());
            System.out.println("Standardized dim1: " + standardized1.getHeightPlusQuiet());
            System.out.println("Standardized dim2: " + standardized2.getHeightPlusQuiet());
            
            // The standardized dimensions should have consistent heights
            Assert.assertEquals("Standardized heights should be consistent", 
                               standardized1.getHeightPlusQuiet(), 
                               standardized2.getHeightPlusQuiet(), 
                               0.1); // Allow small floating point tolerance
            
            // Bar height should remain unchanged
            Assert.assertEquals("Bar height should remain unchanged for dim1", 
                               dim1.getHeight(), standardized1.getHeight(), 0.1);
            Assert.assertEquals("Bar height should remain unchanged for dim2", 
                               dim2.getHeight(), standardized2.getHeight(), 0.1);
            
            // Width should remain unchanged
            Assert.assertEquals("Width should remain unchanged for dim1", 
                               dim1.getWidth(), standardized1.getWidth(), 0.1);
            Assert.assertEquals("Width should remain unchanged for dim2", 
                               dim2.getWidth(), standardized2.getWidth(), 0.1);
                               
        } catch (Exception e) {
            Assert.fail("Failed to test dimension standardization: " + e.getMessage());
        }
    }
    
    @Test 
    public void testMinimalVariationPreserved() {
        // Test that minimal variations are preserved (tolerance check)
        
        // Two dimensions with small difference (within tolerance)
        BarcodeDimension dim1 = new BarcodeDimension(58.5, 22.0, 62.5, 30.0, 0, 0);
        BarcodeDimension dim2 = new BarcodeDimension(58.5, 22.0, 62.5, 30.5, 0, 0); // 0.5 difference
        
        BarcodeCanvasProvider provider = new BarcodeCanvasProvider(150, "ffffff", "000000");
        
        try {
            java.lang.reflect.Method method = BarcodeCanvasProvider.class
                .getDeclaredMethod("createStandardizedDimension", BarcodeDimension.class);
            method.setAccessible(true);
            
            BarcodeDimension standardized1 = (BarcodeDimension) method.invoke(provider, dim1);
            BarcodeDimension standardized2 = (BarcodeDimension) method.invoke(provider, dim2);
            
            // Small variations should be preserved (not standardized)
            Assert.assertEquals("Small variation should be preserved", 
                               dim1.getHeightPlusQuiet(), 
                               standardized1.getHeightPlusQuiet(), 0.1);
            Assert.assertEquals("Small variation should be preserved", 
                               dim2.getHeightPlusQuiet(), 
                               standardized2.getHeightPlusQuiet(), 0.1);
                               
        } catch (Exception e) {
            Assert.fail("Failed to test variation preservation: " + e.getMessage());
        }
    }
}