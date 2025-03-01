package ch.epfl.rechor.timetable;

import ch.epfl.rechor.journey.Vehicle;

/**
 * Represents indexed public transport routes.
 *
 * @author Tom Beaugé
 */
public interface Routes extends Indexed {

    /**
     * Returns the type of vehicle serving the given route index.
     *
     * @param id the index of the route
     * @return the vehicle type
     * @throws IndexOutOfBoundsException if id is not in range [0, size())
     */
    Vehicle vehicle(int id);

    /**
     * Returns the name of the given route index.
     *
     * @param id the index of the route
     * @return the name of the route (e.g., "IR 15")
     * @throws IndexOutOfBoundsException if id is not in range [0, size())
     */
    String name(int id);
}
