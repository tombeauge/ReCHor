package ch.epfl.rechor.journey;

import java.util.ArrayList;
import java.util.List;


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

