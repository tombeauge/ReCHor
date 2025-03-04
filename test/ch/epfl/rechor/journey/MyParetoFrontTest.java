package ch.epfl.rechor.journey;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.NoSuchElementException;

public class MyParetoFrontTest {

    @Test
    public void simpleGetAndAddTest() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(100, 20, 5);
        ParetoFront front =  builder.build();
        System.out.println(front.toString());

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
    public void testFullyDominates() {
        ParetoFront.Builder builder1 = new ParetoFront.Builder();
        builder1.add(10, 2, 5);
        builder1.add(8, 1, 4);

        ParetoFront.Builder builder2 = new ParetoFront.Builder();
        builder2.add(15, 3, 1); // Clearly worse than all in builder1

        assertTrue(builder1.fullyDominates(builder2, 5));
        assertFalse(builder2.fullyDominates(builder1, 5));
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
        System.out.println(front.toString());
        int[] count = {0};
        front.forEach(value -> count[0]++);

        assertEquals(2, count[0]); // Ensures that all elements are iterated over
    }

    @Test
    public void fullyDominates(){
        ParetoFront.Builder builder1 = new ParetoFront.Builder();
        builder1.add(10, 2, 5);
        builder1.build();

        ParetoFront.Builder builder2 = new ParetoFront.Builder();
        builder2.add(8, 1, 4);
        builder2.build();

        assertFalse(builder1.fullyDominates(builder2, 5));
    }
}
