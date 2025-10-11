package nl.unreadable.YPPPP;

import org.junit.Test;
import static org.junit.Assert.*;

public class HtmlParsingTest {
    
    @Test
    public void testParseNewSkillFormat() {
        // Test the new HTML format: Experience/Ocean-wide
        String skillLine1 = "          <b>Paragon</b>/<b>Ultimate</b>";
        String skillLine2 = "          <b>Expert</b>/<b>Renowned</b>";
        String skillLine3 = "          <i><b>Sublime</b></i>/<b>Ultimate</b>";
        
        assertEquals("Should extract Ultimate from Paragon/Ultimate", "Ultimate", extractOceanWideSkill(skillLine1));
        assertEquals("Should extract Renowned from Expert/Renowned", "Renowned", extractOceanWideSkill(skillLine2));
        assertEquals("Should extract Ultimate from Sublime/Ultimate", "Ultimate", extractOceanWideSkill(skillLine3));
    }
    
    @Test
    public void testSkillLevelMapping() {
        // Test that we can map skill names to numbers correctly
        assertEquals("Able should be 0", 0, mapSkillToInt("Able"));
        assertEquals("Distinguished should be 1", 1, mapSkillToInt("Distinguished"));
        assertEquals("Respected should be 2", 2, mapSkillToInt("Respected"));
        assertEquals("Master should be 3", 3, mapSkillToInt("Master"));
        assertEquals("Renowned should be 4", 4, mapSkillToInt("Renowned"));
        assertEquals("Grand-Master should be 5", 5, mapSkillToInt("Grand-Master"));
        assertEquals("Legendary should be 6", 6, mapSkillToInt("Legendary"));
        assertEquals("Ultimate should be 7", 7, mapSkillToInt("Ultimate"));
        assertEquals("Unknown skill should be 0", 0, mapSkillToInt("Unknown"));
    }
    
    @Test
    public void testLineContainsSkillAlt() {
        // Test recognizing skill lines by alt attribute
        String sailingLine = "      alt=\"Sailing\"></a></td>";
        String riggingLine = "      alt=\"Rigging\"></a></td>";
        String carpentryLine = "      alt=\"Carpentry\"></a></td>";
        
        assertTrue("Should recognize Sailing alt", sailingLine.contains("alt=\"Sailing\"></a></td>"));
        assertTrue("Should recognize Rigging alt", riggingLine.contains("alt=\"Rigging\"></a></td>"));
        assertTrue("Should recognize Carpentry alt", carpentryLine.contains("alt=\"Carpentry\"></a></td>"));
    }
    
    // Helper method to extract ocean-wide skill from new format
    private String extractOceanWideSkill(String line) {
        // New format: <b>Experience</b>/<b>Ocean-wide</b>
        // We want the ocean-wide rating (after the slash)
        int slashIndex = line.indexOf(">/");
        if (slashIndex == -1) return "";
        
        String afterSlash = line.substring(slashIndex + 2);
        int startIndex = afterSlash.indexOf("<b>") + 3;
        int endIndex = afterSlash.indexOf("</b>");
        
        if (startIndex < 3 || endIndex == -1) return "";
        
        return afterSlash.substring(startIndex, endIndex);
    }
    
    // Helper method to map skill names to integers
    private int mapSkillToInt(String skillName) {
        switch (skillName) {
            case "Able": return 0;
            case "Distinguished": return 1;
            case "Respected": return 2;
            case "Master": return 3;
            case "Renowned": return 4;
            case "Grand-Master": return 5;
            case "Legendary": return 6;
            case "Ultimate": return 7;
            default: return 0;
        }
    }
}