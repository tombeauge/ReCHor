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
        /**
         * TODO
         * Journey possède un constructeur compact qui valide la liste des étapes reçue en vérifiant que:
         *
         * elle n'est pas vide,
         * les étapes à pied alternent avec celles en transport,
         * pour toutes les étapes sauf la première, l'instant de départ ne précède pas celui d'arrivée de la précédente,
         * pour toutes les étapes sauf la première, l'arrêt de départ est identique à l'arrêt d'arrivée de la précédente.
         */

        legs = List.copyOf(legs);
        for (Leg leg : legs) {
            Preconditions.checkArgument(leg != null);

        }

    }

    public interface Leg {
        public record IntermediateStop(Stop stop, LocalDateTime arrTime, LocalDateTime depTime){
            public IntermediateStop {
                Objects.requireNonNull(stop);
                Preconditions.checkArgument(depTime.isBefore(arrTime()) || depTime.equals(arrTime()));
            }
        }

        public record Transport(Stop stop, LocalDateTime arrTime, Stop arrStop, LocalDateTime depTime, List<IntermediateStop> intermediateStops, Vehicle vehicle, String route, String destination){
            public Transport {
                /**
                 * TODO
                 * Le constructeur compact de Transport valide les arguments en vérifiant que
                 *
                 * aucun d'entre eux n'est null,
                 * la date/heure d'arrivée n'est pas antérieure à celle de départ.
                 *
                 * Notez bien que des appels à requireNonNull ne sont nécessaires dans le constructeur que pour les arguments qui ne sont pas manipulés d'une manière ou d'une autre avant d'être stockés dans l'enregistrement. Par exemple, il n'est pas nécessaire d'utiliser requireNonNull pour intermediateStops puisque cet argument est passé à copyOf, qui lèvera elle-même une exception si cet argument est null.
                 */
            }
        }



        public record Foot(){}

        abstract Stop depStop();

        abstract Stop arrStop();

        abstract LocalDateTime depTime();

        abstract LocalDateTime arrTime();

        //TODO return etape duration
        default Duration duration();



    }
}
