package ch.epfl.rechor.journey.mapped;

import ch.epfl.rechor.timetable.mapped.BufferedStationAliases;
import ch.epfl.rechor.timetable.mapped.BufferedStations;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MyBufferedStationAliasesTest {
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

    private static ByteBuffer getStationsAliasesByteBuffer() {
        String hexStations = "00 05 00 04 00 02 00 03";
        byte [] bytes = hexFormat.parseHex(hexStations);
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        return bb;
    }

    @Test
    void sizeTest() {
        BufferedStationAliases bufferedStationAlias = new BufferedStationAliases(getStringList(), getStationsAliasesByteBuffer());
        assertEquals(2, bufferedStationAlias.size());
        System.out.println(bufferedStationAlias.size());
    }

    @Test
    void aliasTest() {
        BufferedStationAliases bufferedStationAlias = new BufferedStationAliases(getStringList(), getStationsAliasesByteBuffer());
        assertEquals("Losanna", bufferedStationAlias.alias(0));
        assertEquals("Anet", bufferedStationAlias.alias(1));
    }

    @Test
    void stationNameTest() {
        BufferedStationAliases bufferedStationAlias = new BufferedStationAliases(getStringList(), getStationsAliasesByteBuffer());
        assertEquals("Lausanne", bufferedStationAlias.stationName(0));
        assertEquals("Ins", bufferedStationAlias.stationName(1));
    }

}
