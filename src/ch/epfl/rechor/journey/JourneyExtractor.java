package ch.epfl.rechor.journey;

import ch.epfl.rechor.Bits32_24_8;
import ch.epfl.rechor.timetable.Connections;
import ch.epfl.rechor.timetable.TimeTable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
            int finalArrMins = PackedCriteria.arrMins(criteria);
            int changes = PackedCriteria.changes(criteria);

            int payload = PackedCriteria.payload(criteria);
            int connectionId = Bits32_24_8.unpack24(payload);
            int stopsToRide = Bits32_24_8.unpack8(payload);

            List<Journey.Leg> legs = new ArrayList<>();

            int currentConnectionId = connectionId;
            int currentStopId = connections.depStopId(currentConnectionId);
            int finalArrivalStopId = -1; //since stopId cannot be negative
            int lastStopArrTime = -1; //TODO FIND BETTER PLACEHOLDER VALUE

            System.out.println("Corrected connection starts at: " + tt.stations().name(tt.stationId(currentStopId)));
            System.out.println("current stop ID: " + currentStopId);
            System.out.println("current station ID " + tt.stationId(currentStopId));
            System.out.println("# OF CHANGES " + changes);


            int tripId = connections.tripId(connectionId);
            String destination = profile.trips().destination(tripId);
            //String destination = profile.trips().destination(connectionId);

            //adding an intial foot leg if the first connection departs from another station than depStationId
            if (tt.stationId(connections.depStopId(connectionId)) != depStationId) {
                int fromStationId = tt.stationId(depStationId);
                int toStopId = connections.depStopId(connectionId);
                int toStationId = tt.stationId(toStopId);

                Stop footFrom = new Stop(tt.stations().name(fromStationId), tt.platformName(depStationId),
                        tt.stations().longitude(fromStationId), tt.stations().latitude(fromStationId));

                Stop footTo = new Stop(tt.stations().name(toStationId), tt.platformName(toStopId),
                        tt.stations().longitude(toStationId), tt.stations().latitude(toStationId));


                LocalDateTime footDepTime = toDateTime(profile.date(), depMins);
                LocalDateTime footArrTime = toDateTime(profile.date(), connections.depMins(connectionId));

                legs.add(0, new Journey.Leg.Foot(footFrom, footDepTime, footTo, footArrTime));
            }


            while (true){
                List<Journey.Leg.IntermediateStop> intermediateStops = new ArrayList<>();
                int depStopId = connections.depStopId(currentConnectionId);

                for (int i = 0; i < stopsToRide; i++) {
                    currentConnectionId = connections.nextConnectionId(currentConnectionId);
                    int stopId = connections.depStopId(currentConnectionId);
                    int stationId = tt.stationId(stopId);

                    String name = tt.stations().name(stationId);
                    System.out.println("going through " + name);
                    String platform = tt.platformName(stopId);

                    Stop connectionStop = new Stop(name, platform, tt.stations().longitude(stationId), tt.stations().latitude(stationId));
                    int intStopDepMins = connections.depMins(currentConnectionId);
                    int intStopArrMins = connections.arrMins(currentConnectionId);

                    LocalDateTime intStopDepDateTime = toDateTime(profile.date(), intStopDepMins);
                    LocalDateTime intStopArrDateTime = toDateTime(profile.date(), intStopDepMins);

                    Journey.Leg.IntermediateStop intStop = new Journey.Leg.IntermediateStop(connectionStop, intStopDepDateTime, intStopArrDateTime);

                    intermediateStops.add(intStop);
                }

                //int tripId = connections.tripId(currentConnectionId);
                int routeId = profile.trips().routeId(tripId);

                String route = tt.routes().name(routeId);
                Vehicle vehicle = tt.routes().vehicle(routeId);

//                int depStopId = connections.depStopId(connectionId);
                //int depStopId = connections.depStopId(currentConnectionId);
                int arrStopId = connections.arrStopId(currentConnectionId);

                int arrStationId = tt.stationId(arrStopId);

                int depStationIdFromDepStop = tt.stationId(depStopId);
                Stop depStop = new Stop(tt.stations().name(depStationIdFromDepStop), tt.platformName(depStopId),
                        tt.stations().longitude(depStationIdFromDepStop), tt.stations().latitude(depStationIdFromDepStop));

                Stop arrStop = new Stop(tt.stations().name(tt.stationId(arrStopId)), tt.platformName(arrStopId), tt.stations().longitude(arrStationId), tt.stations().latitude(arrStationId));

                //converting to minutes from midnight as LocalDateTime
                LocalDateTime departureDateTime = toDateTime(profile.date(), connections.depMins(currentConnectionId));
                LocalDateTime arrivalDateTime = toDateTime(profile.date(), connections.arrMins(currentConnectionId));

                Journey.Leg.Transport leg = new Journey.Leg.Transport(depStop, departureDateTime, arrStop, arrivalDateTime, intermediateStops, vehicle, route, destination);

                legs.add(leg);

                finalArrivalStopId = connections.arrStopId(currentConnectionId);

                //if no changes occurring, entire journey is already extracted
                if (changes == 0) break;

                changes--;

                try {
                    int nextConnectionId = connections.nextConnectionId(currentConnectionId);

                    int arrMins = connections.arrMins(nextConnectionId);
                    currentStopId = connections.arrStopId(currentConnectionId);
                    System.out.println("new stop: " + tt.stations().name(tt.stationId(currentStopId)));


                    long nextCriteria = profile.forStation(tt.stationId(currentStopId)).get(finalArrMins, changes);
                    depMins = PackedCriteria.depMins(nextCriteria); //the departure minutes for the new stop

                    payload = PackedCriteria.payload(nextCriteria);
                    int nextConnId = Bits32_24_8.unpack24(payload);
                    int nextDepStopId = connections.depStopId(nextConnId);


                    int minutesBetween = tt.transfers().minutesBetween(tt.stationId(currentStopId), tt.stationId(nextDepStopId));

                    LocalDateTime footDepTime = toDateTime(profile.date(), connections.arrMins(currentConnectionId));
                    LocalDateTime footArrTime = toDateTime(profile.date(), connections.arrMins(currentConnectionId) + minutesBetween); //like we already stated depMins is the departure time of the next stop

                    //adding a foot leg if we do not arrive at the station of the next departure
                    if (nextDepStopId != currentStopId) {

                        Stop footFrom = arrStop; // safe since it was just added
                        Stop footTo = new Stop(tt.stations().name(tt.stationId(nextDepStopId)), tt.platformName(nextDepStopId),
                                tt.stations().longitude(tt.stationId(nextDepStopId)), tt.stations().latitude(tt.stationId(nextDepStopId)));

                        legs.add(new Journey.Leg.Foot(footFrom, footDepTime, footTo, footArrTime));

                    } else {
                        legs.add(new Journey.Leg.Foot(arrStop, footDepTime, arrStop, footArrTime));
                    }

                    currentConnectionId = nextConnId;
                    stopsToRide = Bits32_24_8.unpack8(payload);

                } catch (Exception e) {
                    System.out.println(e);
                    return; //path cannot be continued
                }

            }

            //adding a final foot leg if needed
            if (tt.stationId(finalArrivalStopId) != profile.arrStationId()) {
                int fromStationId = tt.stationId(finalArrivalStopId);
                int toStationId = profile.arrStationId();

                Stop footFrom = new Stop(tt.stations().name(fromStationId), tt.platformName(finalArrivalStopId),
                        tt.stations().longitude(fromStationId), tt.stations().latitude(fromStationId));

                Stop footTo = new Stop(tt.stations().name(toStationId), tt.platformName(toStationId),
                        tt.stations().longitude(toStationId), tt.stations().latitude(toStationId));

                LocalDateTime footDepTime = toDateTime(profile.date(), finalArrMins);
                LocalDateTime footArrTime = footDepTime;

                legs.add(new Journey.Leg.Foot(footFrom, footDepTime, footTo, footArrTime));
            }

            System.out.println("Attempting to build journey:");
            for (int i = 0; i < legs.size(); i++) {
                Journey.Leg leg = legs.get(i);
                System.out.println("→ " + leg.getClass().getSimpleName() +
                        " from " + leg.depStop() + " (" + leg.depTime() + ")" +
                        " to " + leg.arrStop() + " (" + leg.arrTime() + ")");
                if (leg.depStop() == null || leg.arrStop() == null || leg.depTime() == null || leg.arrTime() == null) {
                    System.out.println("Null field in leg at index " + i);
                }
            }



            try {
                allJourneys.add(new Journey(legs));
            } catch (IllegalArgumentException e) {
                System.out.println("Failed to build journey: " + e.getMessage());
                throw e;
            }

        });

        return allJourneys;
    }

    private static LocalDateTime toDateTime(LocalDate date, int minutesAfterMidnight) {
        return date.atStartOfDay().plusMinutes(minutesAfterMidnight);
    }
}
