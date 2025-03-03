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

    @Test
    public void testToIcalendarWithExpectedOutput() throws Exception {
        // Create stops.
        Stop stopA = new Stop("Ecublens VD, EPFL", "", 6.5, 46.5);
        Stop stopB = new Stop("Renens VD, gare", "", 6.6, 46.6);
        Stop stopB2 = new Stop("Renens VD", "", 6.6, 46.6);
        Stop stopC = new Stop("Lausanne", "voie 5", 6.7, 46.7);
        Stop stopC2 = new Stop("Lausanne Gare", "voie 5", 6.7, 46.7);
        Stop stopD = new Stop("Romont FR", "voie 2", 6.8, 46.8);
        Stop stopD2 = new Stop("Romont", "voie 2", 6.8, 46.8);
        Stop stopE = new Stop("Bulle", "voie 2", 6.9, 46.9);
        Stop stopE2 = new Stop("Bulle Centre", "voie 2", 6.9, 46.9);
        Stop stopF = new Stop("Gruyères", "voie 2", 7.0, 47.0);

        // Define times.
        LocalDateTime depTime = LocalDateTime.of(2025, Month.FEBRUARY, 18, 16, 13, 0);
        LocalDateTime arrTime = LocalDateTime.of(2025, Month.FEBRUARY, 18, 17, 57, 0);

        // Create legs ensuring alternation.
        Journey.Leg.Transport leg1 = new Journey.Leg.Transport(stopA, depTime, stopB, LocalDateTime.of(2025, Month.FEBRUARY, 18, 16, 19, 0), List.of(), Vehicle.BUS, "", "Renens VD, gare");
        Journey.Leg.Foot leg2 = new Journey.Leg.Foot(stopB, LocalDateTime.of(2025, Month.FEBRUARY, 18, 16, 19, 0), stopB2, LocalDateTime.of(2025, Month.FEBRUARY, 18, 16, 22, 0));
        Journey.Leg.Transport leg3 = new Journey.Leg.Transport(stopB2, LocalDateTime.of(2025, Month.FEBRUARY, 18, 16, 26, 0), stopC, LocalDateTime.of(2025, Month.FEBRUARY, 18, 16, 33, 0), List.of(), Vehicle.TRAIN, "voie 4", "Lausanne");
        Journey.Leg.Foot leg4 = new Journey.Leg.Foot(stopC, LocalDateTime.of(2025, Month.FEBRUARY, 18, 16, 33, 0), stopC2, LocalDateTime.of(2025, Month.FEBRUARY, 18, 16, 38, 0));
        Journey.Leg.Transport leg5 = new Journey.Leg.Transport(stopC2, LocalDateTime.of(2025, Month.FEBRUARY, 18, 16, 40, 0), stopD, LocalDateTime.of(2025, Month.FEBRUARY, 18, 17, 13, 0), List.of(), Vehicle.TRAIN, "voie 1", "Romont FR");
        Journey.Leg.Foot leg6 = new Journey.Leg.Foot(stopD, LocalDateTime.of(2025, Month.FEBRUARY, 18, 17, 13, 0), stopD2, LocalDateTime.of(2025, Month.FEBRUARY, 18, 17, 18, 0));
        Journey.Leg.Transport leg7 = new Journey.Leg.Transport(stopD2, LocalDateTime.of(2025, Month.FEBRUARY, 18, 17, 22, 0), stopE, LocalDateTime.of(2025, Month.FEBRUARY, 18, 17, 41, 0), List.of(), Vehicle.TRAIN, "voie 1", "Bulle");
        Journey.Leg.Foot leg8 = new Journey.Leg.Foot(stopE, LocalDateTime.of(2025, Month.FEBRUARY, 18, 17, 41, 0), stopE2, LocalDateTime.of(2025, Month.FEBRUARY, 18, 17, 46, 0));
        Journey.Leg.Transport leg9 = new Journey.Leg.Transport(stopE2, LocalDateTime.of(2025, Month.FEBRUARY, 18, 17, 50, 0), stopF, arrTime, List.of(), Vehicle.TRAIN, "voie 4", "Gruyères");

        // Create journey.
        Journey journey = new Journey(List.of(leg1, leg2, leg3, leg4, leg5, leg6, leg7, leg8, leg9));

        String icalContent = getIcalContent(journey);

        // Check VCALENDAR structure.
        assertContains("BEGIN:VCALENDAR", icalContent);
        assertContains("END:VCALENDAR", icalContent);
        assertContains("VERSION:2.0", icalContent);
        assertContains("PRODID:ReCHor", icalContent);

        // Check VEVENT structure.
        assertContains("BEGIN:VEVENT", icalContent);
        assertContains("END:VEVENT", icalContent);

        // Check UID and DTSTAMP properties are present.
        assertContains("UID:", icalContent);
        assertContains("DTSTAMP:", icalContent);

        // Check DTSTART and DTEND.
        String expectedDtstart = "20250218T161300";
        String expectedDtend = "20250218T175700";
        assertContains("DTSTART:" + expectedDtstart, icalContent);
        assertContains("DTEND:" + expectedDtend, icalContent);

        // SUMMARY should reflect the overall journey from stopA to stopF.
        assertContains("SUMMARY:Ecublens VD, EPFL → Gruyères", icalContent);

        // Validate DESCRIPTION includes foot transfers.
        String logicalDescription = extractDescription(icalContent);
        assertTrue(logicalDescription.contains("trajet à pied"), "DESCRIPTION should contain transfer foot legs.");
    }
}
