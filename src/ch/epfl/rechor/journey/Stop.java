package ch.epfl.rechor.journey;

import ch.epfl.rechor.Preconditions;

import java.time.LocalDateTime;
import java.util.Objects;

public record Stop (String name, String platformName, double longitude, double latitude) {
    public Stop {
        Objects.requireNonNull(name, "name is null");

        Preconditions.checkArgument(latitude <= 90 && latitude >= -90);
        Preconditions.checkArgument(longitude <= 180 && longitude >= -180);
    }
}
