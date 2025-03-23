package ch.epfl.rechor.journey;

import ch.epfl.rechor.timetable.Connections;
import ch.epfl.rechor.timetable.TimeTable;
import ch.epfl.rechor.timetable.Trips;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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


    public final static class Builder {

        TimeTable timeTable;
        LocalDate date;
        int arrStationId;
        ParetoFront.Builder[] stationsFrontierBuilders;
        ParetoFront.Builder[] journeysFrontierBuilders;

        public Builder(TimeTable timeTable, LocalDate date, int arrStationId) {
            this.timeTable = timeTable;
            this.date = date;
            this.arrStationId = arrStationId;

            //this.journeysFrontierBuilders = new ParetoFront.Builder[arrStationId];
            //this.stationsFrontierBuilders = new ParetoFront.Builder[arrStationId];

        }

        public ParetoFront.Builder forStation(int stationId) {
            return stationsFrontierBuilders[stationId];
        }

        public void setForStation(int stationId, ParetoFront.Builder builder) {
            stationsFrontierBuilders[stationId] = builder;
        }

        public ParetoFront.Builder forTrip(int tripId) {
            return journeysFrontierBuilders[tripId];
        }

        public void setForTrip(int tripId, ParetoFront.Builder builder) {
            journeysFrontierBuilders[tripId] = builder;
        }


        public Profile build() {
            List<ParetoFront> stationFrontiers = new ArrayList<>(stationsFrontierBuilders.length);
            for (int i = 0; i < stationsFrontierBuilders.length; i++) {
                // If the builder is null, replace with ParetoFront.EMPTY
                if(stationFrontiers.get(i) == null){
                    stationFrontiers.set(i, ParetoFront.EMPTY);
                }
                else {
                    stationFrontiers.set(i, stationsFrontierBuilders[i].build());
                }
            }


            return new Profile(timeTable, date, arrStationId, stationFrontiers);

        }

    }
}
