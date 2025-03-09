package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.timetable.StationAliases;

import java.nio.ByteBuffer;
import java.util.List;

import static ch.epfl.rechor.timetable.mapped.Structure.field;

public final class BufferedStationAliases implements StationAliases {

    List<String> stringTable;
    ByteBuffer buffer;
    int  ALIAS_ID  =  0 ;
    int  STATION_NAME_ID  =  1;
    StructuredBuffer stationAliasesStructureBuffer;
    Structure stationAliasesStructure;



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
