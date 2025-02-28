package ch.epfl.rechor;

/**
 * A utility class for packing and unpacking integer ranges into a compact format.
 * The range is stored in a single integer, where the lower 24 bits represent the
 * start of the range, and the last 8 bits represent the length of the range.
 * @author Tom BEAUGE
 */
public class PackedRange {

    /** Private constructor to prevent instantiation. */
    private PackedRange() {}

    /**
     * Packs a start-inclusive and end-exclusive range into a single integer.
     *
     * @param startInclusive The start of the range (inclusive).
     * @param endExclusive The end of the range (exclusive).
     * @return A packed integer representing the range.
     * @throws IllegalArgumentException If the range length exceeds 8 bits
     *                                  or if the start value exceeds 24 bits.
     */
    public static int pack(int startInclusive, int endExclusive) {

        // checks for correct interval
        Preconditions.checkArgument(endExclusive >= startInclusive);

        int length = (endExclusive - startInclusive) & 0x000000FF; // Bitmask to only consider last 8 bits

        if (length != endExclusive - startInclusive) {
            throw new IllegalArgumentException("The duration of the event does not fit in 8 bits");
        }

        int lowerBound = startInclusive << 8;

        if ((lowerBound >>> 8) != startInclusive) {
            throw new IllegalArgumentException("The lower bound does not fit into 24 bits");
        }

        return lowerBound | length;
    }

    /**
     * Extracts the length of the range from a packed integer.
     *
     * @param interval The packed integer representing a range.
     * @return The length of the range.
     */
    public static int length(int interval) {
        return interval & 0x000000FF;
    }

    /**
     * Extracts the start of the range (inclusive) from a packed integer.
     *
     * @param interval The packed integer representing a range.
     * @return The start of the range.
     */
    public static int startInclusive(int interval) {
        return interval >>> 8;
    }

    /**
     * Extracts the end of the range (exclusive) from a packed integer.
     *
     * @param interval The packed integer representing a range.
     * @return The end of the range.
     */
    public static int endExclusive(int interval) {
        return startInclusive(interval) + length(interval);
    }
}
