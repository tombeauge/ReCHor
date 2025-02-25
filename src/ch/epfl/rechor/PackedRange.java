package ch.epfl.rechor;

public class PackedRange {
    private PackedRange(){}

    public static int pack(int startInclusive, int endExclusive) {
        int length = (endExclusive - startInclusive) & 0x000000FF; //bitmask to only consider last 8 bits

        if (length != endExclusive - startInclusive) {
            throw new IllegalArgumentException("the duration of the event does not fit in 8 bits");
        }

        int lowerBound = startInclusive << 8;

        if ((lowerBound >>> 8) != startInclusive) {
            throw new IllegalArgumentException("the lower bound does not fit into 24 bits");
        }

        return lowerBound | length;
    }

    public static int length(int interval){
        return interval & 0x000000FF;
    }

    public static int startInclusive(int interval){
        return interval >>> 8;
    }

    public static int endExclusive(int interval){
        return startInclusive(interval) +length(interval);
    }
}
