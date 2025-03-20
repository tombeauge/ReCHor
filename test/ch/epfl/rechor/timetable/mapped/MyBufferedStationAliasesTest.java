package ch.epfl.rechor.timetable.mapped;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void testValidAliases() {
        ByteBuffer buffer = ByteBuffer.wrap(hexFormat.parseHex("00 05 00 04 00 02 00 03"));
        List<String> stringTable = List.of("1", "70", "Anet", "Ins", "Lausanne", "Losanna", "Palézieux");

        BufferedStationAliases aliases = new BufferedStationAliases(stringTable, buffer);

        assertEquals("Losanna", aliases.alias(0));
        assertEquals("Lausanne", aliases.stationName(0));

        assertEquals("Anet", aliases.alias(1));
        assertEquals("Ins", aliases.stationName(1));
    }

    @Test
    void testInvalidIndexThrowsException() {
        ByteBuffer buffer = ByteBuffer.wrap(hexFormat.parseHex("00 05 00 04 00 02 00 03"));
        List<String> stringTable = List.of("1", "70", "Anet", "Ins", "Lausanne", "Losanna", "Palézieux");

        BufferedStationAliases aliases = new BufferedStationAliases(stringTable, buffer);

        assertThrows(IndexOutOfBoundsException.class, () -> aliases.alias(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> aliases.stationName(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> aliases.alias(2));
        assertThrows(IndexOutOfBoundsException.class, () -> aliases.stationName(2));
    }

    @Test
    void testEmptyBuffer() {
        ByteBuffer emptyBuffer = ByteBuffer.wrap(new byte[0]);
        List<String> stringTable = List.of();

        BufferedStationAliases aliases = new BufferedStationAliases(stringTable, emptyBuffer);

        assertEquals(0, aliases.size());
        assertThrows(IndexOutOfBoundsException.class, () -> aliases.alias(0));
        assertThrows(IndexOutOfBoundsException.class, () -> aliases.stationName(0));
    }

    @Test
    void testCorruptBuffer() {
        ByteBuffer corruptBuffer = ByteBuffer.wrap(hexFormat.parseHex("FF FF FF FF"));
        List<String> stringTable = List.of("Losanna", "Lausanne");

        BufferedStationAliases aliases = new BufferedStationAliases(stringTable, corruptBuffer);

        assertThrows(IndexOutOfBoundsException.class, () -> aliases.alias(0));
        assertThrows(IndexOutOfBoundsException.class, () -> aliases.stationName(0));
    }

    @Test
    void testExtremeValues2() {
        ByteBuffer buffer = ByteBuffer.wrap(hexFormat.parseHex("FF FF FF FF"));

        List<String> stringTable = new ArrayList<>(Collections.nCopies(10, "Station"));

        BufferedStationAliases aliases = new BufferedStationAliases(stringTable, buffer);

        assertEquals(1, aliases.size());

        assertThrows(IndexOutOfBoundsException.class, () -> aliases.alias(0));
        assertThrows(IndexOutOfBoundsException.class, () -> aliases.stationName(0));
    }

    @Test
    void testOutOfBoundsHighIndices() {
        ByteBuffer buffer = ByteBuffer.wrap(hexFormat.parseHex("FF FF FF FF FF FF FF FF"));
        List<String> stringTable = new ArrayList<>(Collections.nCopies(500, "Station"));

        BufferedStationAliases aliases = new BufferedStationAliases(stringTable, buffer);

        assertEquals(2, aliases.size());

        assertThrows(IndexOutOfBoundsException.class, () -> aliases.alias(0));
        assertThrows(IndexOutOfBoundsException.class, () -> aliases.stationName(0));
        assertThrows(IndexOutOfBoundsException.class, () -> aliases.alias(1));
        assertThrows(IndexOutOfBoundsException.class, () -> aliases.stationName(1));
    }

    @Test
    void testZeroValues() {
        ByteBuffer buffer = ByteBuffer.wrap(hexFormat.parseHex("00 00 00 00 00 00 00 00"));
        List<String> stringTable = new ArrayList<>(Collections.nCopies(5, "ValidStation"));

        BufferedStationAliases aliases = new BufferedStationAliases(stringTable, buffer);

        assertEquals(2, aliases.size());

        assertEquals("ValidStation", aliases.alias(0));
        assertEquals("ValidStation", aliases.stationName(0));
        assertEquals("ValidStation", aliases.alias(1));
        assertEquals("ValidStation", aliases.stationName(1));
    }

    @Test
    void testAlternatingValidAndInvalidValues() {
        byte[] bufferData = new byte[20 * 4]; // 20 aliases, each with 4 bytes (U16 + U16)
        int outOfBoundsIndex = 25; // Just beyond the stringTable size (20)

        for (int i = 0; i < 20; i++) {
            int recordOffset = 4 * i;
            if (i % 2 == 0) {
                //even -> valid
                bufferData[recordOffset]     = (byte) ((i >> 8) & 0xFF);
                bufferData[recordOffset + 1] = (byte) (i & 0xFF);
                bufferData[recordOffset + 2] = (byte) ((i >> 8) & 0xFF);
                bufferData[recordOffset + 3] = (byte) (i & 0xFF);
            } else {
                //odd -> invalid
                bufferData[recordOffset]     = (byte) ((outOfBoundsIndex >> 8) & 0xFF);
                bufferData[recordOffset + 1] = (byte) (outOfBoundsIndex & 0xFF);
                bufferData[recordOffset + 2] = (byte) ((outOfBoundsIndex >> 8) & 0xFF);
                bufferData[recordOffset + 3] = (byte) (outOfBoundsIndex & 0xFF);
            }
        }

        ByteBuffer buffer = ByteBuffer.wrap(bufferData);
        List<String> stringTable = new ArrayList<>(Collections.nCopies(20, "Station"));

        BufferedStationAliases aliases = new BufferedStationAliases(stringTable, buffer);

        assertEquals(20, aliases.size());

        for (int i = 0; i < 20; i++) {
            final int index = i;
            if (index % 2 == 0) {
                //even -> valid
                assertEquals("Station", aliases.alias(index));
                assertEquals("Station", aliases.stationName(index));
            } else {
                //odd -> out-of-bounds
                assertThrows(IndexOutOfBoundsException.class, () -> aliases.alias(index));
                assertThrows(IndexOutOfBoundsException.class, () -> aliases.stationName(index));
            }
        }
    }

    @Test
    void testRandomStressTest() {
        int numAliases = 10000;
        byte[] bufferData = new byte[numAliases * 4];
        Random random = new Random();

        for (int i = 0; i < numAliases; i++) {
            int randomIndex = random.nextInt(5000); //random index within 5000
            bufferData[4 * i]     = (byte) ((randomIndex >> 8) & 0xFF);
            bufferData[4 * i + 1] = (byte) (randomIndex & 0xFF);
            bufferData[4 * i + 2] = (byte) ((randomIndex >> 8) & 0xFF);
            bufferData[4 * i + 3] = (byte) (randomIndex & 0xFF);
        }

        ByteBuffer buffer = ByteBuffer.wrap(bufferData);
        List<String> stringTable = new ArrayList<>(Collections.nCopies(5000, "Station"));

        BufferedStationAliases aliases = new BufferedStationAliases(stringTable, buffer);

        assertEquals(numAliases, aliases.size());

        for (int i = 0; i < 5000; i++) {
            assertEquals("Station", aliases.alias(i));
            assertEquals("Station", aliases.stationName(i));
        }
    }
}
