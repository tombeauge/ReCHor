package ch.epfl.rechor.journey.mapped;

import ch.epfl.rechor.timetable.mapped.Structure;
import org.junit.jupiter.api.Test;

import static ch.epfl.rechor.timetable.mapped.Structure.FieldType.*;
import static ch.epfl.rechor.timetable.mapped.Structure.field;
import static org.junit.jupiter.api.Assertions.*;

public class MyStructureTest {

    private static final int ALIAS_ID = 0;
    private static final int STATION_NAME_ID = 1;
    private static final int LONGITUDE = 2;
    private static final int LATITUDE = 3;

    private static final Structure TEST_STRUCTURE = new Structure(
            field(ALIAS_ID, U16),
            field(STATION_NAME_ID, U16),
            field(LONGITUDE, S32),
            field(LATITUDE, S32)
    );

    @Test
    void offsetTest() {
        Structure STRUCTURE = new Structure(
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

    @Test
    void testOffsetForFirstElement() {
        assertEquals(0, TEST_STRUCTURE.offset(ALIAS_ID, 0));
        assertEquals(2, TEST_STRUCTURE.offset(STATION_NAME_ID, 0));
        assertEquals(4, TEST_STRUCTURE.offset(LONGITUDE, 0));
        assertEquals(8, TEST_STRUCTURE.offset(LATITUDE, 0));
    }

    @Test
    void testOffsetForSecondElement() {
        int elementIndex = 1;
        int elementSize = TEST_STRUCTURE.totalSize();
        assertEquals(0 + elementSize, TEST_STRUCTURE.offset(ALIAS_ID, elementIndex));
        assertEquals(2 + elementSize, TEST_STRUCTURE.offset(STATION_NAME_ID, elementIndex));
        assertEquals(4 + elementSize, TEST_STRUCTURE.offset(LONGITUDE, elementIndex));
        assertEquals(8 + elementSize, TEST_STRUCTURE.offset(LATITUDE, elementIndex));
    }

    @Test
    void testOffsetForTenthElement() {
        int elementIndex = 10;
        int elementSize = TEST_STRUCTURE.totalSize();
        assertEquals(elementSize * 10, TEST_STRUCTURE.offset(ALIAS_ID, elementIndex));
        assertEquals(2 + elementSize * 10, TEST_STRUCTURE.offset(STATION_NAME_ID, elementIndex));
        assertEquals(4 + elementSize * 10, TEST_STRUCTURE.offset(LONGITUDE, elementIndex));
        assertEquals(8 + elementSize * 10, TEST_STRUCTURE.offset(LATITUDE, elementIndex));
    }

    @Test
    void testOffsetWithNegativeElementIndex() {
        assertThrows(IndexOutOfBoundsException.class, () -> TEST_STRUCTURE.offset(ALIAS_ID, -1));
        assertThrows(IndexOutOfBoundsException.class, () -> TEST_STRUCTURE.offset(STATION_NAME_ID, -5));
    }

    @Test
    void testOffsetWithInvalidFieldIndex() {
        assertThrows(IndexOutOfBoundsException.class, () -> TEST_STRUCTURE.offset(10, 0));
    }

    @Test
    void testOffsetForMaxIntegerIndex() {
        int maxIndex = Integer.MAX_VALUE / TEST_STRUCTURE.totalSize(); // Prevent overflow
        assertDoesNotThrow(() -> TEST_STRUCTURE.offset(ALIAS_ID, maxIndex));
    }

    @Test
    void testTotalSizeCalculation2() {
        assertEquals(12, TEST_STRUCTURE.totalSize());
    }

    @Test
    void testOffsetCalculationDoesNotOverflow2() {
        int largeIndex = Integer.MAX_VALUE / TEST_STRUCTURE.totalSize() - 1;
        assertDoesNotThrow(() -> TEST_STRUCTURE.offset(ALIAS_ID, largeIndex));
    }

    @Test
    void testStructureConstructorWithInvalidOrder() {
        assertThrows(IllegalArgumentException.class, () -> new Structure(
                field(STATION_NAME_ID, U16),
                field(ALIAS_ID, U16)
        ));
    }

    @Test
    void testStructureFieldCreation() {
        Structure.Field field = new Structure.Field(0, U16);
        assertEquals(0, field.index());
        assertEquals(U16, field.type());
    }

    @Test
    void testFieldCreationWithNullType() {
        assertThrows(NullPointerException.class, () -> new Structure.Field(0, null));
    }

    @Test
    void testTotalSizeForEmptyStructure() {
        Structure emptyStructure = new Structure();
        assertEquals(0, emptyStructure.totalSize());
    }

    @Test
    void testTotalSizeForSingleU8Field() {
        Structure structure = new Structure(
                field(0, U8)
        );
        assertEquals(1, structure.totalSize());
    }

    @Test
    void testTotalSizeForSingleU16Field() {
        Structure structure = new Structure(
                field(0, U16)
        );
        assertEquals(2, structure.totalSize());
    }

    @Test
    void testTotalSizeForSingleS32Field() {
        Structure structure = new Structure(
                field(0, S32)
        );
        assertEquals(4, structure.totalSize());
    }

    @Test
    void testTotalSizeForMultipleFields() {
        Structure structure = new Structure(
                field(0, U8),
                field(1, U16),
                field(2, S32)
        );
        assertEquals(1 + 2 + 4, structure.totalSize());
    }

    @Test
    void testTotalSizeForAllU8Fields() {
        Structure structure = new Structure(
                field(0, U8),
                field(1, U8),
                field(2, U8),
                field(3, U8)
        );
        assertEquals(4, structure.totalSize());
    }

    @Test
    void testTotalSizeForAllU16Fields() {
        Structure structure = new Structure(
                field(0, U16),
                field(1, U16),
                field(2, U16)
        );
        assertEquals(2 * 3, structure.totalSize());
    }

    @Test
    void testTotalSizeForAllS32Fields() {
        Structure structure = new Structure(
                field(0, S32),
                field(1, S32)
        );
        assertEquals(4 * 2, structure.totalSize());
    }

    @Test
    void testTotalSizeWithUnorderedIndexes() {
        assertThrows(IllegalArgumentException.class, () -> new Structure(
                field(1, U8),
                field(0, U16)
        ));
    }

    @Test
    void testTotalSizeWithGapsBetweenIndexes() {
        assertThrows(IllegalArgumentException.class, () -> new Structure(
                field(0, U8),
                field(2, U16)
        ));
    }

    @Test
    void testTotalSizeWithMaxFields() {
        Structure structure = new Structure(
                field(0, U8),
                field(1, U16),
                field(2, S32),
                field(3, U8),
                field(4, U16),
                field(5, S32)
        );
        assertEquals(1 + 2 + 4 + 1 + 2 + 4, structure.totalSize());
    }

    @Test
    void testTotalSizeWithLargeStructures() {
        int numFields = 1000;
        Structure.Field[] fields = new Structure.Field[numFields];
        for (int i = 0; i < numFields; i++) {
            fields[i] = field(i, U8);
        }
        Structure structure = new Structure(fields);
        assertEquals(numFields, structure.totalSize());
    }
}



