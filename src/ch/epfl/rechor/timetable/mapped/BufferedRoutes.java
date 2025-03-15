package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.journey.Vehicle;
import ch.epfl.rechor.timetable.Routes;

import java.nio.ByteBuffer;
import java.util.List;

import static ch.epfl.rechor.timetable.mapped.Structure.FieldType.U16;
import static ch.epfl.rechor.timetable.mapped.Structure.FieldType.U8;
import static ch.epfl.rechor.timetable.mapped.Structure.field;

/**
 * {@code BufferedRoutes} is a final class that implements the {@code Routes} interface,
 * providing access to a table of routes stored in a flattened format.
 * It allows retrieving the name and vehicle type of each route.
 *
 * @author Tom Beaugé
 */
public final class BufferedRoutes implements Routes {
    private final List<String> stringTable;
    private final ByteBuffer buffer;
    private final StructuredBuffer routeStructuredBuffer;
    private final Structure routeStructure;

    private final Vehicle[] vehicles = Vehicle.values();

    private static final int NAME_ID = 0;
    private static final int KIND = 1;

    /**
     * Constructs a BufferedRoutes instance.
     *
     * @param stringTable the table of strings containing route names
     * @param buffer      the buffer storing the flattened route data
     */
    public BufferedRoutes(List<String> stringTable, ByteBuffer buffer) {
        this.stringTable = stringTable;
        this.buffer = buffer;

        routeStructure = new Structure(field(NAME_ID, U16), field(KIND, U8));
        routeStructuredBuffer = new StructuredBuffer(routeStructure, buffer);
    }

    /**
     * Returns the vehicle corresponding to the given route ID.
     *
     * @param id the route ID
     * @return the vehicle type associated with the route
     * @throws IndexOutOfBoundsException if the ID is invalid
     */
    @Override
    public Vehicle vehicle(int id) {
        int vehicleId = routeStructuredBuffer.getU8(KIND, id);
        return vehicles[vehicleId];
    }

    /**
     * Returns the name of the route corresponding to the given ID.
     *
     * @param id the route ID
     * @return the name of the route
     * @throws IndexOutOfBoundsException if the ID is invalid
     */
    @Override
    public String name(int id) {
        int routeId = routeStructuredBuffer.getU16(NAME_ID, id);
        return stringTable.get(routeId);
    }

    /**
     * Returns the total number of routes stored in the buffer.
     *
     * @return the number of routes
     */
    @Override
    public int size() {
        return routeStructuredBuffer.size();
    }
}
