package ch.epfl.rechor.journey;

import ch.epfl.rechor.timetable.TimeTable;
import ch.epfl.rechor.timetable.mapped.FileTimeTable;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;

public class MyJourneyExtractorTest {

    public static void main(String[] args) throws IOException {
        Path timetablePath = Path.of("timetable");
        Path profilePath = Path.of("test/resources/profiles/profile_2025-03-18_11486.txt");

        TimeTable tt = FileTimeTable.in(timetablePath);
        LocalDate date = LocalDate.of(2025, Month.MARCH, 18);
        int depStationId = 7872;   // Ecublens VD, EPFL
        int arrStationId = 11486;  // Gruyères

        Profile profile = readProfile(tt, date, arrStationId);

        System.out.println("Timetable loaded.");
        System.out.println("Extracting journeys from station ID " + depStationId + " to station ID " + arrStationId + "...");

        List<Journey> journeys = JourneyExtractor.journeys(profile, depStationId);
        System.out.println("✅ Found " + journeys.size() + " journeys.");

        if (journeys.isEmpty()) {
            System.out.println("❌ No journeys found for this day and station.");
            return;
        }

        int index = journeys.size() > 32 ? 32 : 0;
        Journey j = journeys.get(index);
        System.out.println("🧭 Journey #" + index);

        System.out.println("• Departure: " + j.depStop().name() + " @ " + j.depTime());
        System.out.println("• Arrival:   " + j.arrStop().name() + " @ " + j.arrTime());
        System.out.println("• Legs:");

        for (Journey.Leg leg : j.legs()) {
            if (leg instanceof Journey.Leg.Transport t) {
                System.out.println("  🚆 Transport: " + t.depStop().name() + " → " + t.arrStop().name());
                System.out.println("     Vehicle: " + t.vehicle() + ", Route: " + t.route() + ", Destination: " + t.destination());
                System.out.println("     Departure: " + t.depTime() + ", Arrival: " + t.arrTime());
                if (!t.intermediateStops().isEmpty()) {
                    System.out.println("     🛑 Intermediate stops:");
                    for (Journey.Leg.IntermediateStop s : t.intermediateStops()) {
                        System.out.println("       - " + s.stop().name() + " at " + s.depTime());
                    }
                }
            } else if (leg instanceof Journey.Leg.Foot f) {
                System.out.println("  🚶 Foot: " + f.depStop().name() + " → " + f.arrStop().name());
                System.out.println("     From " + f.depTime() + " to " + f.arrTime());
            }
        }

        System.out.println("\n📅 iCalendar export:");
        System.out.println(JourneyIcalConverter.toIcalendar(j));
    }

    /**
     * Reads a packed profile text file for a given timetable, date, and arrival station.
     */
    public static Profile readProfile(TimeTable timeTable, LocalDate date, int arrStationId) throws IOException {
        Path path = Path.of("test/resources/profiles/profile_2025-03-18_11486.txt");

        try (BufferedReader r = Files.newBufferedReader(path)) {
            Profile.Builder profileB = new Profile.Builder(timeTable, date, arrStationId);
            int stationId = -1;
            String line;

            while ((line = r.readLine()) != null) {
                stationId++;
                if (line.isEmpty()) continue;

                ParetoFront.Builder frontB = new ParetoFront.Builder();
                for (String t : line.split(","))
                    frontB.add(Long.parseLong(t, 16));

                profileB.setForStation(stationId, frontB);
            }
            return profileB.build();
        }
    }
}
