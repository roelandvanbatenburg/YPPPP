package nl.unreadable.YPPPP;

import nl.unreadable.YPPPP.model.YPPPPPirate;
import nl.unreadable.YPPPP.parser.PiratePageParser;
import org.junit.Test;
import static org.junit.Assert.*;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

public class PiratePageParserTest {
    
    @Test
    public void testParseActualPiratePage() throws IOException {
        // Load the actual HTML content from test resources
        String htmlContent = loadTestHtmlFile();
        
        // Parse the pirate information
        YPPPPPirate pirate = PiratePageParser.parseFromHtml(htmlContent);
        
        // Verify pirate name
        assertNotNull("Pirate should not be null", pirate);
        assertEquals("Pirate name should be Btza", "Btza", pirate.getName());
        
        // Verify skill ratings based on the HTML content
        // From the HTML: <b>Paragon</b>/<b>Ultimate</b>
        assertEquals("Sailing should be Ultimate (7)", 7, pirate.getSailing());
        
        // From the HTML: <b>Solid</b>/<b>Legendary</b>
        assertEquals("Rigging should be Legendary (6)", 6, pirate.getRigging());
        
        // From the HTML: <i><b>Sublime</b></i>/<b>Ultimate</b>
        assertEquals("Carpentry should be Ultimate (7)", 7, pirate.getCarpentry());
        
        // From the HTML: <b>Paragon</b>/<b>Legendary</b>
        assertEquals("Bilging should be Legendary (6)", 6, pirate.getBilge());
        
        // From the HTML: <b>Expert</b>/<b>Renowned</b>
        assertEquals("Gunning should be Renowned (4)", 4, pirate.getGunning());
        
        // From the HTML: <b>Solid</b>/<b>Master</b>
        assertEquals("Treasure Haul should be Master (3)", 3, pirate.getTH());
        
        // From the HTML: <b>Expert</b>/<b>Respected</b>
        assertEquals("Navigating should be Respected (2)", 2, pirate.getDNav());
        
        // From the HTML: <b>Expert</b>/<b>Ultimate</b>
        assertEquals("Battle Navigation should be Ultimate (7)", 7, pirate.getBNav());
        
        // From the HTML: <b>Expert</b>/<b>Grand-Master</b>
        assertEquals("Swordfighting should be Grand-Master (5)", 5, pirate.getSF());
        
        // From the HTML: <b>Solid</b>/<b>Master</b>
        assertEquals("Rumble should be Master (3)", 3, pirate.getRumble());
        
        // From the HTML: <b>Solid</b>/<b>Legendary</b>
        assertEquals("Foraging should be Legendary (6)", 6, pirate.getForage());
    }
    
    private String loadTestHtmlFile() throws IOException {
        // Load from test resources directory
        File testFile = new File("test/resources/test_pirate_page.html");
        assertTrue("Test HTML file should exist", testFile.exists());
        
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(testFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        
        return content.toString();
    }
}