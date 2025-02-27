package ch.epfl.rechor;


import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class MyBits32_24_8Test {

    /**
     * Tests packing and unpacking valid values within range.
     */
    @Test
    public void testPackAndUnpackValidValues() {
        int bits24 = 0xABCDEF; // 24-bit value
        int bits8 = 0x42;      // 8-bit value

        int packed = Bits32_24_8.pack(bits24, bits8);

        assertEquals(bits24, Bits32_24_8.unpack24(packed),
                "\n--- EXPECTED unpack24 ---\n" + Integer.toHexString(bits24) +
                        "\n--- ACTUAL ---\n" + Integer.toHexString(Bits32_24_8.unpack24(packed)));

        assertEquals(bits8, Bits32_24_8.unpack8(packed),
                "\n--- EXPECTED unpack8 ---\n" + Integer.toHexString(bits8) +
                        "\n--- ACTUAL ---\n" + Integer.toHexString(Bits32_24_8.unpack8(packed)));

    }

    /**
     * Tests packing and unpacking the minimum valid values (both zero).
     */
    @Test
    public void testPackAndUnpackMinimumValues() {
        int bits24 = 0x000000;
        int bits8 = 0x00;

        int packed = Bits32_24_8.pack(bits24, bits8);

        assertEquals(bits24, Bits32_24_8.unpack24(packed));
        assertEquals(bits8, Bits32_24_8.unpack8(packed));
    }

    /**
     * Tests packing and unpacking the maximum valid values (24-bit and 8-bit limits).
     */
    @Test
    public void testPackAndUnpackMaximumValues() {
        int bits24 = 0xFFFFFF; // Maximum 24-bit value
        int bits8 = 0xFF;      // Maximum 8-bit value

        int packed = Bits32_24_8.pack(bits24, bits8);

        assertEquals(bits24, Bits32_24_8.unpack24(packed));
        assertEquals(bits8, Bits32_24_8.unpack8(packed));
    }

    /**
     * Tests if an exception is thrown when the 24-bit value exceeds the valid range.
     */
    @Test
    public void testPackThrowsExceptionWhenBits24TooLarge() {
        int invalidBits24 = 0x1000000; // 25-bit value
        int bits8 = 0x42;

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                Bits32_24_8.pack(invalidBits24, bits8));
    }

    /**
     * Tests if an exception is thrown when the 8-bit value exceeds the valid range.
     */
    @Test
    public void testPackThrowsExceptionWhenBits8TooLarge() {
        int bits24 = 0x123456;
        int invalidBits8 = 0x1FF; // 9-bit value (out of range)

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                Bits32_24_8.pack(bits24, invalidBits8));
    }

    /**
     * Tests if packing zero values still results in zero.
     */
    @Test
    public void testPackZeroValues() {
        int packed = Bits32_24_8.pack(0, 0);
        assertEquals(0, packed);
    }

    /**
     * Tests if unpacking an arbitrary packed value works correctly.
     */
    @Test
    public void testUnpackArbitraryValue() {
        int bits24 = 0x345678;
        int bits8 = 0xAB;

        int packed = Bits32_24_8.pack(bits24, bits8);

        assertEquals(bits24, Bits32_24_8.unpack24(packed));
        assertEquals(bits8, Bits32_24_8.unpack8(packed));
    }


}
