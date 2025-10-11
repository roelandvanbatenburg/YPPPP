package nl.unreadable.YPPPP;

import org.junit.Test;
import static org.junit.Assert.*;

public class SimpleTest {
    
    @Test
    public void testBasicAssertion() {
        assertEquals("Simple test should pass", 2, 1 + 1);
        assertTrue("True should be true", true);
    }
    
    @Test
    public void testStringManipulation() {
        String testStr = "<b>Ultimate</b>";
        assertTrue("Should contain Ultimate", testStr.contains("Ultimate"));
        
        // Test the slash parsing logic we'll need
        String testRating = "<b>Expert</b>/<b>Ultimate</b>";
        int slashIndex = testRating.indexOf(">/");
        assertTrue("Should find slash after >", slashIndex > 0);
        
        String afterSlash = testRating.substring(slashIndex + 2);
        assertTrue("Should have content after slash", afterSlash.length() > 0);
        assertTrue("Should contain Ultimate after slash", afterSlash.contains("Ultimate"));
    }
}