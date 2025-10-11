package nl.unreadable.YPPPP.parser;

import nl.unreadable.YPPPP.model.YPPPPPirate;
import java.io.BufferedReader;
import java.io.StringReader;
import java.io.IOException;
import java.util.Hashtable;

public class PiratePageParser {
    private static final Hashtable<String, Integer> SKILL_TO_INT = new Hashtable<>();

    static {
        SKILL_TO_INT.put("Able", 0);
        SKILL_TO_INT.put("Distinguished", 1);
        SKILL_TO_INT.put("Respected", 2);
        SKILL_TO_INT.put("Master", 3);
        SKILL_TO_INT.put("Renowned", 4);
        SKILL_TO_INT.put("Grand-Master", 5);
        SKILL_TO_INT.put("Legendary", 6);
        SKILL_TO_INT.put("Ultimate", 7);
    }

    public static YPPPPPirate parseFromHtml(String htmlContent) throws IOException {
        BufferedReader reader = new BufferedReader(new StringReader(htmlContent));

        YPPPPPirate pirate = new YPPPPPirate("Unknown");

        // Parse pirate name
        String name = extractPirateName(reader);
        pirate.setName(name);

        // Reset reader for skill parsing
        reader = new BufferedReader(new StringReader(htmlContent));

        // Parse skills in the order they appear on the page
        pirate.setSailing(extractSkillRating(reader, "Sailing"));

        reader = new BufferedReader(new StringReader(htmlContent));
        pirate.setRigging(extractSkillRating(reader, "Rigging"));

        reader = new BufferedReader(new StringReader(htmlContent));
        pirate.setCarpentry(extractSkillRating(reader, "Carpentry"));

        reader = new BufferedReader(new StringReader(htmlContent));
        pirate.setBilge(extractSkillRating(reader, "Bilging"));

        reader = new BufferedReader(new StringReader(htmlContent));
        pirate.setGunning(extractSkillRating(reader, "Gunning"));

        reader = new BufferedReader(new StringReader(htmlContent));
        pirate.setTH(extractSkillRating(reader, "Treasure Haul"));

        reader = new BufferedReader(new StringReader(htmlContent));
        pirate.setDNav(extractSkillRating(reader, "Navigating"));

        reader = new BufferedReader(new StringReader(htmlContent));
        pirate.setBNav(extractSkillRating(reader, "Battle Navigation"));

        reader = new BufferedReader(new StringReader(htmlContent));
        pirate.setSF(extractSkillRating(reader, "Swordfighting"));

        reader = new BufferedReader(new StringReader(htmlContent));
        pirate.setRumble(extractSkillRating(reader, "Rumble"));

        reader = new BufferedReader(new StringReader(htmlContent));
        pirate.setForage(extractSkillRating(reader, "Foraging"));

        reader.close();
        return pirate;
    }

    private static String extractPirateName(BufferedReader reader) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.contains("<td align=\"center\" height=\"32\"><font size=\"+1\"><b>")) {
                int startIndex = line.indexOf("<b>") + 3;
                int endIndex = line.indexOf("</b>");
                if (startIndex >= 3 && endIndex > startIndex) {
                    return line.substring(startIndex, endIndex);
                }
            }
        }
        return "Unknown";
    }

    private static int extractSkillRating(BufferedReader reader, String skillName) throws IOException {
        String line;

        // Find the skill line by alt attribute
        while ((line = reader.readLine()) != null) {
            if (line.contains("alt=\"" + skillName + "\"></a></td>")) {
                break;
            }
        }

        if (line == null)
            return 0;

        // Look for the rating line (contains the slash format)
        while ((line = reader.readLine()) != null) {
            if (line.contains("/") && line.contains("<b>")) {
                return parseOceanWideSkill(line);
            }
        }

        return 0;
    }

    private static int parseOceanWideSkill(String line) {
        // New format: <b>Experience</b>/<b>Ocean-wide</b>
        // We want the ocean-wide rating (after the slash)
        int slashIndex = line.indexOf(">/");
        if (slashIndex == -1)
            return 0;

        String afterSlash = line.substring(slashIndex + 2);
        int startIndex = afterSlash.indexOf("<b>") + 3;
        int endIndex = afterSlash.indexOf("</b>");

        if (startIndex < 3 || endIndex == -1)
            return 0;

        String skillLevel = afterSlash.substring(startIndex, endIndex);
        return SKILL_TO_INT.getOrDefault(skillLevel, 0);
    }
}