package ch.epfl.rechor.timetable.mapped;



public class Structure {

    private int totalsize = 0;
    private int[] fieldBytePos;


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

    public int totalSize() {
        return totalsize;
    }

    public int offset(int fieldIndex, int elementIndex) {
        return (elementIndex * this.totalSize()) + fieldBytePos[fieldIndex];
    }

    public enum FieldType {
        U8,
        U16,
        S32
    }

    public record Field(int index, FieldType type) {
        public Field {
            if (type == null) {
                throw new NullPointerException("FieldType cannot be null");
            }
        }
    }

    public static Field field(int index, FieldType type) {
        return new Field(index, type);
    }


}
