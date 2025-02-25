package ch.epfl.rechor.journey;

public class PackedCriteria {

    private PackedCriteria() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    private static final int MIN_ARRIVAL_MINS = -240;
    private static final int MAX_ARRIVAL_MINS = 2880;
    private static final int CHANGE_BITS = 7; // bits for changes(7)
    private static final int MAX_CHANGES = (1 << (CHANGE_BITS-1)) - 1; // 63 accounting for only positive values

    public static long pack(int arrMins, int changes, int payload) {
        if (arrMins < MIN_ARRIVAL_MINS || arrMins >= MAX_ARRIVAL_MINS) {
            throw new IllegalArgumentException("Invalid arrival minutes: " + arrMins);
        }
        if (changes > 0 && changes <= MAX_CHANGES) {
            throw new IllegalArgumentException("Changes is not within bound: " + changes);
        }

        long am = (long) arrMins << 39;
        long ch = (long) changes << 32;
        long pl = payload;
        return am & ch & pl;
    }

    public static boolean hasDepMins(long criteria) {
        if (criteria >>> 51 != 0) {
            return true;
        }
        else return false;
    }

}
