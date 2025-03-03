package ch.epfl.rechor.timetable;

/**
 * Represents indexed station aliases.
 * Maps alternative names to official station names.
 *
 * @author Tom Beaugé
 */
public interface StationAliases extends Indexed {

    /**
     * Returns the alternative name at the given index.
     *
     * @param id the index of the alias
     * @return the alternative name
     * @throws IndexOutOfBoundsException if id is not in range [0, size())
     */
    String alias(int id);

    /**
     * Returns the official station name corresponding to the alias at the given index.
     *
     * @param id the index of the alias
     * @return the official station name
     * @throws IndexOutOfBoundsException if id is not in range [0, size())
     */
    String stationName(int id);
}
