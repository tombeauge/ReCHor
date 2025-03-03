package ch.epfl.rechor.timetable;

/**
 * Represents indexed public transport trips.
 *
 * @author Tom Beaugé
 */
public interface Trips extends Indexed {

    /**
     * Returns the route index of the given trip index.
     *
     * @param id the index of the trip
     * @return the route index
     * @throws IndexOutOfBoundsException if id is not in range [0, size())
     */
    int routeId(int id);

    /**
     * Returns the final destination of the given trip index.
     *
     * @param id the index of the trip
     * @return the final destination name
     * @throws IndexOutOfBoundsException if id is not in range [0, size())
     */
    String destination(int id);
}
