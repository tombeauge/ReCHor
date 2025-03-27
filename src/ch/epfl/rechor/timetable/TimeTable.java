package ch.epfl.rechor.timetable;

import java.time.LocalDate;

/**
 * Represents a public transport timetable.
 *
 * @author Tom Beaugé
 */
public interface TimeTable {

    /**
     * Returns the indexed stations in the timetable.
     *
     * @return the stations in the timetable
     */
    Stations stations();

    /**
     * Returns the indexed alternative station names.
     *
     * @return the station aliases
     */
    StationAliases stationAliases();

    /**
     * Returns the indexed platforms in the timetable.
     *
     * @return the platforms in the timetable
     */
    Platforms platforms();

    /**
     * Returns the indexed public transport routes in the timetable.
     *
     * @return the routes in the timetable
     */
    Routes routes();

    /**
     * Returns the indexed transfers in the timetable.
     *
     * @return the transfers in the timetable
     */
    Transfers transfers();

    /**
     * Returns the indexed trips that are active on a given date.
     *
     * @param date the date for which to retrieve active trips
     * @return the active trips on the given date
     */
    Trips tripsFor(LocalDate date);

    /**
     * Returns the indexed connections that are active on a given date.
     *
     * @param date the date for which to retrieve active connections
     * @return the active connections on the given date
     */
    Connections connectionsFor(LocalDate date);

    /**
     * Checks whether a given stop index represents a station.
     *
     * @param stopId the stop index
     * @return true if the index corresponds to a station, false otherwise
     */
    default boolean isStationId(int stopId) {
        return stopId < stations().size();
    }

    /**
     * Checks whether a given stop index represents a platform.
     *
     * @param stopId the stop index
     * @return true if the index corresponds to a platform, false otherwise
     */
    default boolean isPlatformId(int stopId) {
        return stopId >= stations().size();
    }

    /**
     * Returns the station index corresponding to a given stop index.
     * If the stop index corresponds to a station, it is returned unchanged.
     * If it corresponds to a platform, the corresponding station index is computed.
     *
     * @param stopId the stop index
     * @return the station index
     */
    default int stationId(int stopId) {
        return isStationId(stopId) ? stopId : platforms().stationId(stopId - stations().size());
    }

    /**
     * Returns the platform name associated with a given stop index.
     * If the stop is a station, returns null.
     *
     * @param stopId the stop index
     * @return the platform name, or null if the stop is a station
     */
    default String platformName(int stopId) {
        return isPlatformId(stopId) ? platforms().name(stopId - stations().size()) : null;
    }
}
