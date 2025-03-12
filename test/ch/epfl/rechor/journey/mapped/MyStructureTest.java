package ch.epfl.rechor.journey.mapped;

import ch.epfl.rechor.timetable.mapped.Structure;
import org.junit.jupiter.api.Test;

import static ch.epfl.rechor.timetable.mapped.Structure.FieldType.U16;
import static ch.epfl.rechor.timetable.mapped.Structure.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MyStructureTest {

    @Test
    void offsetTest() {
        int  ALIAS_ID  =  0 ;
        int  STATION_NAME_ID  =  1 ; Structure STRUCTURE = new Structure(
                field(ALIAS_ID, U16),
                field(STATION_NAME_ID, U16));
        assertEquals(6, STRUCTURE.offset(STATION_NAME_ID, 1 ));
    }

    @Test
    void structureOrderedTest() {
        //testing if all elements in structure have to be oredered
        assertThrows(IllegalArgumentException.class, () -> {
            Structure STRUCTURE = new Structure(
                    field(2, U16),
                    field(3, U16));
        });
        assertThrows(IllegalArgumentException.class, () -> {
            Structure STRUCTURE = new Structure(
                    field(0, U16),
                    field(-1, U16));
        });
        assertThrows(IllegalArgumentException.class, () -> {
            Structure STRUCTURE = new Structure(
                    field(0, U16),
                    field(0, U16));
        });
    }




}
