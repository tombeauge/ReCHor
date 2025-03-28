package ch.epfl.rechor.journey;

import ch.epfl.rechor.Bits32_24_8;
import ch.epfl.rechor.timetable.Connections;
import ch.epfl.rechor.timetable.TimeTable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Extracts all optimal journeys from a Profile for a given departure station.
 * Real transport data (vehicle, route, etc.) is used directly from the timetable.
 */
public final class JourneyExtractor {
    private JourneyExtractor() {}

    public static List<Journey> journeys(Profile profile, int depStationId) {
        Objects.requireNonNull(profile);
        List<Journey> journeys = new ArrayList<>();
        TimeTable tt = profile.timeTable();
        Connections connections = profile.connections();
        LocalDate date = profile.date();
        int arrStationId = profile.arrStationId();

        profile.forStation(depStationId).forEach(criteria -> {
            try {
                List<Journey.Leg> legs = new ArrayList<>();
                int changes = PackedCriteria.changes(criteria);
                int arrMins = PackedCriteria.arrMins(criteria);

                int payload = PackedCriteria.payload(criteria);
                int connId = Bits32_24_8.unpack24(payload);
                int stopsToRide = Bits32_24_8.unpack8(payload);

                System.out.println("con id " + connId);

                int depStopId = connections.depStopId(connId);
                int depStation = tt.stationId(depStopId);

                //System.out.println(tt.stations().name(depStation));

                // Add initial foot leg if needed
                if (depStation != depStationId) {
                    legs.add(new Journey.Leg.Foot(
                            stopOf(depStationId, tt),
                            toDateTime(date, PackedCriteria.depMins(criteria)),
                            stopOf(depStopId, tt),
                            toDateTime(date, connections.depMins(connId))
                    ));
                }

                while (true) {
                    int tripId = connections.tripId(connId);
                    //int depStopIdTest = connections.depStopId(connId);
                    //System.out.println("STATION " + stopOf(depStopIdTest, tt).name());
                    //String destination = profile.trips().destination(connId);
                    //System.out.println("destination: " + destination);
                    //System.out.println("station: " + tt.stationId(depStopIdTest));

                    int routeId = profile.trips().routeId(tripId);
                    String route = tt.routes().name(routeId);
                    System.out.println("route " + route);
                    Vehicle vehicle = tt.routes().vehicle(routeId);

                    List<Journey.Leg.IntermediateStop> intermediateStops = new ArrayList<>();

                    int startConnId = connId;
                    for (int i = 0; i < stopsToRide; i++) {
                        connId = connections.nextConnectionId(connId);
                        int interStopId = connections.depStopId(connId);
                        System.out.println(connId);
                        int interStationID = tt.stationId(interStopId);
                        System.out.println(tt.stations().name(interStationID));
                        LocalDateTime t = toDateTime(date, connections.depMins(connId));
                        intermediateStops.add(new Journey.Leg.IntermediateStop(stopOf(interStopId, tt), t, t));
                    }

                    int endDepMins = connections.depMins(startConnId);
                    int endArrMins = connections.arrMins(connId);
                    int endDepStopId = connections.depStopId(startConnId);
                    int endArrStopId = connections.arrStopId(connId);

                    int legArrStationId = tt.stationId(endArrStopId);


                    System.out.println(vehicle);

                    legs.add(new Journey.Leg.Transport(
                            stopOf(endDepStopId, tt),
                            toDateTime(date, endDepMins),
                            stopOf(endArrStopId, tt),
                            toDateTime(date, endArrMins),
                            intermediateStops,
                            vehicle,
                            route,
                            tt.stations().name(legArrStationId)
                    ));

                    if (changes == 0) {
                        // Final foot leg if needed
                        int finalStation = tt.stationId(endArrStopId);
                        if (finalStation != arrStationId) {
                            LocalDateTime t = toDateTime(date, endArrMins);
                            legs.add(new Journey.Leg.Foot(
                                    stopOf(endArrStopId, tt),
                                    t,
                                    stopOf(arrStationId, tt),
                                    t
                            ));
                        }
                        journeys.add(new Journey(legs));
                        return;
                    }

                    changes--;
                    int nextStation = tt.stationId(endArrStopId);
                    int nextArrMins = connections.arrMins(connId);

                    try {
                        long nextCriteria = profile.forStation(nextStation).get(nextArrMins, changes);
                        payload = PackedCriteria.payload(nextCriteria);
                        connId = Bits32_24_8.unpack24(payload);
                        stopsToRide = Bits32_24_8.unpack8(payload);
                    } catch (Exception e) {
                        return; // incomplete path, skip
                    }

                    int nextDepStopId = connections.depStopId(connId);
                    if (tt.stationId(nextDepStopId) != tt.stationId(endArrStopId)) {
                        int transferDep = connections.arrMins(connId);
                        int transferArr = connections.depMins(connId);
                        legs.add(new Journey.Leg.Foot(
                                stopOf(endArrStopId, tt),
                                toDateTime(date, transferDep),
                                stopOf(nextDepStopId, tt),
                                toDateTime(date, transferArr)
                        ));
                    }
                }

            } catch (Exception ignored) {
                // Ignore malformed or incomplete journeys
                System.out.println(ignored.toString());
            }
        });

        journeys.sort(Comparator.comparing(Journey::depTime).thenComparing(Journey::arrTime));
        return journeys;
    }

    private static Stop stopOf(int stopId, TimeTable tt) {
        int stationId = tt.platforms().stationId(stopId);
        return new Stop(
                tt.stations().name(stationId),
                tt.platformName(stopId),
                tt.stations().longitude(stationId),
                tt.stations().latitude(stationId)
        );
    }

    private static LocalDateTime toDateTime(LocalDate date, int mins) {
        return date.atStartOfDay().plusMinutes(mins);
    }
}
