package ch.epfl.rechor.journey;

import ch.epfl.rechor.Preconditions;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Constructs a Stop using the stops name, the platform name and the longitude and latitude of the position where it is located
 * @author Tom Beauge
 * @param name
 * @param platformName
 * @param longitude
 * @param latitude
 */
public record Stop (String name, String platformName, double longitude, double latitude) {

    public Stop {
        /**
         * Makes sure name is not null and the latitude is between 90 and -90 and the longitude is between 180 and -180
         */
        Objects.requireNonNull(name, "name is null");
        Preconditions.checkArgument(latitude <= 90 && latitude >= -90);
        Preconditions.checkArgument(longitude <= 180 && longitude >= -180);
    }
}
