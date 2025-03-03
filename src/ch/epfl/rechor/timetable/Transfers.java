package ch.epfl.rechor.timetable;

import java.util.NoSuchElementException;

/**
 * Represents indexed transfers between stations.
 *
 * @author Tom Beaugé
 */
public interface Transfers extends Indexed {

    /**
     * Returns the departure station index of the given transfer index.
     *
     * @param id the index of the transfer
     * @return the departure station index
     * @throws IndexOutOfBoundsException if id is not in range [0, size())
     */
    int depStationId(int id);

    /**
     * Returns the transfer duration in minutes.
     *
     * @param id the index of the transfer
     * @return the duration in minutes
     * @throws IndexOutOfBoundsException if id is not in range [0, size())
     */
    int minutes(int id);

    /**
     * Returns the packed interval of transfer indices arriving at the given station.
     *
     * @param stationId the station index
     * @return the packed interval of transfer indices
     * @throws IndexOutOfBoundsException if stationId is not in range [0, size())
     */
    int arrivingAt(int stationId);

    /**
     * Returns the transfer duration in minutes between two given station indices.
     *
     * @param depStationId the departure station index
     * @param arrStationId the arrival station index
     * @return the transfer duration in minutes
     * @throws IndexOutOfBoundsException if either station index is invalid
     * @throws NoSuchElementException if no transfer exists between the given stations
     */
    int minutesBetween(int depStationId, int arrStationId);
}
