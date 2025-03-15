package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.timetable.Transfers;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.NoSuchElementException;

/**
 * {@code BufferedTransfers} implements {@link Transfers} and provides efficient access
 * to a flattened table of station-to-station transfers.
 *
 * <p>Each transfer consists of:
 * <ul>
 *   <li>A departure station ID (U16)</li>
 *   <li>An arrival station ID (U16)</li>
 *   <li>A transfer duration in minutes (U8)</li>
 * </ul>
 *
 * <p>An auxiliary table allows quick lookup of transfers arriving at a station.</p>
 *
 * @author Tom Beaugé
 */
public final class BufferedTransfers implements Transfers {

    private static final int DEP_STATION_ID = 0;
    private static final int ARR_STATION_ID = 1;
    private static final int TRANSFER_MINUTES = 2;

    private final int[] arrivalTable;
    private final StructuredBuffer transfersStructuredBuffer;
    private final Structure transfersStructure;

    /**
     * Constructs a {@code BufferedTransfers} instance.
     *
     * @param buffer the buffer containing flattened transfer data
     */
    public BufferedTransfers(ByteBuffer buffer) {
        transfersStructure = new Structure(
                Structure.field(DEP_STATION_ID, Structure.FieldType.U16),
                Structure.field(ARR_STATION_ID, Structure.FieldType.U16),
                Structure.field(TRANSFER_MINUTES, Structure.FieldType.U8)
        );

        transfersStructuredBuffer = new StructuredBuffer(transfersStructure, buffer);
        arrivalTable = precomputeArrivalTable();
    }

    /**
     * Returns the departure station index for a given transfer.
     *
     * @param id the transfer index
     * @return the departure station ID
     * @throws IndexOutOfBoundsException if the transfer index is out of range
     */
    @Override
    public int depStationId(int id) {
        checkTransferId(id);
        return transfersStructuredBuffer.getU16(DEP_STATION_ID, id);
    }

    /**
     * Returns the transfer duration in minutes.
     *
     * @param id the transfer index
     * @return the duration in minutes
     * @throws IndexOutOfBoundsException if the transfer index is out of range
     */
    @Override
    public int minutes(int id) {
        checkTransferId(id);
        return transfersStructuredBuffer.getU8(TRANSFER_MINUTES, id);
    }

    /**
     * Returns the packed interval of transfer indices arriving at a station.
     *
     * @param stationId the station index
     * @return the packed interval of transfer indices
     * @throws IndexOutOfBoundsException if the station index is invalid
     */
    @Override
    public int arrivingAt(int stationId) {
        if (stationId < 0 || stationId >= arrivalTable.length) {
            throw new IndexOutOfBoundsException("Invalid station ID: " + stationId);
        }
        return arrivalTable[stationId];
    }

    /**
     * Returns the transfer duration in minutes between two stations.
     *
     * @param depStationId the departure station index
     * @param arrStationId the arrival station index
     * @return the transfer duration in minutes
     * @throws IndexOutOfBoundsException if either station index is invalid
     * @throws NoSuchElementException if no transfer exists between the given stations
     */
    @Override
    public int minutesBetween(int depStationId, int arrStationId) {
        checkTransferId(depStationId);
        checkTransferId(arrStationId);

        for (int i = 0; i < size(); i++) {
            int currArrivalStation = transfersStructuredBuffer.getU16(ARR_STATION_ID, i);
            if (depStationId(i) == depStationId && currArrivalStation == arrStationId) {
                return minutes(i);
            }
        }

        throw new NoSuchElementException("No transfer exists between the given stations");
    }

    /**
     * Returns the total number of transfers.
     *
     * @return the number of transfers
     */
    @Override
    public int size() {
        return transfersStructuredBuffer.size();
    }

    /**
     * Precomputes an arrival table mapping each station to its first arriving transfer.
     *
     * @return an array mapping stations to transfer indices
     */
    private int[] precomputeArrivalTable() {
        int maxStationId = 0;

        for (int i = 0; i < size(); i++) {
            int stationId = transfersStructuredBuffer.getU16(ARR_STATION_ID, i);
            if (stationId > maxStationId) {
                maxStationId = stationId;
            }
        }

        int[] arrivalTable = new int[maxStationId + 1];

        //initialising all values to -1 by default to indicate no transfers
        Arrays.fill(arrivalTable, -1);

        for (int i = 0; i < size(); i++) {
            int stationId = transfersStructuredBuffer.getU16(ARR_STATION_ID, i);
            arrivalTable[stationId] = i;
        }

        return arrivalTable;
    }

    /**
     * Ensures that a transfer index is within valid bounds.
     *
     * @param id the transfer index
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    private void checkTransferId(int id) {
        if (id < 0 || id >= size()) {
            throw new IndexOutOfBoundsException("Invalid transfer index: " + id);
        }
    }
}
