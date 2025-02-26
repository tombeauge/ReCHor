// File: PackedCriteriaTest.java
package ch.epfl.rechor.journey;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class MyPackedCriteriaTest {

    @Test
    void pack_ValidInputs_ReturnsPackedValue() {
        long packed = PackedCriteria.pack(500, 10, 12345);
        assertEquals(500, PackedCriteria.arrMins(packed));
        assertEquals(10, PackedCriteria.changes(packed));
        assertEquals(12345, PackedCriteria.payload(packed));
    }

    @Test
    void pack_InvalidArrivalTime_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> PackedCriteria.pack(-241, 5, 100));
        assertThrows(IllegalArgumentException.class, () -> PackedCriteria.pack(2880, 5, 100));
    }

    @Test
    void pack_TooManyChanges_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> PackedCriteria.pack(500, 128, 100));
    }

    @Test
    void hasDepMins_CorrectlyIdentifiesPresence() {
        long packedWithDep = PackedCriteria.withDepMins(PackedCriteria.pack(600, 5, 300), 400);
        long packedWithoutDep = PackedCriteria.withoutDepMins(packedWithDep);

        assertTrue(PackedCriteria.hasDepMins(packedWithDep));
        assertFalse(PackedCriteria.hasDepMins(packedWithoutDep));
    }

    @Test
    void depMins_ThrowsWhenNoDepartureTime() {
        long packed = PackedCriteria.pack(500, 3, 42);
        assertThrows(IllegalArgumentException.class, () -> PackedCriteria.depMins(packed));
    }

    @Test
    void depMins_ReturnsCorrectValue() {
        long packed = PackedCriteria.withDepMins(PackedCriteria.pack(700, 4, 500), 300);
        assertEquals(300, PackedCriteria.depMins(packed));
    }

    @Test
    void arrMins_ReturnsCorrectValue() {
        long packed = PackedCriteria.pack(750, 2, 999);
        assertEquals(750, PackedCriteria.arrMins(packed));
    }

    @Test
    void changes_ReturnsCorrectValue() {
        long packed = PackedCriteria.pack(900, 7, 50);
        assertEquals(7, PackedCriteria.changes(packed));
    }

    @Test
    void payload_ReturnsCorrectValue() {
        long packed = PackedCriteria.pack(1000, 1, 5555);
        assertEquals(5555, PackedCriteria.payload(packed));
    }

    @Test
    void dominatesOrIsEqual_ValidComparisons() {
        long packed1 = PackedCriteria.pack(800, 2, 100);
        long packed2 = PackedCriteria.pack(900, 3, 150);

        assertTrue(PackedCriteria.dominatesOrIsEqual(packed1, packed2)); // Earlier arrival, fewer changes
        assertFalse(PackedCriteria.dominatesOrIsEqual(packed2, packed1));
    }

    @Test
    void dominatesOrIsEqual_MismatchedDeparture_ThrowsException() {
        long packed1 = PackedCriteria.withDepMins(PackedCriteria.pack(1000, 3, 200), 500);
        long packed2 = PackedCriteria.pack(1000, 3, 200);

        assertThrows(IllegalArgumentException.class, () -> PackedCriteria.dominatesOrIsEqual(packed1, packed2));
    }

    @Test
    void withoutDepMins_RemovesDepartureTime() {
        long packedWithDep = PackedCriteria.withDepMins(PackedCriteria.pack(600, 5, 100), 300);
        long packedWithoutDep = PackedCriteria.withoutDepMins(packedWithDep);

        assertFalse(PackedCriteria.hasDepMins(packedWithoutDep));
    }

    @Test
    void withDepMins_AddsDepartureTime() {
        long packed = PackedCriteria.pack(700, 4, 800);
        long packedWithDep = PackedCriteria.withDepMins(packed, 350);

        assertEquals(350, PackedCriteria.depMins(packedWithDep));
    }

    @Test
    void withAdditionalChange_IncreasesChangeCount() {
        long packed = PackedCriteria.pack(1100, 2, 900);
        long packedUpdated = PackedCriteria.withAdditionalChange(packed);

        assertEquals(3, PackedCriteria.changes(packedUpdated));
    }

    @Test
    void withPayload_UpdatesPayloadCorrectly() {
        long packed = PackedCriteria.pack(1200, 1, 555);
        long updated = PackedCriteria.withPayload(packed, 9999);

        assertEquals(9999, PackedCriteria.payload(updated));
    }
}
