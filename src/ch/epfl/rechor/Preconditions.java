package ch.epfl.rechor;

/**
 * Handles checking certain preconditions and arguments
 */
public final class Preconditions {
    private Preconditions() {throw new UnsupportedOperationException("This class cannot be instantiated");}

    /**
     * Checks if the shouldBeTrue parameter is true and if it is not it throws a IllegalArgumentExcpetion
     * @param shouldBeTrue
     */
    public static void checkArgument(boolean shouldBeTrue){
        if(!shouldBeTrue){
            throw new IllegalArgumentException();
        }
    }
}
