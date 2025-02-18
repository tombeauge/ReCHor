package ch.epfl.rechor.journey;

import ch.epfl.rechor.Preconditions;

import java.awt.*;
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
        for (Leg leg : legs) {
            Preconditions.checkArgument(leg != null);

        }

    }

    public interface Leg {
        public record IntermediateStop(){}

        public record Transport(){}

        public record Foot(){}
    }
}
