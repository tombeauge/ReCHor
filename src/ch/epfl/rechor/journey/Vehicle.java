package ch.epfl.rechor.journey;

import java.util.ArrayList;
import java.util.List;

/**
 * Enumeration of types of vehicles
 * @author Cem Celik (399448)
 *
 */
public enum Vehicle {
    TRAM,
    METRO,
    TRAIN,
    BUS,
    FERRY,
    AERIAL_LIFT,
    FUNICULAR;

    public final static List<Vehicle> ALL = List.of(Vehicle.values());
}

