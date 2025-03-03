package ch.epfl.rechor.timetable;

/**
 * Represents indexed stations, extending Indexed.
 * Provides methods to retrieve station names and coordinates by index.
 */
public interface Stations extends Indexed{

    /**
     * Returns the name of the station at the given index.
     *
     * @param id the index of the station
     * @return the name of the station
     * @throws IndexOutOfBoundsException if id is not in range [0, size())
     */
    String name(int id);

    /**
     * Returns the longitude of the station at the given index.
     *
     * @param id the index of the station
     * @return the longitude of the station in degrees
     * @throws IndexOutOfBoundsException if id is not in range [0, size())
     */
    double longitude(int id);

    /**
     * Returns the latitude of the station at the given index.
     *
     * @param id the index of the station
     * @return the latitude of the station in degrees
     * @throws IndexOutOfBoundsException if id is not in range [0, size())
     */
    double latitude(int id);
}
