package ch.epfl.rechor.journey.mapped;

import ch.epfl.rechor.timetable.mapped.Structure;
import ch.epfl.rechor.timetable.mapped.StructuredBuffer;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.HexFormat;
import java.util.Random;

import static ch.epfl.rechor.journey.mapped.MyBufferedStationsTest.hexFormat;
import static ch.epfl.rechor.timetable.mapped.Structure.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MyStructuredBufferTest {

    private static final int ALIAS_ID = 0;
    private static final int STATION_NAME_ID = 1;

    private static final Structure STRUCTURE = new Structure(
            Structure.field(ALIAS_ID, Structure.FieldType.U16),
            Structure.field(STATION_NAME_ID, Structure.FieldType.U16)
    );

    private static ByteBuffer getStationsByteBuffer() {
        String hexStations = "00 04 04 b6 ca 14 21 14 1f a1 00 06 04 dc cc 12 21 18 da 03";
        byte [] bytes = hexFormat.parseHex(hexStations);
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        return bb;
    }

    @Test
    void StructuredBufferTest() {
        Structure stationStructure = new Structure(
                field(0, Structure.FieldType.U16),
                field(1, Structure.FieldType.S32),
                field(2, Structure.FieldType.S32));
        Structure errorStructure = new Structure(
                field(0, Structure.FieldType.U16),
                field(1, Structure.FieldType.S32),
                field(2, Structure.FieldType.S32),
                field(3, Structure.FieldType.S32));
        StructuredBuffer stationStructureBuffer = new StructuredBuffer(stationStructure, getStationsByteBuffer());

        //tests that if buffer is not divisible by structure it gives an error
        assertThrows(IllegalArgumentException.class, () -> {
            StructuredBuffer stationStructureBufferError = new StructuredBuffer(errorStructure, getStationsByteBuffer());
        });
    }

    @Test
    void getU8Test() {
        Structure stationStructure = new Structure(
                field(0, Structure.FieldType.U16),
                field(1, Structure.FieldType.S32),
                field(2, Structure.FieldType.S32));
        StructuredBuffer stationStructureBuffer = new StructuredBuffer(stationStructure, getStationsByteBuffer());
        //System.out.println(stationStructureBuffer.getU8(2,1));
        assertThrows(IndexOutOfBoundsException.class, () -> {
            stationStructureBuffer.getU8(-1, 0);
            stationStructureBuffer.getU8(5, 0);
            stationStructureBuffer.getU8(0, -1);
        });
    }

    //I didn't add any other tests for the get16s cus they get tested via the buffered stations and other tests already

    @Test
    void structuredBufferSizeIsCorrect() {
        byte[] bytes = HexFormat.ofDelimiter(" ").parseHex("00 05 00 04 00 02 00 03");
        assertEquals(8, bytes.length); // Ensure buffer is a multiple of structure size

        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        StructuredBuffer structuredBuffer = new StructuredBuffer(STRUCTURE, buffer);

        assertEquals(2, structuredBuffer.size());
    }

    @Test
    void getU16ReturnsCorrectValues() {
        byte[] bytes = HexFormat.ofDelimiter(" ").parseHex("00 05 00 04 00 02 00 03");
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        StructuredBuffer structuredBuffer = new StructuredBuffer(STRUCTURE, buffer);

        assertEquals(5, structuredBuffer.getU16(ALIAS_ID, 0));
        assertEquals(4, structuredBuffer.getU16(STATION_NAME_ID, 0));
        assertEquals(2, structuredBuffer.getU16(ALIAS_ID, 1));
        assertEquals(3, structuredBuffer.getU16(STATION_NAME_ID, 1));
    }

    @Test
    void structuredBufferConstructorThrowsExceptionOnInvalidSize() {
        ByteBuffer buffer = ByteBuffer.wrap(new byte[5]); //not a multiple of structure size (4)
        assertThrows(IllegalArgumentException.class, () -> new StructuredBuffer(STRUCTURE, buffer));
    }

    @Test
    void getU16ThrowsOnInvalidIndex() {
        byte[] bytes = HexFormat.ofDelimiter(" ").parseHex("00 05 00 04 00 02 00 03");
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        StructuredBuffer structuredBuffer = new StructuredBuffer(STRUCTURE, buffer);

        assertThrows(IndexOutOfBoundsException.class, () -> structuredBuffer.getU16(ALIAS_ID, 2));
        assertThrows(IndexOutOfBoundsException.class, () -> structuredBuffer.getU16(STATION_NAME_ID, 2));
    }

    @Test
    void getU8WorksCorrectly() {
        byte[] bytes = HexFormat.ofDelimiter(" ").parseHex("00 05 00 04 00 02 00 03");
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        StructuredBuffer structuredBuffer = new StructuredBuffer(STRUCTURE, buffer);

        assertEquals(0, structuredBuffer.getU8(ALIAS_ID, 0));
        assertEquals(5, structuredBuffer.getU8(ALIAS_ID, 0) + 5); // Indirect check
    }

    @Test
    void getS32WorksCorrectly() {
        Structure s32Structure = new Structure(
                Structure.field(0, Structure.FieldType.S32),
                Structure.field(1, Structure.FieldType.S32)
        );

        byte[] bytes = HexFormat.ofDelimiter(" ").parseHex("04 b6 ca 14 21 14 1f a1");
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        StructuredBuffer structuredBuffer = new StructuredBuffer(s32Structure, buffer);

        assertEquals(79088148, structuredBuffer.getS32(0, 0));
        assertEquals(554966945, structuredBuffer.getS32(1, 0));
    }

    @Test
    void getS32ThrowsOnInvalidIndex() {
        Structure s32Structure = new Structure(
                Structure.field(0, Structure.FieldType.S32)
        );

        byte[] bytes = HexFormat.ofDelimiter(" ").parseHex("04 b6 ca 14");
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        StructuredBuffer structuredBuffer = new StructuredBuffer(s32Structure, buffer);

        assertThrows(IndexOutOfBoundsException.class, () -> structuredBuffer.getS32(0, 1));
    }

    @Test
    void emptyBufferHasZeroSize() {
        ByteBuffer buffer = ByteBuffer.wrap(new byte[0]);
        StructuredBuffer structuredBuffer = new StructuredBuffer(STRUCTURE, buffer);
        assertEquals(0, structuredBuffer.size());
    }

    @Test
    void largeValuesWorkCorrectly() {
        Structure s32Structure = new Structure(
                Structure.field(0, Structure.FieldType.S32)
        );

        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putInt(Integer.MAX_VALUE);
        buffer.rewind();

        StructuredBuffer structuredBuffer = new StructuredBuffer(s32Structure, buffer);
        assertEquals(Integer.MAX_VALUE, structuredBuffer.getS32(0, 0));
    }

    @Test
    void negativeS32ValuesAreHandledCorrectly() {
        Structure s32Structure = new Structure(
                Structure.field(0, Structure.FieldType.S32)
        );

        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putInt(-123456789);
        buffer.rewind();

        StructuredBuffer structuredBuffer = new StructuredBuffer(s32Structure, buffer);
        assertEquals(-123456789, structuredBuffer.getS32(0, 0));
    }

    @Test
    void getU8ReturnsCorrectValues() {
        Structure u8Structure = new Structure(
                Structure.field(0, Structure.FieldType.U8)
        );

        ByteBuffer buffer = ByteBuffer.wrap(new byte[]{(byte) 255});
        StructuredBuffer structuredBuffer = new StructuredBuffer(u8Structure, buffer);

        assertEquals(255, structuredBuffer.getU8(0, 0));
    }

    @Test
    void getU8ThrowsOnInvalidIndex() {
        Structure u8Structure = new Structure(
                Structure.field(0, Structure.FieldType.U8)
        );

        ByteBuffer buffer = ByteBuffer.wrap(new byte[]{(byte) 255});
        StructuredBuffer structuredBuffer = new StructuredBuffer(u8Structure, buffer);

        assertThrows(IndexOutOfBoundsException.class, () -> structuredBuffer.getU8(0, 1));
    }

    @Test
    void randomizedS32ValuesAreCorrect() {
        Structure s32Structure = new Structure(
                Structure.field(0, Structure.FieldType.S32)
        );

        Random random = new Random();
        int testSize = 1000;
        ByteBuffer buffer = ByteBuffer.allocate(4 * testSize);
        int[] expectedValues = new int[testSize];

        for (int i = 0; i < testSize; i++) {
            expectedValues[i] = random.nextInt();
            buffer.putInt(expectedValues[i]);
        }
        buffer.rewind();

        StructuredBuffer structuredBuffer = new StructuredBuffer(s32Structure, buffer);
        for (int i = 0; i < testSize; i++) {
            assertEquals(expectedValues[i], structuredBuffer.getS32(0, i));
        }
    }

    @Test
    void randomizedU8ValuesAreCorrect() {
        Structure u8Structure = new Structure(
                Structure.field(0, Structure.FieldType.U8)
        );

        Random random = new Random();
        ByteBuffer buffer = ByteBuffer.allocate(10);
        int[] expectedValues = new int[10];

        for (int i = 0; i < 10; i++) {
            expectedValues[i] = random.nextInt(256);
            buffer.put((byte) expectedValues[i]);
        }
        buffer.rewind();

        StructuredBuffer structuredBuffer = new StructuredBuffer(u8Structure, buffer);
        for (int i = 0; i < 10; i++) {
            assertEquals(expectedValues[i], structuredBuffer.getU8(0, i));
        }
    }
}
