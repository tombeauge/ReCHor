package ch.epfl.rechor.journey;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.LongConsumer;

import static ch.epfl.rechor.PackedRange.pack;

/**
 * Represents a Pareto frontier of optimization criteria.
 * The tuples in the frontier are stored in a packed format inside a long[] array.
 * This class is immutable.
 *
 * @author Tom Beaugé
 */
public final class ParetoFront {

    public static final ParetoFront EMPTY = new ParetoFront(new long[0]);
    private final long[] packedCriteria;

    /**
     * Private constructor to ensure immutability.
     *
     * @param packedCriteria the packed optimization criteria.
     */
    private ParetoFront(long[] packedCriteria) {
        this.packedCriteria = packedCriteria;
    }

    /**
     * Returns the number of tuples in the Pareto frontier.
     *
     * @return the size of the Pareto frontier.
     */
    public int size() {
        return packedCriteria.length;
    }

    /**
     * Retrieves the packed optimization criteria corresponding to the given arrival time and number of changes.
     *
     * @param arrMins the arrival time in minutes.
     * @param changes the number of changes.
     * @return the packed criteria matching the parameters.
     * @throws NoSuchElementException if no matching criteria exist.
     */
    public long get(int arrMins, int changes) {
        for (long criteria : packedCriteria) {
            if (PackedCriteria.arrMins(criteria) == arrMins && PackedCriteria.changes(criteria) == changes) {
                return criteria; //there can be at most one such element
            }
        }
        throw new NoSuchElementException("No criteria found for given arrival minutes and changes.");
    }

    /**
     * Iterates over each packed criterion in the Pareto frontier and applies the given action.
     *
     * @param action the action to perform on each packed criterion.
     */
    public void forEach(LongConsumer action) {
        for (long value : packedCriteria) {
            action.accept(value);
        }
    }


    /**
     * Returns a readable string representation of the Pareto frontier.
     *
     * @return a string representation of the frontier.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ParetoFront:\n");
        forEach(value -> {
            int arrMins = PackedCriteria.arrMins(value);
            int changes = PackedCriteria.changes(value);
            Integer depMins = PackedCriteria.hasDepMins(value) ? PackedCriteria.depMins(value) : null;
            sb.append("[");
            if (depMins != null) {
                sb.append("Departure: ").append(depMins).append(" min, ");
            }
            sb.append("Arrival: ").append(arrMins).append(" min, ")
                    .append("Changes: ").append(changes)
                    .append("]\n");
        });
        return sb.toString();
    }

    /**
     * A builder class to construct a Pareto frontier.
     */
    public static class Builder {
        private long[] frontier;
        private int size;
        private int capacity = 2;
        private final double SCALE_UP_ARRAY_FACTOR = 1.5;

        /**
         * Creates an empty builder.
         */
        public Builder() {
            this.frontier = new long[capacity];
            this.size = 0;
        }

        /**
         * Creates a copy of an existing builder.
         *
         * @param that the builder to copy.
         */
        public Builder(Builder that) {
            this.capacity = that.capacity;
            this.size = that.size;
            this.frontier = Arrays.copyOf(that.frontier, that.capacity);
        }

        /**
         * Checks if the builder is empty.
         *
         * @return true if the builder is empty, false otherwise.
         */
        public boolean isEmpty() {
            return size == 0;
        }

        /**
         * Clears all tuples in the builder.
         *
         * @return this builder after clearing.
         */
        public Builder clear() {
            size = 0;
            return this;
        }

        /**
         * Adds a packed tuple to the Pareto frontier if it is not dominated.
         *
         * @param packedTuple the packed tuple to add.
         * @return this builder after the addition.
         */
        public Builder add(long packedTuple) {
            int i = 0;
            boolean inserted = false;

            //setting the payload of the new tuple to 0 for comparisons
            long newTupleForComp = PackedCriteria.withPayload(packedTuple, 0);

            //removing the payload for comparison so it does not affect result
            while (i < size) {

                long currentForComp = PackedCriteria.withPayload(frontier[i], 0);

                //if the new insertion is already dominated nothing happens
                if (PackedCriteria.dominatesOrIsEqual(currentForComp, newTupleForComp)) {
                    return this;
                }

                if (currentForComp > newTupleForComp) {
                    break;
                }

                i++;
            }

            int insertionPoint = i;
            int dst = insertionPoint;

            int n = size;
            for (int src = insertionPoint; src < n; src++) {
                if (PackedCriteria.dominatesOrIsEqual(newTupleForComp, PackedCriteria.withPayload(frontier[src], 0))) {
                    System.out.println(frontier[src] + " is dominated");
                    if (!inserted){
                        //System.out.println("to remove through insertion: " + frontier[src]);
                        frontier[src] = packedTuple;
                        inserted = true;
                        dst++;
                    }
                    else {
                        size--;
                        //System.out.println("to remove: " + frontier[src]);
                    }
                }
                else {
                    if (dst != src) {
                        frontier[dst] = frontier[src];
                    }
                    dst++;
                }
//                System.out.println("dst: " + dst + " vs src: " + src);
//                System.out.println("size: "+ size);
            }


            //if we didnt insert the new tuple by replacing one that was dominated
            //we insert it by moving the whole array to the right
            if (!inserted) {
                insert(packedTuple, insertionPoint);
            }

            System.out.println("inserting " + criteriaToString(packedTuple));

            return this;
        }


