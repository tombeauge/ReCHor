package ch.epfl.rechor.journey;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

public class MyParetoFrontTest {

    @Test
    public void simpleGetAndAddTest() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(100, 20, 5);
        ParetoFront front = builder.build();

        long test1 = front.get(100, 20);
        assertEquals(test1, PackedCriteria.pack(100, 20, 5));

    }

    @Test
    public void testEmptyParetoFront() {
        ParetoFront frontier = ParetoFront.EMPTY;
        assertEquals(0, frontier.size());
        assertThrows(NoSuchElementException.class, () -> frontier.get(10, 2));
    }

    @Test
    public void testBuilderIsInitiallyEmpty() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        assertTrue(builder.isEmpty());
    }

    @Test
    public void testAddingSingleElement() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(10, 2, 5);
        assertFalse(builder.isEmpty());

        ParetoFront front = builder.build();
        assertEquals(1, front.size());
        assertDoesNotThrow(() -> front.get(10, 2));
    }

    @Test
    public void testAddingDuplicateElement() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(10, 2, 5);
        builder.add(10, 2, 5); // Duplicate

        ParetoFront front = builder.build();
        assertEquals(1, front.size()); // Should not duplicate
    }

    @Test
    public void testDominatedElementsAreRemoved() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(15, 3, 1); // Worse element
        builder.add(10, 2, 5); // Better element (dominates previous)

        ParetoFront front = builder.build();
        assertEquals(1, front.size());
        assertDoesNotThrow(() -> front.get(10, 2));
        assertThrows(NoSuchElementException.class, () -> front.get(15, 3));
    }

    @Test
    public void testInsertOrderDoesNotMatter() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(15, 3, 5);
        builder.add(10, 2, 1);
        ParetoFront front1 = builder.build();

        assertEquals(1, front1.size());


    }

    @Test
    public void testInsertOrderDoesNotMatter2() {
        ParetoFront.Builder builder1 = new ParetoFront.Builder();
        builder1.add(10, 2, 5);
        builder1.add(15, 3, 1);
        ParetoFront front1 = builder1.build();

        ParetoFront.Builder builder2 = new ParetoFront.Builder();
        builder2.add(15, 3, 1);
        builder2.add(10, 2, 5);
        ParetoFront front2 = builder2.build();

        assertEquals(front1.size(), front2.size());
    }

    @Test
    public void testInsertOrderDoesNotMatter3() {
        ParetoFront.Builder builder1 = new ParetoFront.Builder();
        builder1.add(10, 2, 5);
        builder1.add(10, 2, 5);
        builder1.add(5, 3, 1);
        ParetoFront front1 = builder1.build();

        ParetoFront.Builder builder2 = new ParetoFront.Builder();
        builder2.add(5, 3, 1);
        builder2.add(10, 3, 5);
        builder2.add(10, 2, 5);
        ParetoFront front2 = builder2.build();

        ParetoFront.Builder builder3 = new ParetoFront.Builder();
        builder3.add(5, 3, 1);
        builder3.add(10, 2, 5);
        builder3.add(10, 3, 5);
        ParetoFront front3 = builder3.build();

        assertEquals(front1.size(), front2.size());
    }

    @Test
    public void testFullyDominates() {
        ParetoFront.Builder builder1 = new ParetoFront.Builder();
        long crit1 = PackedCriteria.pack(10, 2, 5);
        long crit2 = PackedCriteria.pack(8, 1, 4);

        crit1 = PackedCriteria.withDepMins(crit1, 5);
        crit2 = PackedCriteria.withDepMins(crit2, 4);
        builder1.add(crit1);
        builder1.add(crit2);

        ParetoFront.Builder builder2 = new ParetoFront.Builder();
        builder2.add(15, 3, 1); // Clearly worse than all in builder1

        assertTrue(builder1.fullyDominates(builder2, 5));
    }

    @Test
    public void testAddingMultipleNonDominatedElements() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(10, 2, 5);
        builder.add(8, 4, 4); // Should not be removed
        builder.add(12, 1, 6); // Should not be removed

        ParetoFront front = builder.build();
        assertEquals(3, front.size());
    }

    @Test
    public void testClearBuilder() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(10, 2, 5);
        builder.clear();
        assertTrue(builder.isEmpty());
    }

    @Test
    public void testAddingToClearedBuilder() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(10, 2, 5);
        builder.clear();
        builder.add(15, 3, 1);

        ParetoFront front = builder.build();
        assertEquals(1, front.size());
        assertDoesNotThrow(() -> front.get(15, 3));
    }

    @Test
    public void testBuildDoesNotAffectOriginalBuilder() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(10, 2, 5);
        ParetoFront front1 = builder.build();

        builder.add(15, 1, 1); // Modify builder
        ParetoFront front2 = builder.build();

        assertEquals(1, front1.size()); // The first built ParetoFront should not change
        assertEquals(2, front2.size()); // The second build should include the new element
    }

    @Test
    public void testForEach() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(10, 2, 5);
        builder.add(8, 4, 4);

        ParetoFront front = builder.build();
        int[] count = {0};
        front.forEach(value -> count[0]++);

        assertEquals(2, count[0]); // Ensures that all elements are iterated over
    }

    @Test
    public void testForEach2() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(10, 2, 5);
        builder.add(8, 4, 4);
        builder.add(10, 4, 4);

        ParetoFront front = builder.build();
        System.out.println(front.toString());
        int[] count = {0};
        front.forEach(value -> count[0]++);

        assertEquals(2, count[0]); // Ensures that all elements are iterated over
    }

    @Test
    public void fullyDominates() {
        ParetoFront.Builder builder1 = new ParetoFront.Builder();
        long crit1 = PackedCriteria.pack(10, 2, 5);
        crit1 = PackedCriteria.withDepMins(crit1, 5);

        builder1.add(crit1);
        builder1.build();

        ParetoFront.Builder builder2 = new ParetoFront.Builder();
        builder2.add(8, 1, 4);
        builder2.build();

        assertFalse(builder1.fullyDominates(builder2, 5));
    }

    @Test
    public void fullyDominates2() {
        ParetoFront.Builder builder1 = new ParetoFront.Builder();
        long crit1 = PackedCriteria.pack(10, 2, 5);
        crit1 = PackedCriteria.withDepMins(crit1, 5);
        builder1.add(crit1);

        long crit2 = PackedCriteria.pack(8, 4, 2);
        crit2 = PackedCriteria.withDepMins(crit2, 5);
        builder1.add(crit2);

        long crit3 = PackedCriteria.pack(6, 10, 4);
        crit3 = PackedCriteria.withDepMins(crit3, 5);
        builder1.add(crit3);
        builder1.build();

        ParetoFront.Builder builder2 = new ParetoFront.Builder();
        long crit4 = PackedCriteria.pack(10, 1, 4);
        crit4 = PackedCriteria.withDepMins(crit4, 5);
        builder2.add(crit4);

        long crit5 = PackedCriteria.pack(7, 4, 2);
        crit5 = PackedCriteria.withDepMins(crit5, 5);
        builder2.add(crit5);

        long crit6 = PackedCriteria.pack(6, 6, 40);
        crit6 = PackedCriteria.withDepMins(crit6, 5);
        builder2.add(crit6);
        builder2.build();

        assertFalse(builder1.fullyDominates(builder2, 5));
        assertTrue(builder2.fullyDominates(builder1, 5)); //TODO check dep minutes
    }

    @Test
    public void addAllThenClear() {
        ParetoFront.Builder builder1 = new ParetoFront.Builder();
        builder1.add(10, 2, 5);
        builder1.add(8, 4, 2);
        builder1.add(6, 10, 4);
        builder1.add(15, 1, 5);
        builder1.add(2, 30, 409);
        //builder1.build();

        ParetoFront.Builder builder2 = new ParetoFront.Builder();
        builder2.add(10, 1, 4);
        builder2.add(7, 4, 2);
        builder2.add(6, 6, 40);

        builder2.addAll(builder1);
        ParetoFront front = builder2.build();

        System.out.println(front.toString());

        assertEquals(front.get(2, 30), PackedCriteria.pack(2, 30, 409));
        assertEquals(4, front.size());

        builder2.clear();
        assertTrue(builder2.isEmpty());

        ParetoFront front2 = builder2.build();

        assertEquals(0, front2.size());
    }

    @Test
    public void overflow() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(100, 99, 999999999);
        ParetoFront front = builder.build();

        long test1 = front.get(100, 99);
        long crit = PackedCriteria.pack(100, 20, 5);
        assertEquals(100, PackedCriteria.arrMins(crit));

    }

    @Test
    public void testAddDominatedElementNotAdded() {
        ParetoFront.Builder builder = new ParetoFront.Builder();

        builder.add(480, 2, 100);

        builder.add(480, 3, 200);
        ParetoFront front = builder.build();
        assertEquals(1, front.size());
        long packed = front.get(480, 2);
        assertEquals(480, PackedCriteria.arrMins(packed));
        assertEquals(2, PackedCriteria.changes(packed));
    }

    @Test
    public void testAddDominatesExistingElements() {
        ParetoFront.Builder builder = new ParetoFront.Builder();

        builder.add(500, 3, 100);
        builder.add(500, 2, 150);

        builder.add(500, 1, 200);
        ParetoFront front = builder.build();
        assertEquals(1, front.size());
        long packed = front.get(500, 1);
        assertEquals(500, PackedCriteria.arrMins(packed));
        assertEquals(1, PackedCriteria.changes(packed));
    }

    @Test
    public void testAddAll() {
        ParetoFront.Builder builder1 = new ParetoFront.Builder();
        builder1.add(480, 4, 100);
        builder1.add(490, 3, 110);

        ParetoFront.Builder builder2 = new ParetoFront.Builder();
        builder2.add(510, 2, 120);

        builder2.add(500, 2, 100);

        builder1.addAll(builder2);
        ParetoFront front = builder1.build();

        assertEquals(3, front.size());
    }

    @Test
    public void testExtremeArrivalTimes() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        // Minimum valid arrival time (-4*60 = -240) and maximum valid arrival time (47*60+59 = 2879)
        builder.add(-240, 10, 100);
        builder.add(2879, 0, 200);
        ParetoFront front = builder.build();
        assertEquals(2, front.size());
        // Both extreme points should be retrievable
        assertDoesNotThrow(() -> front.get(-240, 10));
        assertDoesNotThrow(() -> front.get(2879, 0));
    }

    @Test
    public void testParetoFrontSortedOrder() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        // Add points in unsorted order. Note that (200,3) and (400,2) are non-dominated even though 400 > 200;
        // (400,2) is kept because its lower changes (2) do not let (200,3) dominate it.
        builder.add(300, 5, 1);
        builder.add(200, 3, 2);
        builder.add(250, 4, 3);
        builder.add(400, 2, 4);
        ParetoFront front = builder.build();
        // Expected Pareto front: (200,3) and (400,2) since (200,3) dominates (250,4) and (300,5)
        assertEquals(2, front.size());
        long[] values = new long[front.size()];
        final int[] idx = {0};
        front.forEach(val -> values[idx[0]++] = val);
        int arr1 = PackedCriteria.arrMins(values[0]);
        int changes1 = PackedCriteria.changes(values[0]);
        int arr2 = PackedCriteria.arrMins(values[1]);
        int changes2 = PackedCriteria.changes(values[1]);
        // Check that the tuples are sorted lexicographically: first by arrival then by number of changes.
        assertTrue(arr1 < arr2 || (arr1 == arr2 && changes1 <= changes2),
                "Pareto frontier is not sorted lexicographically.");
    }

    @Test
    public void testBuilderCopyConstructor() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(300, 10, 100);
        // Create a copy of the builder
        ParetoFront.Builder copy = new ParetoFront.Builder(builder);
        copy.add(250, 9, 200);
        ParetoFront frontOriginal = builder.build();
        ParetoFront frontCopy = copy.build();
        System.out.println(frontCopy);
        // The original builder should not include the newly added element
        assertThrows(NoSuchElementException.class, () -> frontOriginal.get(250, 9));
        assertDoesNotThrow(() -> frontCopy.get(250, 9));
    }

    @Test
    public void simpleTest(){
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(300, 10, 100);
        builder.add(250, 9, 200);

        ParetoFront front = builder.build();
        System.out.println(front);
        assertDoesNotThrow(() -> front.get(250, 9));
        assertThrows(NoSuchElementException.class, () -> front.get(300, 10));

    }

    @Test
    public void printingRandomParetoFrontWithDepTime() {
        // Inner helper class to represent a (departure, arrival, changes) triplet.
        class Triplet {
            int dep, arr, changes;

            Triplet(int dep, int arr, int changes) {
                this.dep = dep;
                this.arr = arr;
                this.changes = changes;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                Triplet triplet = (Triplet) o;
                return dep == triplet.dep && arr == triplet.arr && changes == triplet.changes;
            }

            @Override
            public int hashCode() {
                return Objects.hash(dep, arr, changes);
            }
        }

        final int NUM_POINTS = 500;  // Increased number of points for better coverage
        Random rng = new Random(42);
        List<Triplet> uniqueTrips = new ArrayList<>();
        ParetoFront.Builder builder = new ParetoFront.Builder();

        for (int i = 0; i < NUM_POINTS; i++) {
            int dep = rng.nextInt(1440);    // Departure time between 0 and 1439 (full day in minutes)
            int arr = dep + rng.nextInt(500);  // Arrival time after departure, within a range
            int changes = rng.nextInt(20);  // Changes between 0 and 19
            int payload = rng.nextInt(1000);

            Triplet trip = new Triplet(dep, arr, changes);
            if (!uniqueTrips.contains(trip)) {
                uniqueTrips.add(trip);
            }

            long packed = PackedCriteria.pack(arr, changes, payload);
            packed = PackedCriteria.withDepMins(packed, dep); // Ensuring depTime is included
            builder.add(packed);
        }

        ParetoFront front = builder.build();
        System.out.println("Random Front with DepTime: " + front.toString());
    }


    @Test
    public void testNoDominatedElementAddedExtensive() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(100, 5, 1);
        builder.add(110, 6, 2); // Dominated by (100,5)
        builder.add(120, 7, 3); // Dominated by (100,5)
        builder.add(90, 10, 4); // Incomparable with (100,5); both should be kept
        builder.add(95, 9, 5);  // Incomparable with (100,5) and (90,10)
        ParetoFront front = builder.build();
        // Expected Pareto elements: (100,5), (90,10) and (95,9)
        assertEquals(3, front.size());
        assertDoesNotThrow(() -> front.get(100, 5));
        assertDoesNotThrow(() -> front.get(90, 10));
        assertDoesNotThrow(() -> front.get(95, 9));
        // The dominated tuples should not be found.
        assertThrows(NoSuchElementException.class, () -> front.get(110, 6));
        assertThrows(NoSuchElementException.class, () -> front.get(120, 7));
    }

    @Test
    public void testParetoFrontPropertyNoDominationAmongElements() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        // Add a mix of elements that are all non-dominated (none in the final front should dominate another).
        builder.add(300, 10, 1);
        builder.add(250, 15, 2);
        builder.add(200, 20, 3);
        builder.add(220, 18, 4);
        builder.add(280, 8, 5);
        ParetoFront front = builder.build();
        // Verify that for any two distinct elements in the frontier, neither dominates the other.
        front.forEach(a -> {
            front.forEach(b -> {
                if (a != b) {
                    boolean aDominatesB = (PackedCriteria.arrMins(a) <= PackedCriteria.arrMins(b)
                            && PackedCriteria.changes(a) <= PackedCriteria.changes(b))
                            && (PackedCriteria.arrMins(a) < PackedCriteria.arrMins(b)
                            || PackedCriteria.changes(a) < PackedCriteria.changes(b));
                    assertFalse(aDominatesB, "Pareto frontier violation: an element dominates another.");
                }
            });
        });
    }
}



