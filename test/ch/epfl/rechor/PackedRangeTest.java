// File: PackedRangeTest.java
package ch.epfl.rechor;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link PackedRange} class.
 */
public class PackedRangeTest {

    @Test
    public void testPackValidRange() {
        int start = 10;
        int end = 20;
        int packed = PackedRange.pack(start, end);
        assertEquals(start, PackedRange.startInclusive(packed));
        assertEquals(end - start, PackedRange.length(packed));
        assertEquals(end, PackedRange.endExclusive(packed));
    }

    @Test
    public void testPackEdgeLength255() {
        int start = 100;
        int end = start + 255;
        int packed = PackedRange.pack(start, end);
        assertEquals(255, PackedRange.length(packed));
        assertEquals(start, PackedRange.startInclusive(packed));
        assertEquals(end, PackedRange.endExclusive(packed));
    }

    @Test
    public void testPackInvalidLength() {
        int start = 10;
        int end = start + 256;
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            PackedRange.pack(start, end);
        });
        assertTrue(exception.getMessage().contains("The duration of the event does not fit in 8 bits"));
    }

    @Test
    public void testPackEdgeStartValue() {
        int start = (1 << 24) - 1;
        int length = 10;
        int end = start + length;
        int packed = PackedRange.pack(start, end);
        assertEquals(start, PackedRange.startInclusive(packed));
        assertEquals(length, PackedRange.length(packed));
        assertEquals(end, PackedRange.endExclusive(packed));
    }

    @Test
    public void testPackInvalidStartValue() {
        int start = 1 << 24;
        int end = start + 10;
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            PackedRange.pack(start, end);
        });
        assertTrue(exception.getMessage().contains("The lower bound does not fit into 24 bits"));
    }

    @Test
    public void testNegativeStartThrows() {
        int start = -5;
        int end = 10;
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            PackedRange.pack(start, end);
        });
        assertTrue(exception.getMessage().contains("The lower bound does not fit into 24 bits"));
    }

    @Test
    public void testNegativeLengthThrows() {
        int start = 20;
        int end = 10;
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            PackedRange.pack(start, end);
        });
        assertTrue(exception.getMessage().contains("The duration of the event does not fit in 8 bits"));
    }
}
