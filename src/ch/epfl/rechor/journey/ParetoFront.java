package ch.epfl.rechor.journey;

import java.util.NoSuchElementException;
import java.util.function.LongConsumer;

public final class ParetoFront {

    public static final ParetoFront EMPTY = new ParetoFront(new long[0]);
    private final long[] packedCriteria;

    private ParetoFront(long[] packedCriteria) {
        this.packedCriteria = packedCriteria;
    }

    public int size(){
        return packedCriteria.length;
    }

    public long get(int arrMins, int changes){
        for (long criteria : packedCriteria) {
            if (PackedCriteria.arrMins(criteria) == arrMins && PackedCriteria.changes(criteria) == changes) {
                return criteria; //there can be at most one such element
            }
        }
        throw new NoSuchElementException("No criteria found for given arrival minutes and changes.");
    }

    public void forEach(LongConsumer action){
        for (long value : packedCriteria){
            action.accept(value);
        }
    }
}
