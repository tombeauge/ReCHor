// File: PackedCriteriaTest.java
package ch.epfl.rechor.journey;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Test class for the PackedCriteria utility.
 * Assumes that PackedCriteria is in the journey package and that its methods
 * behave according to the project specification.
 */
public class MyPackedCriteriaTest {

    @Test
    public void testPackValidCriteriaWithoutDep() {
        int arrMins = 1000; // valid: between -240 (inclusive) and 2880 (exclusive)
        int changes = 1;    // fits in 7 bits (< 128)
        int payload = 42;
        long criteria = PackedCriteria.pack(arrMins, changes, payload);
        // Without departure minute, hasDepMins should be false.
        assertFalse(PackedCriteria.hasDepMins(criteria));
        assertEquals(arrMins, PackedCriteria.arrMins(criteria));
        assertEquals(changes, PackedCriteria.changes(criteria));
        assertEquals(payload, PackedCriteria.payload(criteria));
    }

    @Test
    public void testPackInvalidArrMins() {
        int changes = 0;
        int payload = 0;
        // arrMins below valid range should throw exception.
        assertThrows(IllegalArgumentException.class, () ->
                PackedCriteria.pack(-241, changes, payload)
        );
        // arrMins at or above 2880 should throw exception.
        assertThrows(IllegalArgumentException.class, () ->
                PackedCriteria.pack(2880, changes, payload)
        );
    }

    @Test
    public void testPackInvalidChanges() {
        int arrMins = 1000;
        int payload = 0;
        // changes that do not fit in 7 bits (>=128) should throw exception.
        assertThrows(IllegalArgumentException.class, () ->
                PackedCriteria.pack(arrMins, 128, payload)
        );
    }

    @Test
    public void testWithDepMins() {
        int arrMins = 1200;
        int changes = 2;
        int payload = 99;
        long criteria = PackedCriteria.pack(arrMins, changes, payload);
        // Initially, criteria does not include a departure minute.
        assertFalse(PackedCriteria.hasDepMins(criteria));

        int dep = 720;
        long criteriaWithDep = PackedCriteria.withDepMins(criteria, dep);
        assertTrue(PackedCriteria.hasDepMins(criteriaWithDep));
        // Verify that the departure minute is correctly added.
        assertEquals(dep, PackedCriteria.depMins(criteriaWithDep));
        // Other fields should remain unchanged.
        assertEquals(arrMins, PackedCriteria.arrMins(criteriaWithDep));
        assertEquals(changes, PackedCriteria.changes(criteriaWithDep));
        assertEquals(payload, PackedCriteria.payload(criteriaWithDep));
    }

    @Test
    public void testDepMinsThrowsWhenNoDep() {
        int arrMins = 1000;
        int changes = 1;
        int payload = 50;
        long criteria = PackedCriteria.pack(arrMins, changes, payload);
        // Attempting to retrieve departure minute when none is set should throw.
        assertThrows(IllegalArgumentException.class, () ->
                PackedCriteria.depMins(criteria)
        );
    }

    @Test
    public void testWithoutDepMins() {
        int arrMins = 1300;
        int changes = 3;
        int payload = 77;
        long criteria = PackedCriteria.pack(arrMins, changes, payload);
        int dep = 800;
        long criteriaWithDep = PackedCriteria.withDepMins(criteria, dep);
        assertTrue(PackedCriteria.hasDepMins(criteriaWithDep));

        long criteriaWithoutDep = PackedCriteria.withoutDepMins(criteriaWithDep);
        // Now the criteria should no longer include a departure minute.
        assertFalse(PackedCriteria.hasDepMins(criteriaWithoutDep));
        // Other values remain unchanged.
        assertEquals(arrMins, PackedCriteria.arrMins(criteriaWithoutDep));
        assertEquals(changes, PackedCriteria.changes(criteriaWithoutDep));
        assertEquals(payload, PackedCriteria.payload(criteriaWithoutDep));
        // Calling depMins now should throw an exception.
        assertThrows(IllegalArgumentException.class, () ->
                PackedCriteria.depMins(criteriaWithoutDep)
        );
    }

    @Test
    public void testWithAdditionalChange() {
        int arrMins = 1100;
        int changes = 2;
        int payload = 10;
        long criteria = PackedCriteria.pack(arrMins, changes, payload);
        long updatedCriteria = PackedCriteria.withAdditionalChange(criteria);
        // The number of changes should be incremented.
        assertEquals(changes + 1, PackedCriteria.changes(updatedCriteria));
        // Other fields should be unchanged.
        assertEquals(arrMins, PackedCriteria.arrMins(updatedCriteria));
        assertEquals(payload, PackedCriteria.payload(updatedCriteria));
        assertEquals(PackedCriteria.hasDepMins(criteria), PackedCriteria.hasDepMins(updatedCriteria));
    }

