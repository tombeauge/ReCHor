// File: IcalBuilderTest.java
package ch.epfl.rechor;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.time.Month;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link IcalBuilder} class.
 */
public class MyIcalBuilderTest {

    /**
     * Helper method to assert that expected output matches actual output.
     */
    private void assertOutputMatches(String expected, String actual) {
        assertEquals(expected, actual, "\n--- EXPECTED OUTPUT ---\n" + expected +
                "\n--- ACTUAL OUTPUT ---\n" + actual);
    }

    @Test
    public void testAddStringPropertyNoFolding() {
        IcalBuilder builder = new IcalBuilder();
        builder.add(IcalBuilder.Name.PRODID, "TestProduct");
        String output = builder.build();

        String expectedOutput = "PRODID:TestProduct\r\n";
        assertOutputMatches(expectedOutput, output);
    }

    @Test
    public void testAddStringPropertyWithFolding() {
        IcalBuilder builder = new IcalBuilder();
        String longValue = "This is a very long value intended to test the folding mechanism of the iCalendar builder. It should be folded appropriately.";
        builder.add(IcalBuilder.Name.DESCRIPTION, longValue);
        String output = builder.build();

        // Manually folding the expected value at MAX_LINE_LENGTH (75 chars)
        String expectedOutput = "DESCRIPTION:This is a very long value intended to test the folding mechanis\r\n" +
                " m of the iCalendar builder. It should be folded appropriately.\r\n";
        assertOutputMatches(expectedOutput, output);
    }

    @Test
    public void testAddDateTimeProperty() {
        IcalBuilder builder = new IcalBuilder();
        LocalDateTime dateTime = LocalDateTime.of(2025, Month.MARCH, 15, 14, 30, 45);
        builder.add(IcalBuilder.Name.DTSTAMP, dateTime);
        String output = builder.build();

        String expectedOutput = "DTSTAMP:20250315T143045\r\n";
        assertOutputMatches(expectedOutput, output);
    }

    @Test
    public void testComponentBeginEndAndBuild() {
        IcalBuilder builder = new IcalBuilder();
        builder.begin(IcalBuilder.Component.VCALENDAR);
        builder.add(IcalBuilder.Name.VERSION, "2.0");
        builder.begin(IcalBuilder.Component.VEVENT);
        builder.add(IcalBuilder.Name.UID, "test-uid");
        builder.end(); // Ends VEVENT
        builder.end(); // Ends VCALENDAR
        String output = builder.build();

        String expectedOutput =
                "BEGIN:VCALENDAR\r\n" +
                        "VERSION:2.0\r\n" +
                        "BEGIN:VEVENT\r\n" +
                        "UID:test-uid\r\n" +
                        "END:VEVENT\r\n" +
                        "END:VCALENDAR\r\n";

        assertOutputMatches(expectedOutput, output);
    }

    @Test
    public void testEndWithoutBeginThrows() {
        IcalBuilder builder = new IcalBuilder();
        Exception exception = assertThrows(IllegalStateException.class, builder::end);
        assertEquals("There are no components to build", exception.getMessage(),
                "\n--- EXPECTED EXCEPTION ---\nThere are no components to build" +
                        "\n--- ACTUAL EXCEPTION ---\n" + exception.getMessage());
    }

    @Test
    public void testBuildWithUnclosedComponentsThrows() {
        IcalBuilder builder = new IcalBuilder();
        builder.begin(IcalBuilder.Component.VCALENDAR);
        Exception exception = assertThrows(IllegalStateException.class, builder::build);
        assertEquals("Some components have not been closed", exception.getMessage(),
                "\n--- EXPECTED EXCEPTION ---\nSome components have not been closed" +
                        "\n--- ACTUAL EXCEPTION ---\n" + exception.getMessage());
    }
}
