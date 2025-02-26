package ch.epfl.rechor.journey;

public class PackedCriteria {

    private PackedCriteria() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    private static final int MIN_ARRIVAL_MINS = -240;
    private static final int MAX_ARRIVAL_MINS = 2880;
    private static final int CHANGE_BITS = 7; // bits for changes(7)
    private static final int MAX_CHANGES = (1 << (CHANGE_BITS)) - 1; // 127
    private static final int ARR_BITS = 12;
    private static final int MAX_ARRBITS = (1 << ARR_BITS) - 1;
    private static final int PAY_BITS = 32;
    private static final long MAX_PAYBITS = (1L << PAY_BITS) - 1;


    public static long pack(int arrMins, int changes, int payload) {
        if (arrMins < MIN_ARRIVAL_MINS || arrMins >= MAX_ARRIVAL_MINS) {
            throw new IllegalArgumentException("Invalid arrival minutes: " + arrMins);
        }
        if (changes >= MAX_CHANGES || changes < 0) {
            throw new IllegalArgumentException("Changes is not within bound: " + changes);
        }

        long am = (long) (arrMins - MIN_ARRIVAL_MINS) << 39;
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

    public static int depMins(long criteria) {
        if (criteria >>> 51 != 0) {
            return (int) (criteria >>> 51);
        }
        else {
            throw new IllegalArgumentException("Deperature mins not provided");
        }
    }

    public static int arrMins(long criteria) {
        return (int) ((criteria >>> 39)+ MIN_ARRIVAL_MINS) & (MAX_ARRBITS);
    }

    public static int changes(long criteria) {
        return (int) (criteria >>> 32) & (MAX_CHANGES);
    }

    public static int payload(long criteria) {
        return (int) (criteria) & (int)(MAX_PAYBITS);
    }

    //assuming that dominates means arrival time and n. of changes is less and dep. time is more for criteria 1 compared to criteria 2
    public static boolean dominatesOrIsEqual(long criteria1, long criteria2) {
        boolean hasDep1 = hasDepMins(criteria1);
        boolean hasDep2 = hasDepMins(criteria2);
        if (hasDep1 != hasDep2) {
            throw new IllegalArgumentException("One criteria has departure time, but the other does not");
        }
        if (hasDep1 = true) {
            return (arrMins(criteria1) <= arrMins(criteria2)) && (changes(criteria1) <= changes(criteria2) && (depMins(criteria1) >= depMins(criteria2)));
        }
        else {
            return (arrMins(criteria1) <= arrMins(criteria2)) && (changes(criteria1) <= changes(criteria2));
        }
    }

    public static long withoutDepMins(long criteria) {
        return criteria & ((1L << PAY_BITS + CHANGE_BITS + ARR_BITS) - 1);
    }

    public static long withDepMins(long criteria, int depMins1) {
        return withoutDepMins(criteria) | ((long) depMins1 << 51);
    }

    public static long withAdditionalChange(long criteria) {
        int a = arrMins(criteria);
        int b = changes(criteria) + 1;
        int c = payload(criteria);
        if (hasDepMins(criteria)) {
            return withDepMins(pack(a,b,c), depMins(criteria));
        }
        else {
            return pack(a, b, c);
        }

    }

    public static long withPayload(long criteria, int payload1) {
        return (criteria >>> PAY_BITS) << PAY_BITS + payload1;
    }




}
