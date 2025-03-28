package ch.epfl.rechor.journey;

import ch.epfl.rechor.timetable.TimeTable;
import ch.epfl.rechor.timetable.mapped.FileTimeTable;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MyJourneyExtractorTest2 {



@Test
void journeysWorks1() throws IOException {
    List<Journey> js = JourneyExtractor.journeys(createProfile(), 7872);

    assertEquals("BEGIN:VCALENDAR\n" +
            "VERSION:2.0\n" +
            "PRODID:ReCHor\n" +
            "BEGIN:VEVENT\n" +
            "DTSTART:20250318T161300\n" +
            "DTEND:20250318T175700\n" +
            "SUMMARY:Ecublens VD, EPFL → Gruyères\n" +
            "DESCRIPTION:16h13 Ecublens VD, EPFL → Renens VD, gare (arr. 16h19)\\ntrajet \n" +
            " à pied (3 min)\\n16h26 Renens VD (voie 4) → Lausanne (arr. 16h33 voie 5)\\nc\n" +
            " hangement (5 min)\\n16h40 Lausanne (voie 1) → Romont FR (arr. 17h13 voie 2)\n" +
            " \\nchangement (3 min)\\n17h22 Romont FR (voie 1) → Bulle (arr. 17h41 voie 2)\n" +
            " \\nchangement (3 min)\\n17h50 Bulle (voie 4) → Gruyères (arr. 17h57 voie 2)\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR", splitString(js.get(32)));



}

    @Test
    void journeysWorks2() throws IOException {
        List<Journey> js = JourneyExtractor.journeys(createProfile(), 7872);

        assertEquals("BEGIN:VCALENDAR\n" +
                "VERSION:2.0\n" +
                "PRODID:ReCHor\n" +
                "BEGIN:VEVENT\n" +
                "DTSTART:20250318T053300\n" +
                "DTEND:20250318T072800\n" +
                "SUMMARY:Ecublens VD, EPFL → Gruyères\n" +
                "DESCRIPTION:5h33 Ecublens VD, EPFL → Renens VD, gare (arr. 5h39)\\ntrajet à \n" +
                " pied (3 min)\\n5h50 Renens VD (voie 5) → Palézieux (arr. 6h16 voie 3)\\nchan\n" +
                " gement (3 min)\\n6h19 Palézieux (voie 12) → Gruyères (arr. 7h28 voie 1)\n" +
                "END:VEVENT\n" +
                "END:VCALENDAR", splitString(js.get(0)));



    }

    @Test
    void journeysWorks3() throws IOException {
        List<Journey> js = JourneyExtractor.journeys(createProfile(), 78);

        assertEquals("BEGIN:VCALENDAR\n" +
                "VERSION:2.0\n" +
                "PRODID:ReCHor\n" +
                "BEGIN:VEVENT\n" +
                "DTSTART:20250318T211200\n" +
                "DTEND:20250318T232800\n" +
                "SUMMARY:Aarburg, Höhe → Gruyères\n" +
                "DESCRIPTION:21h12 Aarburg, Höhe → Olten, Bahnhof (arr. 21h20 quai B4)\\ntraj\n" +
                " et à pied (5 min)\\n21h29 Olten (voie 11) → Bern (arr. 21h56 voie 4)\\nchang\n" +
                " ement (6 min)\\n22h04 Bern (voie 3) → Romont FR (arr. 22h44 voie 1)\\nchange\n" +
                " ment (3 min)\\n22h52 Romont FR (voie 1) → Bulle (arr. 23h11 voie 2)\\nchange\n" +
                " ment (3 min)\\n23h20 Bulle (voie 4) → Gruyères (arr. 23h28 voie 1)\n" +
                "END:VEVENT\n" +
                "END:VCALENDAR", splitString(js.get(32)));





    }
    @Test
    void journeysWorks4() throws IOException {
        List<Journey> js = JourneyExtractor.journeys(createProfile(), 0);

        assertEquals("BEGIN:VCALENDAR\n" +
                "VERSION:2.0\n" +
                "PRODID:ReCHor\n" +
                "BEGIN:VEVENT\n" +
                "DTSTART:20250318T042000\n" +
                "DTEND:20250318T092800\n" +
                "SUMMARY:Aach (Hegau), Aachquelle → Gruyères\n" +
                "DESCRIPTION:4h20 Aach (Hegau), Aachquelle → Singen (Htw), Bahnhof/KARSTADT \n" +
                " (arr. 4h49)\\ntrajet à pied (5 min)\\n5h41 Singen (Hohentwiel) (voie 1) → Sc\n" +
                " haffhausen (arr. 5h54 voie 5)\\nchangement (3 min)\\n6h17 Schaffhausen (voie\n" +
                "  3) → Zürich HB (arr. 6h55 voie 5)\\nchangement (7 min)\\n7h02 Zürich HB (vo\n" +
                " ie 31) → Bern (arr. 7h58 voie 7)\\nchangement (6 min)\\n8h09 Bern (quai E-H)\n" +
                "  → Bulle (arr. 9h11 voie 2)\\nchangement (3 min)\\n9h20 Bulle (voie 4) → Gru\n" +
                " yères (arr. 9h28 voie 1)\n" +
                "END:VEVENT\n" +
                "END:VCALENDAR", splitString(js.get(0)));
    }

    @Test
    void journeyWorks5() throws IOException {
        List<Journey> js = JourneyExtractor.journeys(createProfile(), 130);

        assertEquals("BEGIN:VCALENDAR\n" +
                "VERSION:2.0\n" +
                "PRODID:ReCHor\n" +
                "BEGIN:VEVENT\n" +
                "DTSTART:20250318T161500\n" +
                "DTEND:20250318T192800\n" +
                "SUMMARY:Achseten, Höchst/Skilift → Gruyères\n" +
                "DESCRIPTION:16h15 Achseten, Höchst/Skilift → Frutigen, Bahnhof (arr. 16h55)\n" +
                " \\ntrajet à pied (3 min)\\n17h01 Frutigen (voie 1) → Spiez (arr. 17h14 voie \n" +
                " 4)\\nchangement (4 min)\\n17h23 Spiez (voie 2) → Bern (arr. 17h56 voie 4)\\nc\n" +
                " hangement (6 min)\\n18h09 Bern (quai E-H) → Bulle (arr. 19h11 voie 2)\\nchan\n" +
                " gement (3 min)\\n19h20 Bulle (voie 4) → Gruyères (arr. 19h28 voie 1)\n" +
                "END:VEVENT\n" +
                "END:VCALENDAR", splitString(js.get(4)));
    }

    @Test
    void journeyWorks6() throws IOException{
        List<Journey> js = JourneyExtractor.journeys(createProfile(), 18532);
        assertEquals("BEGIN:VCALENDAR\n" +
                "VERSION:2.0\n" +
                "PRODID:ReCHor\n" +
                "BEGIN:VEVENT\n" +
                "DTSTART:20250318T160700\n" +
                "DTEND:20250318T185100\n" +
                "SUMMARY:Mitholz, Balmhorn → Gruyères\n" +
                "DESCRIPTION:16h07 Mitholz, Balmhorn → Frutigen, Bahnhof (arr. 16h25 quai A)\n" +
                " \\ntrajet à pied (3 min)\\n16h30 Frutigen (voie 1) → Spiez (arr. 16h44 voie \n" +
                " 5)\\nchangement (4 min)\\n16h54 Spiez (voie 2) → Bern (arr. 17h25 voie 4)\\nc\n" +
                " hangement (6 min)\\n17h34 Bern (voie 3) → Fribourg/Freiburg (arr. 17h56 voi\n" +
                " e 2)\\nchangement (4 min)\\n18h04 Fribourg/Freiburg (voie 2) → Bulle (arr. 1\n" +
                " 8h41 voie 2)\\nchangement (3 min)\\n18h44 Bulle (voie 4) → Gruyères (arr. 18\n" +
                " h51 voie 2)\n" +
                "END:VEVENT\n" +
                "END:VCALENDAR", splitString(js.get(20)));


    }
    @Test
    void journeyWorks7() throws IOException{
        List<Journey> js = JourneyExtractor.journeys(createProfile(), 17300);

        assertEquals("BEGIN:VCALENDAR\n" +
                "VERSION:2.0\n" +
                "PRODID:ReCHor\n" +
                "BEGIN:VEVENT\n" +
                "DTSTART:20250318T175600\n" +
                "DTEND:20250318T205700\n" +
                "SUMMARY:Luzern Gütsch (Talstation) → Gruyères\n" +
                "DESCRIPTION:trajet à pied (1 min)\\n17h57 Luzern, Gütsch → Emmenbrücke, Bahn\n" +
                " hof (arr. 18h07)\\ntrajet à pied (2 min)\\n18h11 Emmenbrücke (voie 1) → Olte\n" +
                " n (arr. 18h52 voie 11)\\nchangement (5 min)\\n18h58 Olten (voie 12) → Bern (\n" +
                " arr. 19h26 voie 7)\\nchangement (6 min)\\n19h34 Bern (voie 3) → Fribourg/Fre\n" +
                " iburg (arr. 19h56 voie 2)\\nchangement (4 min)\\n20h04 Fribourg/Freiburg (vo\n" +
                " ie 2) → Bulle (arr. 20h41 voie 2)\\nchangement (3 min)\\n20h50 Bulle (voie 4\n" +
                " ) → Gruyères (arr. 20h57 voie 2)\n" +
                "END:VEVENT\n" +
                "END:VCALENDAR", splitString(js.get(39)));
    }

    @Test
    void journeyWorks8() throws IOException{
        List<Journey> js = JourneyExtractor.journeys(createProfile(), 12438);


        assertEquals("BEGIN:VCALENDAR\n" +
                "VERSION:2.0\n" +
                "PRODID:ReCHor\n" +
                "BEGIN:VEVENT\n" +
                "DTSTART:20250318T052700\n" +
                "DTEND:20250318T082800\n" +
                "SUMMARY:Hilfikon, Sandbüel → Gruyères\n" +
                "DESCRIPTION:5h27 Hilfikon, Sandbüel → Wohlen AG, Bahnhof (arr. 5h37)\\ntraje\n" +
                " t à pied (3 min)\\n5h47 Wohlen AG (voie 3) → Olten (arr. 6h21 quai CD)\\ncha\n" +
                " ngement (5 min)\\n6h29 Olten (voie 11) → Bern (arr. 6h56 voie 7)\\nchangemen\n" +
                " t (6 min)\\n7h09 Bern (quai E-H) → Bulle (arr. 8h11 voie 2)\\nchangement (3 \n" +
                " min)\\n8h20 Bulle (voie 4) → Gruyères (arr. 8h28 voie 1)\n" +
                "END:VEVENT\n" +
                "END:VCALENDAR", splitString(js.get(0)));

    }

    @Test
    void journeyWorks9() throws IOException{
        List<Journey> js = JourneyExtractor.journeys(createProfile(), 10000);

        assertEquals("BEGIN:VCALENDAR\n" +
                "VERSION:2.0\n" +
                "PRODID:ReCHor\n" +
                "BEGIN:VEVENT\n" +
                "DTSTART:20250318T074200\n" +
                "DTEND:20250318T112800\n" +
                "SUMMARY:Gaillard, Bossonnets → Gruyères\n" +
                "DESCRIPTION:7h42 Gaillard, Bossonnets → Ville-la-Grand, Eglise (arr. 8h00)\\\n" +
                " nchangement (2 min)\\n8h10 Ville-la-Grand, Eglise → Annemasse, gare (arr. 8\n" +
                " h15)\\ntrajet à pied (4 min)\\n8h20 Annemasse → Genève (arr. 8h43 voie 1)\\nc\n" +
                " hangement (4 min)\\n8h54 Genève (voie 4) → Palézieux (arr. 9h56 voie 2)\\nch\n" +
                " angement (3 min)\\n10h19 Palézieux (voie 12) → Gruyères (arr. 11h28 voie 1)\n" +
                "END:VEVENT\n" +
                "END:VCALENDAR", splitString(js.get(9)));
    }

    @Test
    void journeyWorks10() throws IOException{
        List<Journey> js = JourneyExtractor.journeys(createProfile(), 2347);





        assertEquals("BEGIN:VCALENDAR\n" +
                "VERSION:2.0\n" +
                "PRODID:ReCHor\n" +
                "BEGIN:VEVENT\n" +
                "DTSTART:20250318T170900\n" +
                "DTEND:20250318T232800\n" +
                "SUMMARY:Bartenheim, Église → Gruyères\n" +
                "DESCRIPTION:17h09 Bartenheim, Église → Helfrantzkirch, Mairie (arr. 17h17)\\\n" +
                " nchangement (5 min)\\n18h14 Helfrantzkirch, Mairie → Sierentz, Centre (arr.\n" +
                "  18h36)\\nchangement (2 min)\\n18h39 Sierentz, Centre → Basel, Bachgraben (a\n" +
                " rr. 19h13)\\nchangement (1 min)\\n19h24 Basel, Bachgraben (quai B) → Basel, \n" +
                " Bahnhof SBB (arr. 19h43 quai A)\\ntrajet à pied (6 min)\\n19h56 Basel SBB (v\n" +
                " oie 4) → Bern (arr. 20h56 voie 7)\\nchangement (6 min)\\n21h04 Bern (voie 3)\n" +
                "  → Palézieux (arr. 22h02 voie 1)\\nchangement (3 min)\\n22h19 Palézieux (voi\n" +
                " e 12) → Gruyères (arr. 23h28 voie 1)\n" +
                "END:VEVENT\n" +
                "END:VCALENDAR", splitString(js.get(3)));




    }

    @Test
    void journeyWorks11() throws IOException{
        List<Journey> js = JourneyExtractor.journeys(createProfile(),15285);


        assertEquals("BEGIN:VCALENDAR\n" +
                "VERSION:2.0\n" +
                "PRODID:ReCHor\n" +
                "BEGIN:VEVENT\n" +
                "DTSTART:20250318T053500\n" +
                "DTEND:20250318T065100\n" +
                "SUMMARY:Lausanne, gare → Gruyères\n" +
                "DESCRIPTION:trajet à pied (5 min)\\n5h40 Lausanne (voie 1) → Romont FR (arr.\n" +
                "  6h13 voie 2)\\nchangement (3 min)\\n6h22 Romont FR (voie 1) → Bulle (arr. 6\n" +
                " h41 voie 2)\\nchangement (3 min)\\n6h44 Bulle (voie 4) → Gruyères (arr. 6h51\n" +
                "  voie 2)\n" +
                "END:VEVENT\n" +
                "END:VCALENDAR", splitString(js.get(2)));

        }


    public static Profile readProfile(TimeTable timeTable,
                                      LocalDate date,
                                      int arrStationId) throws IOException {
        Path path = Path.of("test/resources/profiles/profile_2025-03-18_11486.txt");
        try (BufferedReader r = Files.newBufferedReader(path)) {
            Profile.Builder profileB = new Profile.Builder(timeTable, date, arrStationId);
            int stationId = -1;
            String line;
            while ((line = r.readLine()) != null) {
                stationId += 1;
                if (line.isEmpty()) continue;
                ParetoFront.Builder frontB = new ParetoFront.Builder();
                for (String t : line.split(","))
                    frontB.add(Long.parseLong(t, 16));
                profileB.setForStation(stationId, frontB);
            }
            return profileB.build();
        }
    }


    Profile createProfile() throws IOException{
        TimeTable t = FileTimeTable.in(Path.of("timetable"));
        LocalDate date = LocalDate.of(2025, Month.MARCH, 18);
        return readProfile(t, date, 11486);
    }

    String splitString(Journey journey){
        List<String> actual = new ArrayList<>(List.of(JourneyIcalConverter.toIcalendar(journey).split("\\r\\n")));
        //remove the DTSTAMP and UID
        actual.remove(5); actual.remove(4);
        return String.join("\n", actual);
    }






}