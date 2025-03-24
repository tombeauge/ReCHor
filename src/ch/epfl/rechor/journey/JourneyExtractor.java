package ch.epfl.rechor.journey;

import ch.epfl.rechor.Bits32_24_8;
import ch.epfl.rechor.timetable.Connections;
import ch.epfl.rechor.timetable.TimeTable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class JourneyExtractor {

    private JourneyExtractor() {throw new UnsupportedOperationException("This class cannot be instantiated");}

    public static List<Journey> journeys(Profile profile, int depStationId){
        List<Journey> allJourneys = new ArrayList<>();
        Connections connections = profile.connections();

        TimeTable tt = profile.timeTable();


        ParetoFront pf = profile.forStation(depStationId);
        pf.forEach((long criteria) -> {
            //extracting the data from the packed criteria
            int depMins = PackedCriteria.depMins(criteria);
            int arrMins = PackedCriteria.arrMins(criteria);
            int changes = PackedCriteria.changes(criteria);

            int payload = PackedCriteria.payload(criteria);
            int connectionId = Bits32_24_8.unpack8(payload);
            int stopsToRide = Bits32_24_8.unpack24(payload);

            List<Journey.Leg> legs = new ArrayList<>();

            int currentConnectionId = connectionId;
            int currentStopId = connections.depStopId(currentConnectionId);
            int finalArrivalStopId = -1; //since stopId cannot be negative

            String destination = profile.trips().destination(connectionId);
            
            while (true){
                List<Journey.Leg.IntermediateStop> intermediateStops = new ArrayList<>();

                for (int i = 0; i < stopsToRide; i++) {
                    currentConnectionId = connections.nextConnectionId(currentConnectionId);

                    Stop connectionStop = new Stop(tt.platformName(currentConnectionId), tt.platformName(currentConnectionId), tt.stations().longitude(currentConnectionId), tt.stations().latitude(currentConnectionId));
                    int intStopDepMins = connections.depMins(currentConnectionId);
                    int intStopArrMins = connections.arrMins(currentConnectionId);

                    LocalDateTime intStopDepDateTime = profile.date().atTime(intStopDepMins / 60, intStopDepMins % 60);
                    LocalDateTime intStopArrDateTime = profile.date().atTime(intStopArrMins / 60, intStopArrMins % 60);

                    Journey.Leg.IntermediateStop intStop = new Journey.Leg.IntermediateStop(connectionStop, intStopDepDateTime, intStopArrDateTime);

                    intermediateStops.add(intStop);
                }

                int tripId = connections.tripId(currentConnectionId);
                int routeId = profile.trips().routeId(tripId);

                String route = tt.routes().name(routeId);
                Vehicle vehicle = tt.routes().vehicle(routeId);

                Stop depStop = new Stop(tt.platformName(connectionId), tt.platformName(connectionId) , tt.stations().longitude(connectionId), tt.stations().latitude(connectionId));
                Stop arrStop = new Stop(tt.platformName(currentConnectionId), tt.platformName(currentConnectionId), tt.stations().longitude(currentConnectionId), tt.stations().latitude(currentConnectionId));

                //converting to minutes from midnight as LocalDateTime
                LocalDateTime departureDateTime = profile.date().atTime(depMins / 60, depMins % 60);
                LocalDateTime arrivalDateTime = profile.date().atTime(arrMins / 60, arrMins % 60);

                Journey.Leg.Transport leg = new Journey.Leg.Transport(depStop, departureDateTime, arrStop, arrivalDateTime, intermediateStops, vehicle, route, destination);

                legs.add(leg);

                finalArrivalStopId = connections.arrStopId(currentConnectionId);

                //if no changes occurring, entire journey is already extracted
                if (changes == 0) break;

                changes--;

                try {
                    long nextCriteria = profile.forStation(currentStopId).get(arrMins, changes);
                    payload = PackedCriteria.payload(nextCriteria);
                    currentConnectionId = Bits32_24_8.unpack8(payload);
                    stopsToRide = Bits32_24_8.unpack24(payload);
                } catch (Exception e) {
                    return; //path cannot be continued
                }

                //adding a foot leg if we do not arrive at the station of the next departure
                int nextDepStopId = connections.depStopId(currentConnectionId);
                if (tt.stationId(nextDepStopId) != tt.stationId(currentStopId)) {
                    Stop footFrom = new Stop(tt.platformName(currentStopId), tt.platformName(currentStopId),
                            tt.stations().longitude(currentStopId), tt.stations().latitude(currentStopId));
                    Stop footTo = new Stop(tt.platformName(nextDepStopId), tt.platformName(nextDepStopId),
                            tt.stations().longitude(nextDepStopId), tt.stations().latitude(nextDepStopId));

                    LocalDateTime footDepTime = profile.date().atTime(connections.arrMins(currentConnectionId) / 60,
                            connections.arrMins(currentConnectionId) % 60);
                    LocalDateTime footArrTime = profile.date().atTime(connections.depMins(currentConnectionId) / 60,
                            connections.depMins(currentConnectionId) % 60);

                    legs.add(new Journey.Leg.Foot(footFrom, footDepTime, footTo, footArrTime));
                }

                //adding an intial foot leg if the first connection departs from another station than depStationId
                if (tt.stationId(connections.depStopId(connectionId)) != depStationId) {
                    Stop footFrom = new Stop(tt.platformName(depStationId), tt.platformName(depStationId),
                            tt.stations().longitude(depStationId), tt.stations().latitude(depStationId));
                    Stop footTo = new Stop(tt.platformName(connections.depStopId(connectionId)), tt.platformName(connections.depStopId(connectionId)),
                            tt.stations().longitude(connections.depStopId(connectionId)), tt.stations().latitude(connections.depStopId(connectionId)));

                    LocalDateTime footDepTime = profile.date().atTime(depMins / 60, depMins % 60);
                    LocalDateTime footArrTime = profile.date().atTime(connections.depMins(connectionId) / 60,
                            connections.depMins(connectionId) % 60);

                    legs.add(0, new Journey.Leg.Foot(footFrom, footDepTime, footTo, footArrTime));
                }

                //adding a final foot leg if needed
                if (tt.stationId(finalArrivalStopId) != profile.arrStationId()) {
                    Stop footFrom = new Stop(tt.platformName(finalArrivalStopId), tt.platformName(finalArrivalStopId),
                            tt.stations().longitude(finalArrivalStopId), tt.stations().latitude(finalArrivalStopId));
                    Stop footTo = new Stop(tt.platformName(profile.arrStationId()), tt.platformName(profile.arrStationId()),
                            tt.stations().longitude(profile.arrStationId()), tt.stations().latitude(profile.arrStationId()));

                    LocalDateTime footDepTime = profile.date().atTime(arrMins / 60, arrMins % 60);
                    LocalDateTime footArrTime = profile.date().atTime(arrMins / 60, arrMins % 60); // same arrival time

                    legs.add(new Journey.Leg.Foot(footFrom, footDepTime, footTo, footArrTime));
                }

            }

            allJourneys.add(new Journey(legs));

        });

        return allJourneys;
    }

}
