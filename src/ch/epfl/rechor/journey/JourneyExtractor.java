package ch.epfl.rechor.journey;

import ch.epfl.rechor.Bits32_24_8;
import ch.epfl.rechor.timetable.Connections;
import ch.epfl.rechor.timetable.TimeTable;

import java.util.ArrayList;
import java.util.List;

public class JourneyExtractor {

    private JourneyExtractor() {throw new UnsupportedOperationException("This class cannot be instantiated");}

    public List<Journey> journeys(Profile profile, int depStationId){
        List<Long> unsortedJourneys = new ArrayList<>();
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

            String destination = profile.trips().destination(connectionId);

            List<Journey.Leg.IntermediateStop> intermediateStops = new ArrayList<>();

            int nextConnectionId = connectionId;
            for (int i = 0; i < stopsToRide; i++) {
                nextConnectionId = connections.nextConnectionId(nextConnectionId);

                //Stop connectionStop = new Stop(tt.platformName(nextConnectionId), tt.platformName(nextConnectionId), tt.stations().longitude(nextConnectionId), tt.stations().latitude(nextConnectionId));
                //Journey.Leg.IntermediateStop intStop = new Journey.Leg.IntermediateStop(connectionStop, )
            }



            int routeId = profile.trips().routeId(nextConnectionId);

            Vehicle vehicle = tt.routes().vehicle(routeId);

            Stop depStop = new Stop(tt.platformName(connectionId), tt.platformName(connectionId) , tt.stations().longitude(connectionId), tt.stations().latitude(connectionId));
            Stop arrStop = new Stop(tt.platformName(nextConnectionId), tt.platformName(nextConnectionId), tt.stations().longitude(nextConnectionId), tt.stations().latitude(nextConnectionId));

            Journey.Leg.Transport leg = new Journey.Leg.Transport(depStop, depMins, arrStop, arrMins, null, vehicle, destination);

            List<Journey.Leg> legs = new ArrayList<>();

            //legs.add(new Journey.Leg.Foot(de))
        });
    }
}
