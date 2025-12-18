package com.bugsnag;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

/**
 * Test for Configuration.shouldIgnoreClass with regex pattern matching
 */
public class ConfigurationDiscardClassesTest {

    private Configuration config;

    @Before
    public void setUp() {
        config = new Configuration("test-api-key");
    }

    @Test
    public void testExactMatch() {
        config.setDiscardClasses(new String[] {"com.example.CustomException"});

        assertTrue(config.shouldIgnoreClass("com.example.CustomException"));
        assertFalse(config.shouldIgnoreClass("com.example.OtherException"));
    }

    @Test
    public void testWildcardMatch() {
        config.setDiscardClasses(new String[] {"com\\.example\\..*"});

        assertTrue(config.shouldIgnoreClass("com.example.CustomException"));
        assertTrue(config.shouldIgnoreClass("com.example.OtherException"));
        assertTrue(config.shouldIgnoreClass("com.example."));
        assertFalse(config.shouldIgnoreClass("com.other.Exception"));
    }

    @Test
    public void testMultipleWildcards() {
        config.setDiscardClasses(new String[] {"com\\..*\\.Exception"});

        assertTrue(config.shouldIgnoreClass("com.example.Exception"));
        assertTrue(config.shouldIgnoreClass("com.other.Exception"));
        assertFalse(config.shouldIgnoreClass("com.example.CustomException"));
    }

    @Test
    public void testQuestionMarkWildcard() {
        config.setDiscardClasses(new String[] {"com\\.example\\.Exception."});

        assertTrue(config.shouldIgnoreClass("com.example.Exception1"));
        assertTrue(config.shouldIgnoreClass("com.example.ExceptionX"));
        assertFalse(config.shouldIgnoreClass("com.example.Exception"));
        assertFalse(config.shouldIgnoreClass("com.example.Exception12"));
    }

    @Test
    public void testMultiplePatterns() {
        config.setDiscardClasses(new String[] {
            "java\\.io\\..*",
            "com\\.example\\.CustomException",
            "org\\..*\\.SpecialException"
        });

        assertTrue(config.shouldIgnoreClass("java.io.IOException"));
        assertTrue(config.shouldIgnoreClass("java.io.FileNotFoundException"));
        assertTrue(config.shouldIgnoreClass("com.example.CustomException"));
        assertTrue(config.shouldIgnoreClass("org.apache.SpecialException"));
        assertTrue(config.shouldIgnoreClass("org.springframework.SpecialException"));
        assertFalse(config.shouldIgnoreClass("com.example.OtherException"));
    }

    @Test
    public void testGetDiscardClassesReturnsOriginalPatterns() {
        String[] patterns = new String[] {"com\\.example\\..*", "java\\.io\\.IOException"};
        config.setDiscardClasses(patterns);

        String[] retrieved = config.getDiscardClasses();
        assertEquals(2, retrieved.length);

        // Check that patterns are returned (not regex)
        boolean hasWildcard = false;
        boolean hasExact = false;
        for (String pattern : retrieved) {
            if (pattern.equals("com\\.example\\..*")) {
                hasWildcard = true;
            }
            if (pattern.equals("java\\.io\\.IOException")) {
                hasExact = true;
            }
        }
        assertTrue(hasWildcard);
        assertTrue(hasExact);
    }

    @Test
    public void testEmptyAndNullPatterns() {
        config.setDiscardClasses(new String[] {});
        assertFalse(config.shouldIgnoreClass("com.example.Exception"));

        config.setDiscardClasses(null);
        assertFalse(config.shouldIgnoreClass("com.example.Exception"));
    }

    @Test
    public void testSpecialCharactersAreEscaped() {
        // In regex, $ is a special character (end of line), so it needs to be escaped
        config.setDiscardClasses(new String[] {"com\\.example\\.Exception\\$Inner"});

        assertTrue(config.shouldIgnoreClass("com.example.Exception$Inner"));
        assertFalse(config.shouldIgnoreClass("com.example.ExceptionXInner"));
    }
}
