package ch.epfl.rechor.journey;

import ch.epfl.rechor.Bits32_24_8;
import ch.epfl.rechor.timetable.Connections;
import ch.epfl.rechor.timetable.TimeTable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Utility class for extracting journeys from a given profile and departure station.
 * <p>
 * This class cannot be instantiated.
 * </p>
 */
public class JourneyExtractor {

    private JourneyExtractor() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    /**
     * Extracts and returns a sorted list of journeys starting from the specified departure station.
     * <p>
     * Journeys are constructed by interpreting the packed criteria provided by the profile.
     * A journey can consist of multiple transport legs interleaved with foot legs for transfers.
     * </p>
     *
     * @param profile     the profile containing connections, timetable, and trip information
     * @param depStationId the identifier of the departure station
     * @return a sorted list of journeys (sorted by departure and arrival times)
     */
    public static List<Journey> journeys(Profile profile, int depStationId) {
        List<Journey> allJourneys = new ArrayList<>();
        Connections connections = profile.connections();
        TimeTable timeTable = profile.timeTable();

        // Get the Pareto front for the given departure station
        ParetoFront stationParetoFront = profile.forStation(depStationId);
        stationParetoFront.forEach((long packedCriteria) -> {
            // Extracting the data from the packed criteria
            int departureMinutes = PackedCriteria.depMins(packedCriteria);
            int finalArrivalMinutes = PackedCriteria.arrMins(packedCriteria);
            int changes = PackedCriteria.changes(packedCriteria);

            int payload = PackedCriteria.payload(packedCriteria);
            int connectionId = Bits32_24_8.unpack24(payload);
            int stopsToRide = Bits32_24_8.unpack8(payload);

            List<Journey.Leg> legs = new ArrayList<>();

            int currentConnectionId = connectionId;
            int currentStopId = connections.depStopId(currentConnectionId);
            int finalArrivalStopId = -1; // since stopId cannot be negative

            int tripId = connections.tripId(connectionId);
            String destination = profile.trips().destination(tripId);

            // Adding an initial foot leg if the first connection departs from a different station than depStationId
            if (timeTable.stationId(connections.depStopId(connectionId)) != depStationId) {
                int fromStationId = timeTable.stationId(depStationId);
                int toStopId = connections.depStopId(connectionId);
                int toStationId = timeTable.stationId(toStopId);

                Stop footFrom = new Stop(timeTable.stations().name(fromStationId),
                        timeTable.platformName(depStationId),
                        timeTable.stations().longitude(fromStationId),
                        timeTable.stations().latitude(fromStationId));

                Stop footTo = new Stop(timeTable.stations().name(toStationId),
                        timeTable.platformName(toStopId),
                        timeTable.stations().longitude(toStationId),
                        timeTable.stations().latitude(toStationId));

                LocalDateTime footDepartureTime = toDateTime(profile.date(), departureMinutes);
                LocalDateTime footArrivalTime = toDateTime(profile.date(), connections.depMins(connectionId));

                legs.add(0, new Journey.Leg.Foot(footFrom, footDepartureTime, footTo, footArrivalTime));
            }

            while (true) {
                List<Journey.Leg.IntermediateStop> intermediateStops = new ArrayList<>();
                int connectionDepartureStopId = connections.depStopId(currentConnectionId);
                int connectionDepartureMinutes = connections.depMins(currentConnectionId);

                // Process intermediate stops for the current connection leg
                for (int i = 0; i < stopsToRide; i++) {
                    currentConnectionId = connections.nextConnectionId(currentConnectionId);
                    int stopId = connections.depStopId(currentConnectionId);
                    int stationId = timeTable.stationId(stopId);

                    String stationName = timeTable.stations().name(stationId);
                    String platform = timeTable.platformName(stopId);

                    Stop connectionStop = new Stop(stationName, platform,
                            timeTable.stations().longitude(stationId),
                            timeTable.stations().latitude(stationId));
                    int intermediateDepartureMinutes = connections.depMins(currentConnectionId);
                    int intermediateArrivalMinutes = connections.arrMins(currentConnectionId);

                    LocalDateTime intermediateDepartureTime = toDateTime(profile.date(), intermediateDepartureMinutes);
                    LocalDateTime intermediateArrivalTime = toDateTime(profile.date(), intermediateArrivalMinutes);

                    Journey.Leg.IntermediateStop intermediateStop = new Journey.Leg.IntermediateStop(connectionStop,
                            intermediateDepartureTime, intermediateArrivalTime);

                    intermediateStops.add(intermediateStop);
                }

                int routeId = profile.trips().routeId(tripId);
                String routeName = timeTable.routes().name(routeId);
                Vehicle vehicle = timeTable.routes().vehicle(routeId);

                int arrivalStopId = connections.arrStopId(currentConnectionId);
                int arrivalStationId = timeTable.stationId(arrivalStopId);

                int connectionDepartureStationId = timeTable.stationId(connectionDepartureStopId);
                Stop departureStop = new Stop(timeTable.stations().name(connectionDepartureStationId),
                        timeTable.platformName(connectionDepartureStopId),
                        timeTable.stations().longitude(connectionDepartureStationId),
                        timeTable.stations().latitude(connectionDepartureStationId));

                Stop arrivalStop = new Stop(timeTable.stations().name(timeTable.stationId(arrivalStopId)),
                        timeTable.platformName(arrivalStopId),
                        timeTable.stations().longitude(arrivalStationId),
                        timeTable.stations().latitude(arrivalStationId));

                // Converting minutes after midnight into LocalDateTime
                LocalDateTime departureDateTime = toDateTime(profile.date(), connectionDepartureMinutes);
                LocalDateTime arrivalDateTime = toDateTime(profile.date(), connections.arrMins(currentConnectionId));

                Journey.Leg.Transport transportLeg = new Journey.Leg.Transport(departureStop, departureDateTime,
                        arrivalStop, arrivalDateTime, intermediateStops, vehicle, routeName, destination);

                legs.add(transportLeg);
                finalArrivalStopId = connections.arrStopId(currentConnectionId);

                // If no changes occur, the entire journey is already extracted
                if (changes == 0) {
                    break;
                }
                changes--;

                try {
                    int nextConnectionId = connections.nextConnectionId(currentConnectionId);
                    currentStopId = connections.arrStopId(currentConnectionId);

                    long nextPackedCriteria = profile.forStation(timeTable.stationId(currentStopId))
                            .get(finalArrivalMinutes, changes);
                    departureMinutes = PackedCriteria.depMins(nextPackedCriteria);
                    payload = PackedCriteria.payload(nextPackedCriteria);
                    int nextDepConnectionId = Bits32_24_8.unpack24(payload);
                    int nextDepartureStopId = connections.depStopId(nextDepConnectionId);

                    int transferMinutes = timeTable.transfers().minutesBetween(
                            timeTable.stationId(currentStopId), timeTable.stationId(nextDepartureStopId));

                    LocalDateTime footDepartureTime = toDateTime(profile.date(), connections.arrMins(currentConnectionId));
                    LocalDateTime footArrivalTime = toDateTime(profile.date(), connections.arrMins(currentConnectionId) + transferMinutes);

                    // Adding a foot leg if the arrival stop is different from the next departure stop
                    if (nextDepartureStopId != currentStopId) {
                        Stop footFrom = arrivalStop; // safe since it was just added
                        Stop footTo = new Stop(timeTable.stations().name(timeTable.stationId(nextDepartureStopId)),
                                timeTable.platformName(nextDepartureStopId),
                                timeTable.stations().longitude(timeTable.stationId(nextDepartureStopId)),
                                timeTable.stations().latitude(timeTable.stationId(nextDepartureStopId)));

                        legs.add(new Journey.Leg.Foot(footFrom, footDepartureTime, footTo, footArrivalTime));
                    } else {
                        legs.add(new Journey.Leg.Foot(arrivalStop, footDepartureTime, arrivalStop, footArrivalTime));
                    }

                    currentConnectionId = nextDepConnectionId;
                    stopsToRide = Bits32_24_8.unpack8(payload);
                } catch (Exception e) {
                    // Path cannot be continued; exit the current iteration
                    return;
                }
            }

            // Adding a final foot leg if needed
            if (timeTable.stationId(finalArrivalStopId) != profile.arrStationId()) {
                int fromStationId = timeTable.stationId(finalArrivalStopId);
                int toStationId = profile.arrStationId();

                Stop footFrom = new Stop(timeTable.stations().name(fromStationId),
                        timeTable.platformName(finalArrivalStopId),
                        timeTable.stations().longitude(fromStationId),
                        timeTable.stations().latitude(fromStationId));

                Stop footTo = new Stop(timeTable.stations().name(toStationId),
                        timeTable.platformName(toStationId),
                        timeTable.stations().longitude(toStationId),
                        timeTable.stations().latitude(toStationId));

                LocalDateTime footDepartureTime = toDateTime(profile.date(), finalArrivalMinutes);
                // Foot leg with identical departure and arrival times
                legs.add(new Journey.Leg.Foot(footFrom, footDepartureTime, footTo, footDepartureTime));
            }

            try {
                allJourneys.add(new Journey(legs));
            } catch (IllegalArgumentException e) {
                throw e;
            }
        });

        // Sorting the journeys by departure time and then by arrival time
        allJourneys.sort(Comparator.comparing(Journey::depTime)
                .thenComparing(Journey::arrTime));
        return allJourneys;
    }

    /**
     * Converts a number of minutes after midnight into a LocalDateTime on the given date.
     * Automatically handles values less than 0 or greater than 1440 (spilling into the previous or next day).
     *
     * @param date                 the reference date
     * @param minutesAfterMidnight the number of minutes to add to midnight
     * @return a {@code LocalDateTime} representing that offset from midnight
     */
    private static LocalDateTime toDateTime(LocalDate date, int minutesAfterMidnight) {
        return date.atStartOfDay().plusMinutes(minutesAfterMidnight);
    }
}
