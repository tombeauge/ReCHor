package ch.epfl.rechor.timetable.mapped;

import java.nio.ByteBuffer;

/**
 * A class forming combining a bytebuffer and a structure to get a structured buffer.
 *
 * @author Cem Celik
 */
public class StructuredBuffer {

    ByteBuffer buffer;
    Structure structure;

    /**
     * public constructor creating a Structured buffer
     * @param structure describing layout of the bytes
     * @param buffer Bytebuffer which is an array containing bytes
     * @throws IllegalArgumentException is buffersize is not a multiple of the totalsize of the structure
     */
    public StructuredBuffer(Structure structure, ByteBuffer buffer) {
         if (buffer.capacity() % structure.totalSize() != 0) {
             throw new IllegalArgumentException("buffer size must be a multiple of structure size: " + structure.totalSize());
         }

         this.buffer = buffer;
         this.structure = structure;

    }

    /**
     * Returns the number of elements stored in the structured buffer.
     * The size is calculated as the total buffer capacity divided by the size of a single structure.
     *
     * @return The number of elements in the buffer.
     */
    public int size() {
        return buffer.capacity() / structure.totalSize();
    }

    /**
     * Retrieves an unsigned 8-bit integer (U8) from the buffer
     * The method reads 1 byte from the specified field and element index, interpreting it as an unsigned value.
     *
     * @param fieldIndex   The index of the field in the structure.
     * @param elementIndex The index of the element in the buffer.
     * @return The unsigned 8-bit integer value.
     * @throws IndexOutOfBoundsException if the field or element index is invalid.
     */
    public int getU8(int fieldIndex, int elementIndex) {
        return Byte.toUnsignedInt(buffer.get(structure.offset(fieldIndex, elementIndex)));
    }

    /**
     * Retrieves an unsigned 16-bit integer (U16) from the buffer
     * The method reads 2 bytes from the specified field and element index, interpreting it as an unsigned value.
     *
     * @param fieldIndex   The index of the field in the structure.
     * @param elementIndex The index of the element in the buffer.
     * @return The unsigned 16-bit integer value.
     * @throws IndexOutOfBoundsException if the field or element index is invalid.
     */
    public int getU16(int fieldIndex, int elementIndex) {
        return Short.toUnsignedInt(buffer.getShort(structure.offset(fieldIndex, elementIndex)));
    }

    /**
     * Retrieves a signed 32-bit integer (S32) from the buffer.
     * The method reads 4 bytes from the specified field and element index, interpreting it as a signed value.
     *
     * @param fieldIndex   The index of the field in the structure.
     * @param elementIndex The index of the element in the buffer.
     * @return The signed 32-bit integer value.
     * @throws IndexOutOfBoundsException if the field or element index is invalid.
     */
    public int getS32(int fieldIndex, int elementIndex) {
        return buffer.getInt(structure.offset(fieldIndex, elementIndex));
    }


}
