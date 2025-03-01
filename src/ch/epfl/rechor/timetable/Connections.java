package ch.epfl.rechor.timetable;

/**
 * Represents indexed connections, which are ordered by decreasing departure time.
 *
 * @author Tom Beaugé
 */
public interface Connections extends Indexed {

    /**
     * Returns the departure stop index of the given connection.
     *
     * @param id the index of the connection
     * @return the departure stop index
     * @throws IndexOutOfBoundsException if id is not in range [0, size())
     */
    int depStopId(int id);

    /**
     * Returns the departure time of the given connection in minutes after midnight.
     *
     * @param id the index of the connection
     * @return the departure time in minutes after midnight
     * @throws IndexOutOfBoundsException if id is not in range [0, size())
     */
    int depMins(int id);

    /**
     * Returns the arrival stop index of the given connection.
     *
     * @param id the index of the connection
     * @return the arrival stop index
     * @throws IndexOutOfBoundsException if id is not in range [0, size())
     */
    int arrStopId(int id);

    /**
     * Returns the arrival time of the given connection in minutes after midnight.
     *
     * @param id the index of the connection
     * @return the arrival time in minutes after midnight
     * @throws IndexOutOfBoundsException if id is not in range [0, size())
     */
    int arrMins(int id);

    /**
     * Returns the trip index associated with the given connection.
     *
     * @param id the index of the connection
     * @return the trip index
     * @throws IndexOutOfBoundsException if id is not in range [0, size())
     */
    int tripId(int id);

    /**
     * Returns the position of the connection within its trip.
     * The first connection of a trip has position 0.
     *
     * @param id the index of the connection
     * @return the position of the connection within its trip
     * @throws IndexOutOfBoundsException if id is not in range [0, size())
     */
    int tripPos(int id);

    /**
     * Returns the index of the next connection in the same trip.
     * If this connection is the last in the trip, returns the first connection of the trip.
     *
     * @param id the index of the connection
     * @return the index of the next connection in the trip
     * @throws IndexOutOfBoundsException if id is not in range [0, size())
     */
    int nextConnectionId(int id);
}
