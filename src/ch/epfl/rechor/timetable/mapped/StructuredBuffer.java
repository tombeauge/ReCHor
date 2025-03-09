package ch.epfl.rechor.timetable.mapped;

import java.nio.ByteBuffer;

public class StructuredBuffer {

    ByteBuffer buffer;
    Structure structure;

    public StructuredBuffer(Structure structure, ByteBuffer buffer) {
         if (buffer.capacity() % structure.totalSize() != 0) {
             throw new IllegalArgumentException("buffer size must be a multiple of structure size: " + structure.totalSize());
         }

         this.buffer = buffer;
         this.structure = structure;

    }

    public int size() {
        return buffer.capacity() / structure.totalSize();
    }

    public int getU8(int fieldIndex, int elementIndex) {
        return Byte.toUnsignedInt(buffer.get(structure.offset(fieldIndex, elementIndex)));
    }

    public int getU16(int fieldIndex, int elementIndex) {
        return Short.toUnsignedInt(buffer.getShort(structure.offset(fieldIndex, elementIndex)));
    }

    public int getS32(int fieldIndex, int elementIndex) {
        return buffer.getInt(structure.offset(fieldIndex, elementIndex));
    }


}
