package ch.epfl.rechor.timetable.mapped;

/**
 * Class setting the structure for the byte arrays to store Buffered values. This is mostly a utility class to allow to package
 * values inside byte arrays.
 *
 * @author Cem Celik
 *
 */

public class Structure {

    private int totalsize = 0;
    private int[] fieldBytePos;

    /**
     * A structure is created from any number of fields. This number of fields can change depending on this classes use case.
     *
     * @param fields
     * @throws IllegalArgumentException if field indices are not labeled properly starting from 0 and increasing by an increment of 1.
     */
    public Structure(Field... fields) {
        fieldBytePos = new int[fields.length];
        for (int i = 0; i < fields.length; i++) {
            if (fields[i].index != i) {
                throw new IllegalArgumentException("Indices in field not ordered properly:" + fields[i].index);
            }
        }
        for (int i = 0; i < fields.length; i++) {
            fieldBytePos[i] = totalsize;
            if (fields[i].type == FieldType.U8) {
                totalsize = totalsize + 1;
            }
            else if (fields[i].type == FieldType.U16) {
                totalsize = totalsize + 2;
            }
            else if (fields[i].type == FieldType.S32) {
                totalsize = totalsize + 4;
            }
        }

    }

    /**
     * @return totalsize of the structure in terms of byte numbers
     */
    public int totalSize() {
        return totalsize;
    }

    /**
     *Computes the byte offset
     * @param fieldIndex
     * @param elementIndex
     * @return the offset required to reach the value in a given fieldindex and element index
     */
    public int offset(int fieldIndex, int elementIndex) {
        if (elementIndex < 0) {
            throw new IndexOutOfBoundsException("Element index cannot be negative");
        }
        return (elementIndex * this.totalSize()) + fieldBytePos[fieldIndex];
    }

    /**
     * Enumeration representing the possible field types in a structured data table.
     * - U8: Unsigned 8-bit integer (1 byte)
     * - U16: Unsigned 16-bit integer (2 bytes)
     * - S32: Signed 32-bit integer (4 bytes)
     */
    public enum FieldType {
        U8,
        U16,
        S32
    }

    /**
     * Construction of a field
     * @param index of the field in the structure
     * @param type the fieldtype of the field
     * @throws NullPointerException if type is null
     */
    public record Field(int index, FieldType type) {
        public Field {
            if (type == null) {
                throw new NullPointerException("FieldType cannot be null");
            }
        }
    }

    /**
     * Creates a new Field instance without requiring the new keyword.
     * Therefore, simplifying the creation of fields when defining a Structure.
     *
     * @param index The index of the field
     * @param type  The type of the field
     * @return A new Field instance
     */
    public static Field field(int index, FieldType type) {
        return new Field(index, type);
    }


}
