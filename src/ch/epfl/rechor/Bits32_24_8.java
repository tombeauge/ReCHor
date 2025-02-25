package ch.epfl.rechor;

public class Bits32_24_8 {

    public static int pack(int bits24, int bits8) {
        if ((bits24 >> 24 == 0) && (bits8 >> 8 == 0)) {
            return bits24 << 8 | bits8;
        }
        else {
            throw new IllegalArgumentException("Integer of bits24 or bits8 are too large") ;
        }
    }

    public static int unpack24(int bits32) {
        return bits32>>8;
    }

    public static int unpack8(int bits32) {
        return bits32&0xff;
    }
}
