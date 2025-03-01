package ch.epfl.rechor.journey;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.function.LongConsumer;

import static ch.epfl.rechor.PackedRange.pack;

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
        long key = pack(arrMins, changes);
        int index = Arrays.binarySearch(packedCriteria, key);
        if (index < 0){ throw new NoSuchElementException("No criteria found for given arrival minutes and changes."); }
        return packedCriteria[index];   //TODO implemented binary, if does not work revert to previous version
    }

    public void forEach(LongConsumer action){
        for (long value : packedCriteria){
            action.accept(value);
        }
    }
}