    @Test
    public void testWithPayload() {
        int arrMins = 900;
        int changes = 1;
        int payload = 123;
        long criteria = PackedCriteria.pack(arrMins, changes, payload);
        int newPayload = 456;
        long updatedCriteria = PackedCriteria.withPayload(criteria, newPayload);
        assertEquals(newPayload, PackedCriteria.payload(updatedCriteria));
        // Ensure other components remain unchanged.
        assertEquals(arrMins, PackedCriteria.arrMins(criteria));
        assertEquals(arrMins, PackedCriteria.arrMins(updatedCriteria));
        assertEquals(changes, PackedCriteria.changes(updatedCriteria));
        assertEquals(PackedCriteria.hasDepMins(criteria), PackedCriteria.hasDepMins(updatedCriteria));
    }

    @Test
    public void testDominatesOrIsEqualWithoutDep() {
        // Create two criteria without departure minutes.
        int arrMins1 = 1000;
        int changes1 = 1;
        int payload1 = 10;
        long crit1 = PackedCriteria.pack(arrMins1, changes1, payload1);

        int arrMins2 = 1100;
        int changes2 = 2;
        int payload2 = 20;
        long crit2 = PackedCriteria.pack(arrMins2, changes2, payload2);

        // Since crit1 has an earlier arrival (smaller value) and fewer changes, it should dominate or be equal.
        assertTrue(PackedCriteria.dominatesOrIsEqual(crit1, crit2));
        // The reverse should not hold.
        assertFalse(PackedCriteria.dominatesOrIsEqual(crit2, crit1));
        // If two criteria are identical, domination holds.
        long critEqual = PackedCriteria.pack(arrMins1, changes1, payload1);
        assertTrue(PackedCriteria.dominatesOrIsEqual(crit1, critEqual));
    }

    @Test
    public void testDominatesOrIsEqualWithMismatchDep() {
        int arrMins = 1000;
        int changes = 1;
        int payload = 0;
        long critWithoutDep = PackedCriteria.pack(arrMins, changes, payload);
        long critWithDep = PackedCriteria.withDepMins(critWithoutDep, 700);
        // Comparing criteria when one includes a departure minute and the other does not should throw.
        assertThrows(IllegalArgumentException.class, () ->
                PackedCriteria.dominatesOrIsEqual(critWithoutDep, critWithDep)
        );
        assertThrows(IllegalArgumentException.class, () ->
                PackedCriteria.dominatesOrIsEqual(critWithDep, critWithoutDep)
        );
    }

    @Test
    public void testPackUnpackFullCriteria() {
        int depMins = 600;  // 10:00 AM
        int arrMins = 900;  // 3:00 PM
        int changes = 3;
        int payload = 99;

        // Pack criteria including departure minutes
        long packed = PackedCriteria.withDepMins(PackedCriteria.pack(arrMins, changes, payload), depMins);

        // Ensure all unpacked values match the original ones
        assertTrue(PackedCriteria.hasDepMins(packed), "Packed criteria should have departure minutes");
        assertEquals(depMins, PackedCriteria.depMins(packed), "Unpacked departure minutes should match");
        assertEquals(arrMins, PackedCriteria.arrMins(packed), "Unpacked arrival minutes should match");
        assertEquals(changes, PackedCriteria.changes(packed), "Unpacked changes should match");
        assertEquals(payload, PackedCriteria.payload(packed), "Unpacked payload should match");
    }

    @Test
    public void testPackUnpackWithoutDepMins() {
        int arrMins = 1100;  // 6:20 PM
        int changes = 2;
        int payload = 50;

        // Pack criteria without departure minutes
        long packed = PackedCriteria.pack(arrMins, changes, payload);

        // Ensure that depMins is not present
        assertFalse(PackedCriteria.hasDepMins(packed), "Packed criteria should not have departure minutes");
        assertEquals(arrMins, PackedCriteria.arrMins(packed), "Unpacked arrival minutes should match");
        assertEquals(changes, PackedCriteria.changes(packed), "Unpacked changes should match");
        assertEquals(payload, PackedCriteria.payload(packed), "Unpacked payload should match");

        // Calling depMins should throw an exception
        assertThrows(IllegalArgumentException.class, () ->
                        PackedCriteria.depMins(packed),
                "Should throw when trying to access depMins from a criteria without it"
        );
    }
}
