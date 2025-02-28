// File: MyJourneyIcalConverterTest.java
package ch.epfl.rechor.journey;

import static ch.epfl.rechor.journey.JourneyIcalConverter.toIcalendar;
import static org.junit.jupiter.api.Assertions.*;

import ch.epfl.rechor.FormatterFr;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.epfl.rechor.IcalBuilder;
import org.junit.jupiter.api.Test;

/**
 * Extensive unit tests for the JourneyIcalConverter class.
 *
 * The converter should return an iCalendar event containing:
 *   - A VCALENDAR object (VERSION: 2.0, PRODID: ReCHor),
 *   - A VEVENT component with UID, DTSTAMP, DTSTART, DTEND, SUMMARY, and DESCRIPTION.
 *
 * DESCRIPTION is expected to be built using FormatterFr.formatTime and FormatterFr.formatLeg
 * (one line per leg, with lines folded if necessary).
 */
public class MyJourneyIcalConverterTest {

    /**
     * Helper method to assert that a given expected substring appears in the actual output.
     */
    private void assertContains(String expectedSubstring, String actualOutput) {
        assertTrue(actualOutput.contains(expectedSubstring),
                "Expected substring:\n" + expectedSubstring +
                        "\nwas not found in actual output:\n" + actualOutput);
    }

    /**
     * Retrieves the final iCalendar content from the converter.
     */
    private String getIcalContent(Journey journey) throws Exception {
        return JourneyIcalConverter.toIcalendar(journey);
    }

    /**
     * Helper method to extract the logical DESCRIPTION value from the iCalendar content.
     * This method unfolds folded lines (i.e. lines beginning with a space) to reconstruct
     * the original logical value.
     *
     * @param ical the complete iCalendar string
     * @return the unfolded DESCRIPTION value
     */
    private String extractDescription(String ical) {
        String[] lines = ical.split("\r\n");
        StringBuilder description = new StringBuilder();
        boolean inDescription = false;
        for (String line : lines) {
            if (line.startsWith("DESCRIPTION:")) {
                inDescription = true;
                description.append(line.substring("DESCRIPTION:".length()));
            } else if (inDescription && line.startsWith(" ")) {
                // Remove the folding space.
                description.append(line.substring(1));
            } else if (inDescription) {
                break;
            }
        }
        return description.toString();
    }

    // Formatter for DTSTAMP values (yyyyMMdd'T'HHmmss)
    private static final DateTimeFormatter ICAL_DT_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");

    /**
     * Test conversion of a journey with two legs (a Foot leg followed by a Transport leg).
     */
    @Test
    public void testToIcalendarWithMultipleLegs() throws Exception {
        // Create stops.
        Stop stopA = new Stop("A", "Platform1", 6.5, 46.5);
        Stop stopB = new Stop("B", "Platform2", 6.6, 46.6);
        Stop stopC = new Stop("C", "Platform3", 6.7, 46.7);

        // Define times.
        LocalDateTime depTime = LocalDateTime.of(2025, Month.MARCH, 1, 10, 0, 0);
        LocalDateTime footArrTime = LocalDateTime.of(2025, Month.MARCH, 1, 10, 10, 0);
        LocalDateTime transArrTime = LocalDateTime.of(2025, Month.MARCH, 1, 10, 30, 0);

        // Create legs.
        Journey.Leg.Foot footLeg = new Journey.Leg.Foot(stopA, depTime, stopB, footArrTime);
        Journey.Leg.Transport transportLeg = new Journey.Leg.Transport(
                stopB, footArrTime, stopC, transArrTime, List.of(), Vehicle.TRAIN, "Route1", "Destination");

        // Create journey.
        Journey journey = new Journey(List.of(footLeg, transportLeg));

        String icalContent = getIcalContent(journey);

        System.out.println("actual output: \n" + icalContent);

        // Check VCALENDAR structure.
        assertContains("BEGIN:VCALENDAR", icalContent);
        assertContains("END:VCALENDAR", icalContent);
        assertContains("VERSION:2.0", icalContent);
        assertContains("PRODID:ReCHor", icalContent);

        // Check VEVENT structure.
        assertContains("BEGIN:VEVENT", icalContent);
        assertContains("END:VEVENT", icalContent);

        // Check that UID and DTSTAMP properties are present.
        assertContains("UID:", icalContent);
        assertContains("DTSTAMP:", icalContent);

        // Check DTSTART and DTEND.
        String expectedDtstart = "20250301T100000";
        String expectedDtend = "20250301T103000";
        assertContains("DTSTART:" + expectedDtstart, icalContent);
        assertContains("DTEND:" + expectedDtend, icalContent);

        // SUMMARY should reflect the overall journey from stopA to stopC.
        // According to Journey.depStop() and Journey.arrStop(), SUMMARY should be "SUMMARY: A → C"
        assertContains("SUMMARY:A → C", icalContent);

        // Build the expected DESCRIPTION.
        // Assume that JourneyIcalConverter constructs DESCRIPTION as:
        // FormatterFr.formatTime(journey.depTime()) + "\n" +
        // FormatterFr.formatLeg(footLeg) + "\n" +
        // FormatterFr.formatLeg(transportLeg)
        String expectedHeader = FormatterFr.formatTime(depTime); // e.g., "10h00"
        String expectedFootLeg = FormatterFr.formatLeg(footLeg);   // e.g., "trajet à pied (10 min)"
        String expectedTransportLeg = FormatterFr.formatLeg(transportLeg); // e.g., "10h10 B (quai Platform2) → C (arr. 10h30 quai Platform3)"
        String logicalDescription = extractDescription(icalContent);
        assertTrue(logicalDescription.contains(expectedHeader),
                "DESCRIPTION should contain header: " + expectedHeader);
        assertTrue(logicalDescription.contains(expectedFootLeg),
                "DESCRIPTION should contain foot leg: " + expectedFootLeg);
        assertTrue(logicalDescription.contains(expectedTransportLeg),
                "DESCRIPTION should contain transport leg: " + expectedTransportLeg);


    }

