package ch.epfl.rechor.journey.mapped;

import ch.epfl.rechor.timetable.mapped.BufferedStations;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    void sizeTest2() {
        Random random = new Random(42);
        for (int i = 1; i <= 1000; i *= 10) {
            List<String> stringTable = new ArrayList<>();
            ByteBuffer buffer = ByteBuffer.allocate(10 * i);
            for (int j = 0; j < i; j++) {
                stringTable.add("Station" + j);
                buffer.putShort((short) j);
                buffer.putInt(random.nextInt());
                buffer.putInt(random.nextInt());
            }
            buffer.flip();
            BufferedStations stations = new BufferedStations(stringTable, buffer);
            assertEquals(i, stations.size());
        }
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

    @Test
    void testBufferedStationsBasic() {
        List<String> stringTable = List.of("Lausanne", "Palézieux");
        ByteBuffer buffer = ByteBuffer.wrap(new byte[]{
                0x00, 0x00, 0x04, (byte) 0xB6, (byte) 0xCA, 0x14, 0x21, 0x14, 0x1F, (byte) 0xA1,
                0x00, 0x01, 0x04, (byte) 0xDC, (byte) 0xCC, 0x12, 0x21, 0x18, (byte) 0xDA, 0x03
        });
        BufferedStations stations = new BufferedStations(stringTable, buffer);

        assertEquals(stringTable.size(), stations.size());
        assertEquals("Lausanne", stations.name(0));
        assertEquals("Palézieux", stations.name(1));
        assertEquals(6.629092, stations.longitude(0), 1e-6);
        assertEquals(46.516792, stations.latitude(0), 1e-6);
        assertEquals(6.837875, stations.longitude(1), 1e-6);
        assertEquals(46.542764, stations.latitude(1), 1e-6);
    }

    @Test
    void testBufferedStationsEdgeCases() {
        List<String> stringTable = List.of("Station");
        ByteBuffer buffer = ByteBuffer.wrap(new byte[]{
                0x00, 0x00,

                (byte) 0x80, 0x00, 0x00, 0x00,

                (byte) 0x80, 0x00, 0x00, 0x00
        });
        BufferedStations stations = new BufferedStations(stringTable, buffer);

        assertEquals(stringTable.size(), stations.size());
        assertEquals("Station", stations.name(0));
        assertEquals(-180.0, stations.longitude(0), 1e-6);
        assertEquals(-180.0, stations.latitude(0), 1e-6);
    }

    @Test
    void testBufferedStationsZeroCoordinates() {
        List<String> stringTable = List.of("StationZero");
        ByteBuffer buffer = ByteBuffer.wrap(new byte[]{
                // NAME_ID = 0 (U16)
                0x00, 0x00,
                // LON = 0x00 00 00 00 (S32, 0)
                0x00, 0x00, 0x00, 0x00,
                // LAT = 0x00 00 00 00 (S32, 0)
                0x00, 0x00, 0x00, 0x00
        });
        BufferedStations stations = new BufferedStations(stringTable, buffer);
        assertEquals(1, stations.size());
        assertEquals("StationZero", stations.name(0));
        assertEquals(0.0, stations.longitude(0), 1e-6);
        assertEquals(0.0, stations.latitude(0), 1e-6);
    }

    /**
     * Test with maximum positive S32 value.
     * The maximum S32 value is 0x7FFFFFFF (2147483647), which should convert to
     * 2147483647 * (360 / 2^32) ≈ 180.0 - (360 / 2^32).
     */
    @Test
    void testBufferedStationsMaxCoordinates() {
        List<String> stringTable = List.of("StationMax");
        ByteBuffer buffer = ByteBuffer.wrap(new byte[]{
                // NAME_ID = 0 (U16)
                0x00, 0x00,
                // LON = 0x7F FF FF FF (S32, 2147483647)
                0x7F, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
                // LAT = 0x7F FF FF FF (S32, 2147483647)
                0x7F, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF
        });
        BufferedStations stations = new BufferedStations(stringTable, buffer);
        double expected = 2147483647 * (360.0 / (1L << 32)); // ≈ 180 - (360/2^32)
        assertEquals(1, stations.size());
        assertEquals("StationMax", stations.name(0));
        assertEquals(expected, stations.longitude(0), 1e-6);
        assertEquals(expected, stations.latitude(0), 1e-6);
    }

    /**
     * Test with multiple stations.
     * Station 0 has zero coordinates; station 1 has raw value 0x40000000 (1073741824)
     * for both longitude and latitude, which converts to 90°.
     */
    @Test
    void testBufferedStationsMultipleStations() {
        List<String> stringTable = List.of("StationZero", "StationQuarter");
        ByteBuffer buffer = ByteBuffer.wrap(new byte[]{
                //station 0
                0x00, 0x00,

                0x00, 0x00, 0x00, 0x00,

                0x00, 0x00, 0x00, 0x00,

                //station 1
                0x00, 0x01,

                0x40, 0x00, 0x00, 0x00,

                0x40, 0x00, 0x00, 0x00
        });
        BufferedStations stations = new BufferedStations(stringTable, buffer);
        assertEquals(2, stations.size());

        assertEquals("StationZero", stations.name(0));
        assertEquals(0.0, stations.longitude(0), 1e-6);
        assertEquals(0.0, stations.latitude(0), 1e-6);

        assertEquals("StationQuarter", stations.name(1));
        assertEquals(90.0, stations.longitude(1), 1e-6);
        assertEquals(90.0, stations.latitude(1), 1e-6);
    }


    @Test
    void testBufferedStationsInvalidIndex() {
        List<String> stringTable = List.of("StationOne");
        ByteBuffer buffer = ByteBuffer.wrap(new byte[]{

                0x00, 0x00,

                0x00, 0x00, 0x00, 0x00,

                0x00, 0x00, 0x00, 0x00
        });
        BufferedStations stations = new BufferedStations(stringTable, buffer);
        assertEquals(1, stations.size());

        assertThrows(IndexOutOfBoundsException.class, () -> stations.name(1));
        assertThrows(IndexOutOfBoundsException.class, () -> stations.longitude(1));
        assertThrows(IndexOutOfBoundsException.class, () -> stations.latitude(1));
    }

    @Test
    void testInvalidByteBufferLength() {
        List<String> stringTable = List.of("StationInvalid");
        //only 9 bytes provided instead of the required 10
        ByteBuffer buffer = ByteBuffer.wrap(new byte[]{
                0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00
        });
        assertThrows(IllegalArgumentException.class, () -> new BufferedStations(stringTable, buffer));
    }

    @Test
    void testBufferedStationsRandom() {
        Random random = new Random(42);
        List<String> stringTable = new ArrayList<>();
        List<Double> expectedLons = new ArrayList<>();
        List<Double> expectedLats = new ArrayList<>();
        ByteBuffer buffer = ByteBuffer.allocate(10 * 1000);

        double SCALE = 360.0 / (1L << 32);

        for (int i = 0; i < 1000; i++) {
            stringTable.add("Station" + i);
            int lon = random.nextInt();
            int lat = random.nextInt();
            expectedLons.add(lon * SCALE);
            expectedLats.add(lat * SCALE);
            buffer.putShort((short) i);
            buffer.putInt(lon);
            buffer.putInt(lat);
        }
        buffer.flip();

        BufferedStations stations = new BufferedStations(stringTable, buffer);
        assertEquals(stringTable.size(), stations.size());

        for (int i = 0; i < 1000; i++) {
            assertEquals("Station" + i, stations.name(i));
            assertEquals(expectedLons.get(i), stations.longitude(i), 1e-6);
            assertEquals(expectedLats.get(i), stations.latitude(i), 1e-6);
        }
    }
    @Test
    void testBufferedStationsOutOfBounds() {
        List<String> stringTable = List.of("OnlyStation");
        ByteBuffer buffer = ByteBuffer.wrap(new byte[]{
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
        });
        BufferedStations stations = new BufferedStations(stringTable, buffer);

        assertThrows(IndexOutOfBoundsException.class, () -> stations.name(1));
        assertThrows(IndexOutOfBoundsException.class, () -> stations.longitude(1));
        assertThrows(IndexOutOfBoundsException.class, () -> stations.latitude(1));
    }
}
