package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.timetable.Stations;
import java.nio.ByteBuffer;
import java.util.List;

import static ch.epfl.rechor.timetable.mapped.Structure.field;
import static java.lang.Math.scalb;

public final class BufferedStations implements Stations {

    List<String> stringTable;
    ByteBuffer buffer;
    int  NAME_ID  =  0 ;
    int  LON  =  1;
    int  LAT  =  2;
    StructuredBuffer stationStructureBuffer;
    Structure stationStructure;
    static final double LONG_LAT_CONSTANT = scalb(360, -32);

    public BufferedStations(List<String> stringTable, ByteBuffer buffer) {
        this.stringTable = stringTable;
        this.buffer = buffer;
        stationStructure = new Structure(field(NAME_ID, Structure.FieldType.U16),
                                         field(LON, Structure.FieldType.S32),
                                         field(LAT, Structure.FieldType.S32));
        stationStructureBuffer = new StructuredBuffer(stationStructure, buffer);

    }

    @Override
    public String name(int id) {
        int nameID = stationStructureBuffer.getU16(NAME_ID,id);

        return stringTable.get(nameID);
    }

    @Override
    public double longitude(int id) {
        int longitudeS32 = stationStructureBuffer.getS32(LON,id);
        return LONG_LAT_CONSTANT * longitudeS32;
    }

    @Override
    public double latitude(int id) {
        int latitudeS32 = stationStructureBuffer.getS32(LAT,id);
        return LONG_LAT_CONSTANT * latitudeS32;
    }

    @Override
    public int size() {
        return stationStructureBuffer.size();
    }


}
