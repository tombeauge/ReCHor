package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.timetable.StationAliases;

import java.nio.ByteBuffer;
import java.util.List;

import static ch.epfl.rechor.timetable.mapped.Structure.field;

/**
 * Final class implementing the interface StationsAliases to decipher elements in buffer.
 *
 * @author Cem Celik
 */
public final class BufferedStationAliases implements StationAliases {

    List<String> stringTable;
    ByteBuffer buffer;
    int  ALIAS_ID  =  0 ;
    int  STATION_NAME_ID  =  1;
    StructuredBuffer stationAliasesStructureBuffer;
    Structure stationAliasesStructure;

    /**
     * Constructs a BufferedStationAlias with a bytebuffer and a string table allowing the user to
     * access elements and their meaning in the byte array.
     * The constructor also initiates the stationAlias structure using specific fields and their lengths.
     * @param stringTable with names referring to stations, platforms, etc
     * @param buffer is a byte array
     */
    public BufferedStationAliases(List<String> stringTable, ByteBuffer buffer) {
        this.stringTable = stringTable;
        this.buffer = buffer;
        stationAliasesStructure = new Structure(field(ALIAS_ID, Structure.FieldType.U16),
                field(STATION_NAME_ID, Structure.FieldType.U16));
        stationAliasesStructureBuffer = new StructuredBuffer(stationAliasesStructure, buffer);

    }


    @Override
    public String alias(int id) {
        int aliasID = stationAliasesStructureBuffer.getU16(ALIAS_ID,id);

        return stringTable.get(aliasID);
    }

    @Override
    public String stationName(int id) {
        int stationName = stationAliasesStructureBuffer.getU16(STATION_NAME_ID,id);

        return stringTable.get(stationName);
    }

    @Override
    public int size() {
        return stationAliasesStructureBuffer.size();
    }
}
