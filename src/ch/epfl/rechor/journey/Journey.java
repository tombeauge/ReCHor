package ch.epfl.rechor.journey;

import ch.epfl.rechor.Preconditions;

import java.awt.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Represents a journey consisting of multiple legs.
 * A journey must contain at least one leg and ensures the following constraints:
 * Legs must be non-null.
 * Each leg must start at or after the arrival time of the previous leg.
 * The departure stop of a leg must match the arrival stop of the previous leg.
 * Legs must alternate between foot and transport.
 *
 * @author Tom Beauge
 * @param legs
 */
public final record Journey(List<Leg> legs) {
    /**
     * @throws IllegalArgumentException if the preconditions mentioned above are not met
     * @throws NullPointerException if current is null
     */
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

    /**
     * @return the starting stop of the trip, i.e. that of its first stage
     */
    public Stop depStop() {
        return legs.getFirst().depStop();
    }

    /**
     * @return the arrival stop of the trip, i.e. that of its last stage
     */
    public Stop arrStop() {
        return legs.getLast().arrStop();
    }

    /**
     * @return the start date/time of the trip, i.e. that of its first stage
     */

    public LocalDateTime depTime() {
        return legs.getFirst().depTime();
    }

    /**
     * @return the end date/time of the trip, i.e. that of its last stage
     */
    public LocalDateTime arrTime() {
        return legs.getLast().arrTime();
    }

    /**
     * @return the total duration of the trip, i.e. that separating the end date/time from the start date/time
     */
    public Duration duration() {
        return Duration.between(depTime(), arrTime());
    }

    /**
     * Interface representing one leg(portion) of a journey
     */
    public sealed interface Leg {
        /**
         * Intermediate stops are stops found in the middle of the start and end stops.
         * Makes sure parameters are not null and departure time is before arrival time
         * @param stop
         * @param arrTime
         * @param depTime
         */
        record IntermediateStop(Stop stop, LocalDateTime arrTime, LocalDateTime depTime){
            public IntermediateStop {

                Objects.requireNonNull(stop);
                Objects.requireNonNull(arrTime);
                Objects.requireNonNull(depTime);
                //checks arrival time does not occur before departure time
                Preconditions.checkArgument(!depTime.isBefore(arrTime));
            }
        }

        /**
         * Represents travel done by using public transport.
         * Ensures parameters are not null and creates a copy of the intermediate stops to make sure immutable.
         * @param depStop
         * @param depTime
         * @param arrStop
         * @param arrTime
         * @param intermediateStops
         * @param vehicle
         * @param route
         * @param destination
         */
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

        /**
         * Represents the travel done by walking.
         * Ensures preconditions are met.
         * @param depStop
         * @param depTime
         * @param arrStop
         * @param arrTime
         */
        record Foot(Stop depStop, LocalDateTime depTime, Stop arrStop, LocalDateTime arrTime) implements Leg {
            public Foot {
                Objects.requireNonNull(depStop);
                Objects.requireNonNull(arrTime);
                Objects.requireNonNull(arrStop);
                Objects.requireNonNull(depTime);

                //checks arrival time does not occur before departure time
                Preconditions.checkArgument(!arrTime.isBefore(depTime));
            }

            /**
             * Overrides intermediate stops as walking does not contain any stops.
             * @return empty list of intermediate stops.
             */
            @Override
            public List<IntermediateStop> intermediateStops() {
                return List.of();
            }

            /**
             * @return true iff the step is a change within the same station. So if the name of the departure stop is identical to that of the arrival stop
             */
            public boolean isTransfer() {
                return (depStop().name().equals(arrStop().name()));
            }
        }

        /**
         * @return The departure stop of this leg.
         */
        Stop depStop();

        /**
         * @return The arrival stop of this leg.
         */
        Stop arrStop();

        /**
         * @return The departure time of this leg.
         */
        LocalDateTime depTime();

        /**
         * @return The arrival time of this leg.
         */
        LocalDateTime arrTime();

        /**
         * @return A list of intermediate stops along this leg.
         */
        List<IntermediateStop> intermediateStops();

        /**
         * @return The duration of this leg.
         */
        default Duration duration() {
            return Duration.between(depTime(), arrTime());
        }



    }
}
