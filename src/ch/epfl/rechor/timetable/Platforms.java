package ch.epfl.rechor.timetable;

/**
 * Represents indexed platforms.
 * Platforms correspond to train tracks or boarding docks.
 *
 * @author Tom Beaugé
 */
public interface Platforms extends Indexed {

    /**
     * Returns the name of the platform at the given index.
     *
     * @param id the index of the platform
     * @return the name of the platform (e.g., "70" or "A"), can be empty
     * @throws IndexOutOfBoundsException if id is not in range [0, size())
     */
    String name(int id);

    /**
     * Returns the station index to which this platform belongs.
     *
     * @param id the index of the platform
     * @return the station index
     * @throws IndexOutOfBoundsException if id is not in range [0, size())
     */
    int stationId(int id);
}
