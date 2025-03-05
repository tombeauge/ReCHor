package ch.epfl.rechor.journey;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MyPareToFrontTest {

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
    public void simpleGetAndAddTest2() {

        ParetoFront.Builder builder = new ParetoFront.Builder();

        for (int i = 0; i < 20; i++) {
            for (int j = 20; j > 0; j--) {
                builder.add(i, j, i + j);
            }
        }

        ParetoFront front = builder.build();

        System.out.println(front.toString());
    }
}
