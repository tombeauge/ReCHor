package ch.epfl.rechor.journey;

import java.util.ArrayList;
import java.util.List;

/**
 * Enumeration of types of vehicles
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

