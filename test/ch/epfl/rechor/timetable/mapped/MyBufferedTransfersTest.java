package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.PackedRange;
import org.junit.jupiter.api.Test;
import java.util.NoSuchElementException;


import java.nio.ByteBuffer;
import java.util.HexFormat;

import static org.junit.jupiter.api.Assertions.*;

public class MyBufferedTransfersTest {
    private static final HexFormat HEX_FORMAT = HexFormat.ofDelimiter(" ");

    private static final BufferedTransfers smallTransfers = new BufferedTransfers(getSmallTransfersByteBuffer());
    private static final BufferedTransfers largeTransfers = new BufferedTransfers(getLargeTransfersByteBuffer());
    private static final BufferedTransfers nonSequentialTransfers = new BufferedTransfers(getNonSequentialTransfersByteBuffer());

    public static ByteBuffer getSmallTransfersByteBuffer() {
        String hexTransfers =
                "00 01 00 02 05 "  // Transfer 0 → Station 1 → Station 2 (5 min)
                        + "00 03 00 04 10 "  // Transfer 1 → Station 3 → Station 4 (16 min)
                        + "00 05 00 06 02";  // Transfer 2 → Station 5 → Station 6 (2 min)

        byte[] bytes = HEX_FORMAT.parseHex(hexTransfers);
        return ByteBuffer.wrap(bytes);
    }

    public static ByteBuffer getLargeTransfersByteBuffer() {
        ByteBuffer buffer = ByteBuffer.allocate(100 * 5);
        for (int i = 0; i < 100; i++) {
            buffer.putShort((short) (i));     // DEP_STATION_ID (0 → 99)
            buffer.putShort((short) (i + 1)); // ARR_STATION_ID (1 → 100)
            buffer.put((byte) ((i % 99) + 1)); // TRANSFER_MINUTES (1 → 99)
        }
        return ByteBuffer.wrap(buffer.array());
    }

    public static ByteBuffer getNonSequentialTransfersByteBuffer() {
        String hexTransfers =
                "00 10 00 20 07 "  // Transfer 0 → Station 16 → Station 32 (7 min)
                        + "00 30 00 40 20 "  // Transfer 1 → Station 48 → Station 64 (32 min)
                        + "00 50 00 60 15";  // Transfer 2 → Station 80 → Station 96 (21 min)

        byte[] bytes = HEX_FORMAT.parseHex(hexTransfers);
        return ByteBuffer.wrap(bytes);
    }

    @Test
    void testSize() {
        assertEquals(3, smallTransfers.size());
        assertEquals(100, largeTransfers.size());
        assertEquals(3, nonSequentialTransfers.size());
    }

    @Test
    void testDepartureStationId() {
        assertEquals(1, smallTransfers.depStationId(0));
        assertEquals(3, smallTransfers.depStationId(1));
        assertEquals(5, smallTransfers.depStationId(2));
    }

    @Test
    void testTransferMinutes() {
        assertEquals(5, smallTransfers.minutes(0));
        assertEquals(16, smallTransfers.minutes(1));
        assertEquals(2, smallTransfers.minutes(2));
    }

    @Test
    void testArrivingAtValidStation() {
        int packed2 = smallTransfers.arrivingAt(2);
        int packed4 = smallTransfers.arrivingAt(4);
        int packed6 = smallTransfers.arrivingAt(6);

        assertEquals(PackedRange.pack(0, 1), packed2); // Only Transfer 0 arrives at station 2
        assertEquals(PackedRange.pack(1, 2), packed4); // Only Transfer 1 arrives at station 4
        assertEquals(PackedRange.pack(2, 3), packed6); // Only Transfer 2 arrives at station 6

        assertEquals(0, PackedRange.startInclusive(packed2));
        assertEquals(1, PackedRange.endExclusive(packed2));

        assertEquals(1, PackedRange.startInclusive(packed4));
        assertEquals(2, PackedRange.endExclusive(packed4));

        assertEquals(2, PackedRange.startInclusive(packed6));
        assertEquals(3, PackedRange.endExclusive(packed6));
    }

    @Test
    void testArrivingAtNonexistentStation() {
        assertThrows(IndexOutOfBoundsException.class, () -> smallTransfers.arrivingAt(10));
    }

    @Test
    void testMinutesBetweenValidTransfers() {
        assertEquals(5, smallTransfers.minutesBetween(1, 2));
        assertEquals(16, smallTransfers.minutesBetween(3, 4));
        assertEquals(2, smallTransfers.minutesBetween(5, 6));
    }

    @Test
    void testMinutesBetweenInvalidTransfer() {
        assertThrows(NoSuchElementException.class, () -> smallTransfers.minutesBetween(1, 3));
    }

    @Test
    void testPackedRange() {
        int packed = PackedRange.pack(5, 10);
        assertEquals(5, PackedRange.startInclusive(packed));
        assertEquals(10, PackedRange.endExclusive(packed));
        assertEquals(5, PackedRange.length(packed));

        int packedSingle = PackedRange.pack(2, 2); // Empty interval
        assertEquals(2, PackedRange.startInclusive(packedSingle));
        assertEquals(2, PackedRange.endExclusive(packedSingle));
        assertEquals(0, PackedRange.length(packedSingle));
    }

    @Test
    void testBoundaryCases() {
        assertThrows(IndexOutOfBoundsException.class, () -> smallTransfers.depStationId(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> smallTransfers.depStationId(3));

        assertThrows(IndexOutOfBoundsException.class, () -> smallTransfers.minutes(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> smallTransfers.minutes(3));
    }

    @Test
    void testPerformanceLargeDataset() {
        long startTime = System.nanoTime();
        for (int i = 0; i < largeTransfers.size(); i++) {
            largeTransfers.depStationId(i);
            largeTransfers.minutes(i);
        }
        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1_000_000; //converting to milliseconds
        assertTrue(duration < 500, "Large dataset took too long: " + duration + "ms");
    }

    @Test
    void testNoArrivals() {
        assertThrows(NoSuchElementException.class, () -> smallTransfers.arrivingAt(3)); //no transfers arrive at station 3
        assertThrows(IndexOutOfBoundsException.class, () -> smallTransfers.arrivingAt(99)); //id 99 is larger than the biggest station id
    }

    @Test
    void testTransfersToSameStation() {
        ByteBuffer buffer = ByteBuffer.wrap(new byte[]{
                0x00, 0x05, 0x00, 0x05, 0x0A // Transfer: Station 5 → Station 5 (10 min)
        });

        BufferedTransfers selfTransfer = new BufferedTransfers(buffer);

        int packed = selfTransfer.arrivingAt(5);
        assertEquals(PackedRange.pack(0, 1), packed);

        assertEquals(10, selfTransfer.minutesBetween(5, 5));
    }
}
