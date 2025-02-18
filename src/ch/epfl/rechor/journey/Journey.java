package ch.epfl.rechor.journey;

import ch.epfl.rechor.Preconditions;

import java.awt.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public final record Journey(List<Leg> legs) {
    public Journey{
        Preconditions.checkArgument(!legs.isEmpty());

        legs = List.copyOf(legs);
        for (int i = 0; i < legs.size(); i++) {

            //makes sure entries are non-null
            Leg current = legs.get(i);
            Objects.requireNonNull(current);

             if (i > 0) {
                 Leg previous = legs.get(i - 1);


                 //makes sure the current leg does not begin before the arrival time of the previous leg
                 Preconditions.checkArgument(!current.depTime().isBefore(previous.arrTime()));

                 //makes sure the next leg departs from the same spot as where the previous leg left us
                 Preconditions.checkArgument(current.depStop().equals(previous.arrStop()));

                 //makes sure alternates between foot and transport
                 boolean bothFoot = (current instanceof  Leg.Foot) && (previous instanceof Leg.Foot);
                 boolean bothTransport = (current instanceof Leg.Transport) && (previous instanceof Leg.Transport);

                 Preconditions.checkArgument(!bothFoot && !bothTransport);
             }
        }

    }

    public Stop depStop() {
        return legs.getFirst().depStop();
    }

    public Stop arrStop() {
        return legs.getLast().arrStop();
    }

    public LocalDateTime depTime() {
        return legs.getFirst().depTime();
    }

    public LocalDateTime arrTime() {
        return legs.getLast().arrTime();
    }

    public Duration duration() {
        return Duration.between(depTime(), arrTime());
    }

    public sealed interface Leg {
       record IntermediateStop(Stop stop, LocalDateTime arrTime, LocalDateTime depTime){
            public IntermediateStop {

                Objects.requireNonNull(stop);
                Objects.requireNonNull(arrTime);
                Objects.requireNonNull(depTime);
                //checks arrival time does not occur before departure time
                Preconditions.checkArgument(!depTime.isBefore(arrTime));
            }
        }

        record Transport(Stop depStop, LocalDateTime depTime, Stop arrStop, LocalDateTime arrTime, List<IntermediateStop> intermediateStops, Vehicle vehicle, String route, String destination) implements Leg {
            public Transport {
                Objects.requireNonNull(depStop);
                Objects.requireNonNull(arrTime);
                Objects.requireNonNull(arrStop);
                Objects.requireNonNull(depTime);
                Objects.requireNonNull(vehicle);
                Objects.requireNonNull(route);
                Objects.requireNonNull(destination);

                //checks arrival time does not occur before departure time
                Preconditions.checkArgument(!arrTime.isBefore(depTime));


                intermediateStops = List.copyOf(intermediateStops);
            }

            @Override
            public Stop depStop() {
                return depStop;
            }
        }



        record Foot(Stop depStop, LocalDateTime depTime, Stop arrStop, LocalDateTime arrTime) implements Leg {
            public Foot {
                Objects.requireNonNull(depStop);
                Objects.requireNonNull(arrTime);
                Objects.requireNonNull(arrStop);
                Objects.requireNonNull(depTime);

                //checks arrival time does not occur before departure time
                Preconditions.checkArgument(!arrTime.isBefore(depTime));
            }

            @Override
            public List<IntermediateStop> intermediateStops() {
                //returns empty list since destinations on foot have no intermediate stops
                return List.of();
            }
            
            public boolean isTransfer() {
                return (depStop().name().equals(arrStop().name()));
            }
        }

        Stop depStop();

        Stop arrStop();

        LocalDateTime depTime();

        LocalDateTime arrTime();
        
        List<IntermediateStop> intermediateStops();

        default Duration duration() {
            return Duration.between(depTime(), arrTime());
        }



    }
}
