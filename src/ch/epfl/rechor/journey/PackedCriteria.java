package ch.epfl.rechor.journey;

/**
 * Utility class for packing and unpacking criteria values into a single long value.
 * This class encodes arrival minutes, number of changes, and a payload into a compact format.
 *
 * @author Cem Celik (399448)
 */
public class PackedCriteria {

    /**
     * Private constructor with no parameters to prevent instantiation
     */
    private PackedCriteria() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    private static final int MIN_ARRIVAL_MINS = -240;
    private static final int MAX_ARRIVAL_MINS = 2880;
    private static final int CHANGE_BITS = 7;
    private static final int MAX_CHANGES = (1 << (CHANGE_BITS)) - 1; // 127
    private static final int ARR_BITS = 12;
    private static final int MAX_ARRBITS = (1 << ARR_BITS) - 1;
    private static final int PAY_BITS = 32;
    private static final long MAX_PAYBITS = (1L << PAY_BITS) - 1;

    /**
     * Packs the given criteria values into a long.
     *
     * @param arrMins  Arrival time in minutes (must be within valid range).
     * @param changes  Number of changes (must be within valid range).
     * @param payload  Additional data payload.
     * @return A long value representing the packed criteria.
     * @throws IllegalArgumentException if the input values are out of bounds.
     */
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
        return am | ch | pl;
    }

    /**
     * Checks if the packed criteria contains departure minutes.
     *
     * @param criteria The packed criteria.
     * @return True if departure minutes are present, false otherwise.
     */
    public static boolean hasDepMins(long criteria) {
        if (criteria >>> 51 != 0) {
            return true;
        }
        else return false;
    }

    /**
     * Extracts the departure minutes from the packed criteria.
     *
     * @param criteria The packed criteria.
     * @return The departure minutes.
     * @throws IllegalArgumentException if departure minutes are not provided.
     */
    public static int depMins(long criteria) {
        if (criteria >>> 51 != 0) {
            return (int) (criteria >>> 51);
        }
        else {
            throw new IllegalArgumentException("Deperature mins not provided");
        }
    }

    /**
     * Extracts the arrival minutes from the packed criteria.
     *
     * @param criteria The packed criteria.
     * @return The arrival minutes.
     */
    public static int arrMins(long criteria) {
        return (int) ((criteria >>> 39) + MIN_ARRIVAL_MINS) & (MAX_ARRBITS);
    }

    /**
     * Extracts the number of changes from the packed criteria.
     *
     * @param criteria The packed criteria.
     * @return The number of changes.
     */
    public static int changes(long criteria) {
        return (int) (criteria >>> 32) & (MAX_CHANGES);
    }

    /**
     * Extracts the payload from the packed criteria.
     *
     * @param criteria The packed criteria.
     * @return The payload.
     */
    public static int payload(long criteria) {
        return (int) (criteria) & (int)(MAX_PAYBITS);
    }

    /**
     * Determines if one criteria dominates or is equal to another.
     * Dominates means arrival time and number of changes is less and departure
     * time is more for criteria 1 compared to criteria 2.
     * @param criteria1 The first criteria.
     * @param criteria2 The second criteria.
     * @return True if criteria1 dominates or is equal to criteria2, false otherwise.
     * @throws IllegalArgumentException if one criteria has departure time and the other does not.
     */
    public static boolean dominatesOrIsEqual(long criteria1, long criteria2) {
        boolean hasDep1 = hasDepMins(criteria1);
        boolean hasDep2 = hasDepMins(criteria2);
        if (hasDep1 != hasDep2) {
            throw new IllegalArgumentException("One criteria has departure time, but the other does not");
        }
        if (hasDep1) {
            return (arrMins(criteria1) <= arrMins(criteria2)) && (changes(criteria1) <= changes(criteria2) && (depMins(criteria1) >= depMins(criteria2)));
        }
        else {
            return (arrMins(criteria1) <= arrMins(criteria2)) && (changes(criteria1) <= changes(criteria2));
        }
    }

    /**
     * Removes departure time from the packed criteria.
     *
     * @param criteria The packed criteria.
     * @return The criteria without departure minutes.
     */
    public static long withoutDepMins(long criteria) {
        return criteria & ((1L << (PAY_BITS + CHANGE_BITS + ARR_BITS)) - 1);
    }

    /**
     * Adds departure minutes to the packed criteria.
     *
     * @param criteria The packed criteria.
     * @param depMins1 The departure minutes to add.
     * @return The updated packed criteria.
     */
    public static long withDepMins(long criteria, int depMins1) {
        return withoutDepMins(criteria) | ((long) depMins1 << 51);
    }

    /**
     * Increments the number of changes in the packed criteria by one.
     * By unpacking and repackaging everything after adding a 1 to the changes value.
     *
     * @param criteria The packed criteria.
     * @return The updated packed criteria with one more change.
     * @throws IllegalArgumentException if by adding one more change it surpasses the maximum amount
     */
    public static long withAdditionalChange(long criteria) {
        int a = arrMins(criteria);
        int b = changes(criteria) + 1;
        int c = payload(criteria);
        if (b > MAX_CHANGES) {
            throw new IllegalArgumentException("Too many changes: " + b);
        }
        if (hasDepMins(criteria)) {
            return withDepMins(pack(a,b,c), depMins(criteria));
        }
        else {
            return pack(a, b, c);
        }
    }

    /**
     * Updates the payload of the packed criteria with the in payload1.
     *
     * @param criteria The packed criteria.
     * @param payload1 The new payload value.
     * @return The updated packed criteria with the new payload.
     */
    public static long withPayload(long criteria, int payload1) {
        return (criteria & -1L << 32) | Integer.toUnsignedLong(payload1);
    }

}
