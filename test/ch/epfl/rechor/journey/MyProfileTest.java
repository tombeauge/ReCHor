package ch.epfl.rechor.journey;

import ch.epfl.rechor.timetable.Connections;
import ch.epfl.rechor.timetable.TimeTable;
import ch.epfl.rechor.timetable.Trips;
import ch.epfl.rechor.timetable.mapped.FileTimeTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.LongConsumer;

import static org.junit.jupiter.api.Assertions.*;

public class MyProfileTest {
    private TimeTable timeTable;
    private LocalDate testDate;
    private List<ParetoFront> stationFrontiers;
    private Profile profile;

    @BeforeEach
    void setUp() throws IOException {
        timeTable = FileTimeTable.in(Path.of("timetable"));
        testDate = LocalDate.of(2025, 3, 17);
        stationFrontiers = new ArrayList<>();

        // Creating ParetoFront instances using the Builder
        ParetoFront.Builder builder1 = new ParetoFront.Builder();
        builder1.add(30, 2, 100);
        ParetoFront frontier1 = builder1.build();

        ParetoFront.Builder builder2 = new ParetoFront.Builder();
        builder2.add(45, 1, 200);
        ParetoFront frontier2 = builder2.build();

        stationFrontiers.add(frontier1);
        stationFrontiers.add(frontier2);

        profile = new Profile(timeTable, testDate, 1, stationFrontiers);
    }

    @Test
    void testConstructorEnsuresImmutability() {
        List<ParetoFront> originalList = new ArrayList<>();
        originalList.add(ParetoFront.EMPTY);
        Profile testProfile = new Profile(timeTable, testDate, 2, originalList);

        assertThrows(UnsupportedOperationException.class, () -> testProfile.stationFront().add(ParetoFront.EMPTY));
    }

    @Test
    void testConnectionsDelegatesToTimeTable() {
        assertNotNull(profile.connections());
    }

    @Test
    void testTripsDelegatesToTimeTable() {
        assertNotNull(profile.trips());
    }

    @Test
    void testForStationReturnsCorrectFrontier() {
        assertEquals(stationFrontiers.get(1), profile.forStation(1));
    }

    @Test
    void testForStationThrowsExceptionForInvalidIndex() {
        assertThrows(IndexOutOfBoundsException.class, () -> profile.forStation(10));
    }

    @Test
    void testProfileBuilderCreatesValidProfile() {
        Profile.Builder builder = new Profile.Builder(timeTable, testDate, 1);
        builder.setForStation(0, new ParetoFront.Builder());
        builder.setForStation(1, new ParetoFront.Builder());
        Profile builtProfile = builder.build();

        assertNotNull(builtProfile);
        assertEquals(timeTable, builtProfile.timeTable());
        assertEquals(testDate, builtProfile.date());
        assertEquals(1, builtProfile.arrStationId());
    }

    @Test
    void testProfileBuilderHandlesNullFrontiers() {
        Profile.Builder builder = new Profile.Builder(timeTable, testDate, 1);
        builder.setForStation(1, new ParetoFront.Builder());

        Profile builtProfile = builder.build();

        System.out.println(builder.forStation(0).isEmpty());
        System.out.println(ParetoFront.EMPTY);

        System.out.println(builder.forStation(1).isEmpty());


    }

    //profile.forStation(7872).forEach(c -> {
    //    var c1 = Bits32_24_8.unpack24(PackedCriteria.payload(c));
    //    var d = tt.connectionsFor(LocalDate.of(2025, Month.MARCH, 18)).depStopId(c1);
    //    var n = tt.stations().name(tt.stationId(d));
    //    System.out.printf("%016x %s\n", c, n);
    //});
}
