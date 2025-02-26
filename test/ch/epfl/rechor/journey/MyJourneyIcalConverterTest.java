// File: JourneyIcalConverterTest.java
package ch.epfl.rechor.journey;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link JourneyIcalConverter} class.
 *
 * The converter should return an iCalendar event containing:
 *   - A VCALENDAR object (VERSION=2.0, PRODID=ReCHor),
 *   - A VEVENT component with UID, DTSTAMP, DTSTART, DTEND, SUMMARY, and DESCRIPTION.
 * The DESCRIPTION should be built using FormatterFr.formatTime followed by formatted legs.
 */
public class MyJourneyIcalConverterTest {

    // Dummy FormatterFr implementation used by JourneyIcalConverter.
    public static class FormatterFr {
        public static String formatLeg(Journey.Leg.Foot f) {
            return "FootLeg";
        }
        public static String formatLeg(Journey.Leg.Transport t) {
            return "TransportLeg";
        }
        public static String formatTime(LocalDateTime dt) {
            return "FormattedTime";
        }
    }

    /**
     * Helper method to assert that expected substring exists in actual string.
     * If not, fail with a detailed message.
     */
    private void assertContains(String expectedSubstring, String actualOutput) {
        assertTrue(actualOutput.contains(expectedSubstring),
                "Expected substring:\n" + expectedSubstring +
                        "\nwas not found in actual output:\n" + actualOutput);
    }

    /**
     * Retrieves the final iCalendar string built by the JourneyIcalConverter.
     */
    private String getIcalContent(Journey journey) throws Exception {
        return JourneyIcalConverter.toIcalendar(journey);
    }

    /**
     * Test conversion of a journey with two legs (Foot then Transport).
     */
    @Test
    public void testToIcalendarWithMultipleLegs() throws Exception {
        // Create stops using the real Stop record (name, platformName, longitude, latitude)
        Stop stopA = new Stop("A", "Platform1", 6.5, 46.5);
        Stop stopB = new Stop("B", "Platform2", 6.6, 46.6);
        Stop stopC = new Stop("C", "Platform3", 6.7, 46.7);

        // Define times
        LocalDateTime depTime = LocalDateTime.of(2025, Month.MARCH, 1, 10, 0, 0);
        LocalDateTime footArrTime = LocalDateTime.of(2025, Month.MARCH, 1, 10, 10, 0);
        LocalDateTime transArrTime = LocalDateTime.of(2025, Month.MARCH, 1, 10, 30, 0);

        // Create a Foot leg from A to B
        Journey.Leg.Foot footLeg = new Journey.Leg.Foot(stopA, depTime, stopB, footArrTime);
        // Create a Transport leg from B to C; no intermediate stops.
        Journey.Leg.Transport transportLeg = new Journey.Leg.Transport(
                stopB, footArrTime, stopC, transArrTime, List.of(), Vehicle.TRAIN, "Route1", "Destination");

        // Create journey: legs must alternate, and stops must connect.
        Journey journey = new Journey(List.of(footLeg, transportLeg));

        // Retrieve the iCalendar output.
        String icalContent = getIcalContent(journey);

        // VCALENDAR structure and attributes
        assertContains("BEGIN:VCALENDAR", icalContent);
        assertContains("END:VCALENDAR", icalContent);
        assertContains("VERSION:2.0", icalContent);
        assertContains("PRODID:ReCHor", icalContent);

        // VEVENT structure
        assertContains("BEGIN:VEVENT", icalContent);
        assertContains("END:VEVENT", icalContent);

        // UID and DTSTAMP properties should be present
        assertContains("UID:", icalContent);
        assertContains("DTSTAMP:", icalContent);

        // Date-time properties - note: exact formatting expected here.
        String expectedDtstart = "20250301T100000";
        String expectedDtend = "20250301T103000";
        assertContains("DTSTART:" + expectedDtstart, icalContent);
        assertContains("DTEND:" + expectedDtend, icalContent);

        // SUMMARY should be "A → C" (with a space before and after the arrow)
        assertContains("SUMMARY: A → C", icalContent);

        // DESCRIPTION should be built from FormatterFr.formatTime and the leg representations.
        // Expected to contain "FormattedTime", "FootLeg", and "TransportLeg".
        assertContains("DESCRIPTION: FormattedTime", icalContent);
        assertContains("FootLeg", icalContent);
        assertContains("TransportLeg", icalContent);
    }

    /**
     * Test conversion of a journey with a single leg.
     */
    @Test
    public void testToIcalendarWithSingleLeg() throws Exception {
        // Create stops using the real Stop record
        Stop stopX = new Stop("X", "Platform1", 6.8, 46.8);
        Stop stopY = new Stop("Y", "Platform2", 6.9, 46.9);

        // Define times
        LocalDateTime depTime = LocalDateTime.of(2025, Month.APRIL, 1, 9, 0, 0);
        LocalDateTime arrTime = LocalDateTime.of(2025, Month.APRIL, 1, 10, 0, 0);

        // Create a single Foot leg from X to Y.
        Journey.Leg.Foot footLeg = new Journey.Leg.Foot(stopX, depTime, stopY, arrTime);
        Journey journey = new Journey(List.of(footLeg));

        // Retrieve the iCalendar output.
        String icalContent = getIcalContent(journey);

        // VCALENDAR and VEVENT structure.
        assertContains("BEGIN:VCALENDAR", icalContent);
        assertContains("BEGIN:VEVENT", icalContent);

        // Date-time properties.
        String expectedDtstart = "20250401T090000";
        String expectedDtend = "20250401T100000";
        assertContains("DTSTART:" + expectedDtstart, icalContent);
        assertContains("DTEND:" + expectedDtend, icalContent);

        // SUMMARY should be "X → Y"
        assertContains("SUMMARY: X → Y", icalContent);

        // DESCRIPTION should contain the formatted time and the leg's representation.
        assertContains("DESCRIPTION: FormattedTime", icalContent);
        assertContains("FootLeg", icalContent);
    }
}
