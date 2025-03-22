package ch.epfl.rechor.journey;

import ch.epfl.rechor.timetable.Connections;
import ch.epfl.rechor.timetable.TimeTable;
import ch.epfl.rechor.timetable.Trips;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * Represents a profile containing a Pareto frontier for each station
 * on a given date and destination.
 *
 * @param timeTable     the timetable from which the profile is computed
 * @param date          the date the profile applies to
 * @param arrStationId  the arrival station index for the profile
 * @param stationFront  list of Pareto frontiers indexed by station ID
 */
public final record Profile(TimeTable timeTable, LocalDate date, int arrStationId, List<ParetoFront> stationFront) {

    /**
     * Compact constructor that ensures immutability of the frontier list.
     *
     * @param timeTable     the timetable (non-null)
     * @param date          the date (non-null)
     * @param arrStationId  arrival station index
     * @param stationFront  frontiers for each station (copied)
     */
    public Profile {
        Objects.requireNonNull(timeTable);
        Objects.requireNonNull(date);
        stationFront = List.copyOf(stationFront);
    }

    /**
     * Returns the connections of the profile (same as those in the timetable).
     *
     * @return the connections for the profile's date
     */
    public Connections connections(){
        return timeTable.connectionsFor(date);
    }

    /**
     * Returns the trips of the profile (same as those in the timetable).
     *
     * @return the trips for the profile's date
     */
    public Trips trips(){
        return timeTable.tripsFor(date);
    }

    /**
     * Returns the Pareto frontier for the station with the given index.
     *
     * @param stationId the index of the station
     * @return the corresponding Pareto frontier
     * @throws IndexOutOfBoundsException if the stationId is invalid
     */
    public ParetoFront forStation(int stationId){
        return stationFront.get(stationId);
    }
}
