package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.Bits32_24_8;
import ch.epfl.rechor.timetable.Connections;

import java.nio.ByteBuffer;

/**
 * {@code BufferedConnections} provides access to a flattened table of public transport connections.
 * Each connection consists of a departure stop and time, an arrival stop and time, and an encoded trip ID and position.
 * The class also supports retrieving the next connection in the same trip efficiently.
 *
 * @author Tom Beaugé
 */
public final class BufferedConnections implements Connections {

    private final StructuredBuffer connectionsStructuredBuffer;
    private final Structure connectionsStructure;
    private final ByteBuffer succBuffer;

    private static final int DEP_STOP_ID = 0;
    private static final int DEP_MINUTES = 1;
    private static final int ARR_STOP_ID = 2;
    private static final int ARR_MINUTES = 3;
    private static final int TRIP_POS_ID = 4;

    /**
     * Constructs a {@code BufferedConnections} instance.
     *
     * @param buffer     the buffer containing structured connection data
     * @param succBuffer the buffer storing the indices of next connections in the same trip
     */
    public BufferedConnections(ByteBuffer buffer, ByteBuffer succBuffer) {
        connectionsStructure = new Structure(
                Structure.field(DEP_STOP_ID, Structure.FieldType.U16),
                Structure.field(DEP_MINUTES, Structure.FieldType.U16),
                Structure.field(ARR_STOP_ID, Structure.FieldType.U16),
                Structure.field(ARR_MINUTES, Structure.FieldType.U16),
                Structure.field(TRIP_POS_ID, Structure.FieldType.S32)
        );

        connectionsStructuredBuffer = new StructuredBuffer(connectionsStructure, buffer);
        this.succBuffer = succBuffer;
    }

    /**
     * Returns the departure stop ID for the given connection.
     *
     * @param id the connection ID
     * @return the departure stop ID
     */
    @Override
    public int depStopId(int id) {
        return connectionsStructuredBuffer.getU16(DEP_STOP_ID, id);
    }

    /**
     * Returns the departure time in minutes after midnight.
     *
     * @param id the connection ID
     * @return the departure time in minutes
     */
    @Override
    public int depMins(int id) {
        return connectionsStructuredBuffer.getU16(DEP_MINUTES, id);
    }

    /**
     * Returns the arrival stop ID for the given connection.
     *
     * @param id the connection ID
     * @return the arrival stop ID
     */
    @Override
    public int arrStopId(int id) {
        return connectionsStructuredBuffer.getU16(ARR_STOP_ID, id);
    }

    /**
     * Returns the arrival time in minutes after midnight.
     *
     * @param id the connection ID
     * @return the arrival time in minutes
     */
    @Override
    public int arrMins(int id) {
        return connectionsStructuredBuffer.getU16(ARR_MINUTES, id);
    }

    /**
     * Returns the trip ID associated with the given connection.
     *
     * @param id the connection ID
     * @return the trip ID
     */
    @Override
    public int tripId(int id) {
        return Bits32_24_8.unpack24(connectionsStructuredBuffer.getS32(TRIP_POS_ID, id));
    }

    /**
     * Returns the position of this connection within its trip.
     *
     * @param id the connection ID
     * @return the trip position
     */
    @Override
    public int tripPos(int id) {
        return Bits32_24_8.unpack8(connectionsStructuredBuffer.getS32(TRIP_POS_ID, id));
    }

    /**
     * Returns the ID of the next connection in the same trip, or -1 if none exists.
     *
     * @param id the connection ID
     * @return the next connection ID, or -1 if this is the last connection
     */
    @Override
    public int nextConnectionId(int id) {
        return succBuffer.get(id);
    }

    /**
     * Returns the total number of connections stored in the buffer.
     *
     * @return the number of connections
     */
    @Override
    public int size() {
        return connectionsStructuredBuffer.size();
    }
}
