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
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    @Test
    void emptyBufferTest() {
        ByteBuffer emptyBuffer = ByteBuffer.wrap(new byte[0]);
        BufferedPlatforms bufferedPlatform = new BufferedPlatforms(getStringList(), emptyBuffer);
        assertEquals(0, bufferedPlatform.size());
    }

    @Test
    void singleEntryTest() {
        String hexStations = "00 01 00 02"; // One entry with name index 1 (70) and station ID 2
        ByteBuffer buffer = ByteBuffer.wrap(hexFormat.parseHex(hexStations));
        BufferedPlatforms bufferedPlatform = new BufferedPlatforms(getStringList(), buffer);

        assertEquals(1, bufferedPlatform.size());
        assertEquals("70", bufferedPlatform.name(0));
        assertEquals(2, bufferedPlatform.stationId(0));
    }

    @Test
    void outOfBoundsAccessTest() {
        BufferedPlatforms bufferedPlatform = new BufferedPlatforms(getStringList(), getPlatformByteBuffer());

        assertThrows(IndexOutOfBoundsException.class, () -> bufferedPlatform.name(3));
        assertThrows(IndexOutOfBoundsException.class, () -> bufferedPlatform.stationId(3));
        assertThrows(IndexOutOfBoundsException.class, () -> bufferedPlatform.name(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> bufferedPlatform.stationId(-1));
    }

    @Test
    void randomAccessTest() {
        BufferedPlatforms bufferedPlatform = new BufferedPlatforms(getStringList(), getPlatformByteBuffer());

        assertEquals("1", bufferedPlatform.name(0));
        assertEquals("70", bufferedPlatform.name(1));
        assertEquals("1", bufferedPlatform.name(2));
        assertEquals(0, bufferedPlatform.stationId(0));
        assertEquals(0, bufferedPlatform.stationId(1));
        assertEquals(1, bufferedPlatform.stationId(2));
    }
}
