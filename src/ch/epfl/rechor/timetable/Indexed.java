package ch.epfl.rechor.timetable;

/**
 * Represents an indexed collection of data.
 * The data is conceptually stored in an array and identified by an index
 * ranging from 0 (inclusive) to size (exclusive).
 */
public interface Indexed {

    /**
     * Returns the number of elements in the data set.
     *
     * @return the size of the indexed data
     */
    int size();
}
