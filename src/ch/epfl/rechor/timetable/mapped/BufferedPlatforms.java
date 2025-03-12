package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.timetable.Platforms;

import java.nio.ByteBuffer;
import java.util.List;

import static ch.epfl.rechor.timetable.mapped.Structure.field;

/**
 * Final class implementing the interface Platforms to decipher elements in buffer.
 * @author Cem Celik
 */
public final class BufferedPlatforms implements Platforms {

    List<String> stringTable;
    ByteBuffer buffer;
    int  NAME_ID  =  0 ;
    int  STATION_ID  =  1;
    StructuredBuffer platformStructureBuffer;
    Structure platformStructure;

    /**
     * Constructs a BufferedPlatform with a bytebuffer and a string table allowing the user to
     * access elements and their meaning in the byte array.
     * The constructor also initiates the platform structure using specific fields and their lengths.
     *
     * @param stringTable with names referring to stations, platforms, etc
     * @param buffer is a byte array
     */
    public BufferedPlatforms(List<String> stringTable, ByteBuffer buffer) {
        this.stringTable = stringTable;
        this.buffer = buffer;
        platformStructure = new Structure(
                field(NAME_ID, Structure.FieldType.U16),
                field(STATION_ID, Structure.FieldType.U16));
        platformStructureBuffer = new StructuredBuffer(platformStructure, buffer);
    }


    @Override
    public String name(int id) {
        int platformNameID = platformStructureBuffer.getU16(NAME_ID,id);

        return stringTable.get(platformNameID);
    }

    @Override
    public int stationId(int id) {
        return platformStructureBuffer.getU16(STATION_ID,id);
    }

    @Override
    public int size() {
        return platformStructureBuffer.size();
    }
}
