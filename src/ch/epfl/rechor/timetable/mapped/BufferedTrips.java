package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.timetable.Trips;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * {@code BufferedTrips} is a final class that implements the {@link Trips} interface,
 * providing access to a table of trips stored in a flattened format.
 *
 * <p>Each trip consists of:
 * <ul>
 *   <li>A route ID (U16) referencing a route in the routes table.</li>
 *   <li>A destination ID (U16) referencing a string in the string table.</li>
 * </ul>
 *
 * @author Tom Beaugé
 */
public final class BufferedTrips implements Trips {

    private final List<String> stringTable;
    private final StructuredBuffer tripsStructuredBuffer;
    private final Structure tripsStructure;

    private static final int ROUTE_ID = 0;
    private static final int DESTINATION_ID = 1;

    /**
     * Constructs a {@code BufferedTrips} instance.
     *
     * @param stringTable the table of strings containing destination names
     * @param buffer      the buffer storing the flattened trip data
     */
    public BufferedTrips(List<String> stringTable, ByteBuffer buffer) {
        this.stringTable = stringTable;

        tripsStructure = new Structure(
                Structure.field(ROUTE_ID, Structure.FieldType.U16),
                Structure.field(DESTINATION_ID, Structure.FieldType.U16)
        );
        tripsStructuredBuffer = new StructuredBuffer(tripsStructure, buffer);
    }

    /**
     * Returns the route ID associated with the given trip.
     *
     * @param id the trip ID
     * @return the route ID for the trip
     */
    @Override
    public int routeId(int id) {
        return tripsStructuredBuffer.getU16(ROUTE_ID, id);
    }

    /**
     * Returns the destination name associated with the given trip.
     *
     * @param id the trip ID
     * @return the destination name
     */
    @Override
    public String destination(int id) {
        return stringTable.get(tripsStructuredBuffer.getU16(DESTINATION_ID, id));
    }

    /**
     * Returns the total number of trips stored in the buffer.
     *
     * @return the number of trips
     */
    @Override
    public int size() {
        return tripsStructuredBuffer.size();
    }
}