        /**
         * Adds a tuple to the frontier with the given arrival time, number of changes, and payload.
         *
         * @param arrMins the arrival time in minutes.
         * @param changes the number of changes.
         * @param payload the payload value.
         * @return this builder after the addition.
         */
        public Builder add(int arrMins, int changes, int payload) {
            long packed = PackedCriteria.pack(arrMins, changes, payload);
            return add(packed);
        }

        /**
         * Adds all tuples from another builder's frontier.
         *
         * @param that the builder to copy tuples from.
         * @return this builder after adding all elements.
         */
        public Builder addAll(Builder that) {
            for (int i = 0; i < that.size; i++) {
                add(that.frontier[i]);
            }

            return this;
        }

        /**
         * Checks if all tuples in the given builder are dominated by at least one tuple in this builder.
         *
         * @param that the builder to compare against.
         * @param depMins the departure time in minutes.
         * @return true if all tuples in 'that' are dominated, false otherwise.
         */
        public boolean fullyDominates(Builder that, int depMins) {
            for (int i = 0; i < that.size; i++) {
                long forcedDepThat = PackedCriteria.withDepMins(that.frontier[i], depMins);

                boolean dominated = false;

                for (int j = 0; j < this.size; j++) {

                    //this should always have depMins (otherwise exception is raised)
                    if (PackedCriteria.dominatesOrIsEqual(this.frontier[j], forcedDepThat)) {
                        dominated = true;
                        break;
                    }
                }

                if (!dominated){
                    return false;
                }

            }

            return true;
        }

        /**
         * Iterates over each packed criterion in the builder's frontier and applies the given action.
         *
         * @param action the action to perform on each packed criterion.
         */
        public void forEach(LongConsumer action) {
            for (int i = 0; i < size; i++) {
                action.accept(frontier[i]);
            }
        }



        /**
         * Constructs an immutable ParetoFront from the builder.
         *
         * @return the constructed ParetoFront instance.
         */
        public ParetoFront build() {
            long[] newFrontier = new long[size];
            System.arraycopy(frontier, 0, newFrontier, 0, size);
            return new ParetoFront(newFrontier);
        }

        /**
         * Returns a readable string representation of the Builder's current Pareto frontier.
         *
         * @return a string representation of the frontier.
         */
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("Builder:\n");
            for (int i = 0; i < size; i++) {
                long value = frontier[i];
                int arrMins = PackedCriteria.arrMins(value);
                int changes = PackedCriteria.changes(value);
                Integer depMins = PackedCriteria.hasDepMins(value) ? PackedCriteria.depMins(value) : null;
                sb.append("[");

                if (depMins != null) {
                    sb.append("Departure: ").append(depMins).append(" min, ");
                }
                sb.append("Arrival: ").append(arrMins).append(" min, ")
                        .append("Changes: ").append(changes)
                        .append("]\n");
            }
            return sb.toString();
        }

        /*************************************************
         *                 OWN METHODS                   *
         *************************************************/

        /**
         * Checks if one packed tuple strictly dominates another.
         *
         * A tuple strictly dominates another if it is not equal to the other tuple
         * and has better or equal values in all criteria.
         *
         * @param a the first packed tuple.
         * @param b the second packed tuple to compare against.
         * @return true if tuple a strictly dominates tuple b, false otherwise.
         * TODO: put it packed criteria if keeping method
         */
        private boolean strictlyDominates(long a, long b) {

            boolean hasDiffDepMins = false;

            if(PackedCriteria.hasDepMins(a) && PackedCriteria.hasDepMins(b)){
                if (PackedCriteria.depMins(a) != PackedCriteria.depMins(b)){
                    hasDiffDepMins = true;
                }
            }

            return PackedCriteria.dominatesOrIsEqual(a, b) &&
                    //ensuring we don't consider two tuples that are equal
                    (hasDiffDepMins ||
                            (PackedCriteria.arrMins(a) != PackedCriteria.arrMins(b)) ||
                            PackedCriteria.changes(a) != PackedCriteria.changes(b));

        }


        /**
         * Inserts a packed tuple into the frontier at the specified position.
         * Ensures the array has enough capacity before performing the insertion.
         *
         * @param packedTuple the packed tuple to insert.
         * @param pos the position at which to insert the tuple.
         */
        private void insert(long packedTuple, int pos) {

            //ensures array has sufficient capacity
            if (capacity == size) {
                capacity = (int) Math.ceil(capacity * SCALE_UP_ARRAY_FACTOR);
                frontier = Arrays.copyOf(frontier, capacity);
            }


            System.arraycopy(frontier, pos, frontier, pos + 1, size - pos);
            frontier[pos] = packedTuple;

            size++;
        }

        //TODO DELETE
        public String criteriaToString(long crit) {
            StringBuilder sb = new StringBuilder("entry:\n");
            long value = crit;
            int arrMins = PackedCriteria.arrMins(value);
            int changes = PackedCriteria.changes(value);
            Integer depMins = PackedCriteria.hasDepMins(value) ? PackedCriteria.depMins(value) : null;
            sb.append("[");

            if (depMins != null) {
                sb.append("Departure: ").append(depMins).append(" min, ");
            }
            sb.append("Arrival: ").append(arrMins).append(" min, ")
                    .append("Changes: ").append(changes)
                    .append("]\n");

            return sb.toString();
        }

    }
}