package ch.epfl.rechor;

/**
 * @author Tom Beauge
 *
 */
public final class Preconditions {
    private Preconditions() {

    }

    public static void checkArgument(boolean shouldBeTrue){
        if(!shouldBeTrue){
            throw new IllegalArgumentException();
        }
    }
}
