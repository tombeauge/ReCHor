package ch.epfl.rechor.journey;

public class PackedCriteria {

    private PackedCriteria() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    public static long pack(int arrMins, int changes, int payload) {
        long am = (long) arrMins << 39;
        long ch = (long) changes << 32;
        long pl = payload;
        return am & ch & pl;
    }

}
