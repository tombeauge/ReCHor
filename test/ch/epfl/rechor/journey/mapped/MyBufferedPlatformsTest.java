package ch.epfl.rechor.journey.mapped;

import ch.epfl.rechor.timetable.mapped.BufferedPlatforms;
import ch.epfl.rechor.timetable.mapped.BufferedStationAliases;
import ch.epfl.rechor.timetable.mapped.BufferedStations;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MyBufferedPlatformsTest {
    static HexFormat hexFormat  = HexFormat.ofDelimiter( " " );

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

    private static ByteBuffer getPlatformByteBuffer() {
        String hexStations = "00 00 00 00 00 01 00 00 00 00 00 01";
        byte [] bytes = hexFormat.parseHex(hexStations);
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        return bb;
    }

    @Test
    void sizeTest() {
        BufferedPlatforms bufferedPlatform = new BufferedPlatforms(getStringList(), getPlatformByteBuffer());
        assertEquals(3, bufferedPlatform.size());
        System.out.println(bufferedPlatform.size());
    }

    @Test
    void nameTest() {
        BufferedPlatforms bufferedPlatform = new BufferedPlatforms(getStringList(), getPlatformByteBuffer());
        assertEquals(bufferedPlatform.name(0), "1");
        assertEquals(bufferedPlatform.name(1), "70");
        assertEquals(bufferedPlatform.name(2), "1");
    }

    @Test
    void stationIDTest() {
        BufferedPlatforms bufferedPlatform = new BufferedPlatforms(getStringList(), getPlatformByteBuffer());
        assertEquals(bufferedPlatform.stationId(0), 0);
        assertEquals(bufferedPlatform.stationId(1), 0);
        assertEquals(bufferedPlatform.stationId(2), 1);
    }
}
