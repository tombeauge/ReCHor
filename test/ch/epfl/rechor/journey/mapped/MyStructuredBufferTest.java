package ch.epfl.rechor.journey.mapped;

import ch.epfl.rechor.timetable.mapped.Structure;
import ch.epfl.rechor.timetable.mapped.StructuredBuffer;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static ch.epfl.rechor.journey.mapped.MyBufferedStationsTest.hexFormat;
import static ch.epfl.rechor.timetable.mapped.Structure.field;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MyStructuredBufferTest {


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

    //I didn't add any other tests for the get16s cus they get tested via the buffered stations and other tests already



}