    /**
     * Test conversion of a journey with a single leg.
     */
    @Test
    public void testToIcalendarWithSingleLeg() throws Exception {
        // Create stops.
        Stop stopX = new Stop("X", "Platform1", 6.8, 46.8);
        Stop stopY = new Stop("Y", "Platform2", 6.9, 46.9);

        // Define times.
        LocalDateTime depTime = LocalDateTime.of(2025, Month.APRIL, 1, 9, 0, 0);
        LocalDateTime arrTime = LocalDateTime.of(2025, Month.APRIL, 1, 10, 0, 0);

        Journey.Leg.Foot footLeg = new Journey.Leg.Foot(stopX, depTime, stopY, arrTime);
        Journey journey = new Journey(List.of(footLeg));

        String icalContent = getIcalContent(journey);

        // Check VCALENDAR and VEVENT structure.
        assertContains("BEGIN:VCALENDAR", icalContent);
        assertContains("BEGIN:VEVENT", icalContent);

        // Check DTSTART and DTEND.
        String expectedDtstart = "20250401T090000";
        String expectedDtend = "20250401T100000";
        assertContains("DTSTART:" + expectedDtstart, icalContent);
        assertContains("DTEND:" + expectedDtend, icalContent);

        // SUMMARY should be "SUMMARY: X → Y"
        assertContains("SUMMARY:X → Y", icalContent);

        // Expected DESCRIPTION is assumed to be:
        // FormatterFr.formatTime(journey.depTime()) + "\n" + FormatterFr.formatLeg(footLeg)
        String expectedHeader = FormatterFr.formatTime(depTime); // e.g., "9h00"
        String expectedFootLeg = FormatterFr.formatLeg(footLeg);   // e.g., "trajet à pied (60 min)"
        String logicalDescription = extractDescription(icalContent);
        assertTrue(logicalDescription.contains(expectedHeader),
                "DESCRIPTION should contain header: " + expectedHeader);
        assertTrue(logicalDescription.contains(expectedFootLeg),
                "DESCRIPTION should contain foot leg: " + expectedFootLeg);
    }

    /**
     * Test that long physical lines in the iCalendar output are folded properly.
     * (Each physical line must not exceed 75 characters, and continuation lines should start with a space.)
     */
    @Test
    public void testIcalLineLengthFolding() throws Exception {
        // Create a Transport leg with long route/destination text to force folding.
        String longText = "This is a very long description intended to force line folding because it exceeds seventy-five characters in length.";
        Journey.Leg.Transport transportLeg = new Journey.Leg.Transport(
                new Stop("S1", "1", 6.0, 46.0),
                LocalDateTime.of(2025, Month.MAY, 1, 8, 0, 0),
                new Stop("S2", "2", 6.1, 46.1),
                LocalDateTime.of(2025, Month.MAY, 1, 8, 30, 0),
                List.of(), Vehicle.BUS, "RouteLong", longText);

        LocalDateTime depTime = LocalDateTime.of(2025, Month.MAY, 1, 8, 0, 0);
        LocalDateTime arrTime = LocalDateTime.of(2025, Month.MAY, 1, 8, 30, 0);
        Journey journey = new Journey(List.of(transportLeg));

        //testing out print
        System.out.println(toIcalendar(journey));

        String icalContent = getIcalContent(journey);

        // Ensure that folded lines exist (i.e. a CRLF followed by a space).
        assertTrue(icalContent.contains("\r\n "),
                "Folded lines should begin with a space following CRLF");

        // Verify that each physical line (ignoring the folding space) is at most 75 characters.
        String[] lines = icalContent.split("\r\n");
        for (String line : lines) {
            String effectiveLine = line.startsWith(" ") ? line.substring(1) : line;
            assertTrue(effectiveLine.length() <= 75,
                    "Each physical line should be at most 75 characters: " + effectiveLine);
        }
    }

    /**
     * Test that DTSTAMP is set to a value close to the current time.
     */
    @Test
    public void testDtstampIsCurrent() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime depTime = now;
        LocalDateTime arrTime = now.plusHours(1);
        Stop stopA = new Stop("A", "Platform1", 6.5, 46.5);
        Stop stopB = new Stop("B", "Platform2", 6.6, 46.6);

        Journey.Leg.Foot footLeg = new Journey.Leg.Foot(stopA, depTime, stopB, arrTime);
        Journey journey = new Journey(List.of(footLeg));

        String icalContent = getIcalContent(journey);

        // Use regex to extract the DTSTAMP value.
        Pattern dtstampPattern = Pattern.compile("DTSTAMP:(\\d{8}T\\d{6})");
        var matcher = dtstampPattern.matcher(icalContent);
        assertTrue(matcher.find(), "DTSTAMP must be present");
        String dtstampStr = matcher.group(1);
        LocalDateTime dtstamp = LocalDateTime.parse(dtstampStr, ICAL_DT_FORMATTER);

        // Allow a margin of 2 minutes.
        LocalDateTime nowAfter = LocalDateTime.now();
        assertTrue(!dtstamp.isBefore(now.minusMinutes(2)) &&
                        !dtstamp.isAfter(nowAfter.plusMinutes(2)),
                "DTSTAMP must be close to the current time");
    }

}
