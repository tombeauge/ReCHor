package ch.epfl.rechor;

public final class Preconditions {
    private Preconditions() {

        int max(int[] array){
            if (! (array.length > 0)) { throw new IllegalArgumentException();}
        }

        public static void checkArgument(boolean shouldBeTrue){
            if(!shouldBeTrue){
                throw new IllegalArgumentException();
            }
        }

    }
}
