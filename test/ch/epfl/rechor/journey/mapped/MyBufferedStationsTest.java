package ch.epfl.rechor.journey.mapped;

import ch.epfl.rechor.timetable.mapped.BufferedStations;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MyBufferedStationsTest {
    static HexFormat  hexFormat  = HexFormat.ofDelimiter( " " );


    private static List<String> getStringList() {
        List<String> stringTable = new ArrayList<String>();
        stringTable.add( "1" );
        stringTable.add( "70" );
        stringTable.add( "Anet" );
        stringTable.add( "Ins" );
        stringTable.add( "Lausanne" );
        stringTable.add( "Losanna" );
        stringTable.add( "Palézieux" );
        return stringTable;
    }


    private static ByteBuffer getStationsByteBuffer() {
        String hexStations = "00 04 04 b6 ca 14 21 14 1f a1 00 06 04 dc cc 12 21 18 da 03";
        byte [] bytes = hexFormat.parseHex(hexStations);
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        return bb;
    }

    @Test
    void sizeTest() {
        BufferedStations bufferedStation = new BufferedStations(getStringList(), getStationsByteBuffer());
        assertEquals(2, bufferedStation.size());
    }

    @Test
    void longTest() {
        BufferedStations bufferedStation = new BufferedStations(getStringList(), getStationsByteBuffer());
        assertEquals(true,(6.629092 - bufferedStation.longitude(0)) < 0.00001);
        assertEquals(true,(6.837875 - bufferedStation.longitude(1)) < 0.00001);

    }

    @Test
    void latTest() {
        BufferedStations bufferedStation = new BufferedStations(getStringList(), getStationsByteBuffer());
        assertEquals(true,(46.516792 - bufferedStation.latitude(0)) < 0.00001);
        assertEquals(true,(46.542764 - bufferedStation.latitude(1)) < 0.00001);
    }

    @Test
    void nameTest() {
        BufferedStations bufferedStation = new BufferedStations(getStringList(), getStationsByteBuffer());
        assertEquals(bufferedStation.name(0), "Lausanne");
        assertEquals(bufferedStation.name(1), "Palézieux");
    }

}
