package ch.epfl.rechor;

/**
 * public utility class for packaging and unpackaging bits of sizes 8, 24, 32.
 *
 */
public class Bits32_24_8 {

    /**
     * Private constructor with no parameters to prevent instantiation
     */
    private Bits32_24_8() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    /**
     * @param bits24
     * @param bits8
     * @return A packaged integer with 32 bits made up bits24 for the high order bits
     * and the bits8 in the low order bits. In the case that either bits8 or bits24 do not fit within
     * the 24 or 8 bit limits an illegal argument exception is thrown.
     */
    public static int pack(int bits24, int bits8) {
        if ((bits24 >>> 24 == 0) && (bits8 >>> 8 == 0)) {
            return bits24 << 8 | bits8;
        }
        else {
            throw new IllegalArgumentException("Integer of bits24 or bits8 are too large") ;
        }
    }

    /**
     *
     * @param bits32
     * @return the high order 24 bits
     */
    public static int unpack24(int bits32) {
        return bits32 >>> 8;
    }

    /**
     *
     * @param bits32
     * @return the low order 8 bits
     */
    public static int unpack8(int bits32) {
        return bits32 & 0xff;
    }
}
