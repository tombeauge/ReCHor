package ch.epfl.rechor;

import ch.epfl.rechor.journey.Journey;
import ch.epfl.rechor.journey.Stop;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Objects;

public final class FormatterFr {
    private FormatterFr() {
    }

    public static String formatDuration(Duration duration) {
        Objects.requireNonNull(duration);

        long totalMinutes = duration.toMinutes();
        long hours = totalMinutes / 60;
        long minutes = totalMinutes % 60;

        if (hours > 0) {
            return String.format("%d h %d min", hours, minutes);
        } else {
            return String.format("%d min", minutes);
        }
    }

    public static String formatTime(LocalDateTime dateTime) {
        Objects.requireNonNull(dateTime);


        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendValue(ChronoField.HOUR_OF_DAY)
                .appendLiteral('h')
                .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
                .toFormatter();

        return dateTime.format(formatter);
    }

    public static String formatPlatformName(Stop stop) {
        if (stop.platformName() == null || stop.platformName().isEmpty()) {
            return "";
        }

        if (Character.isDigit(stop.platformName().charAt(0))) {
            return "voie " + stop.platformName();
        } else {
            return "quai " + stop.platformName();
        }
    }

    public static String formatLeg(Journey.Leg.Foot footLeg) {
        Objects.requireNonNull(footLeg);

        StringBuilder sb = new StringBuilder();
        sb.append(footLeg.isTransfer() ? "changement" : "trajet à pied");
        sb.append(" (").append(formatDuration(footLeg.duration())).append(")");

        return sb.toString();
    }

    public static String formatLeg(Journey.Leg.Transport leg) {
        Objects.requireNonNull(leg);

        String departurePlatform = formatPlatformName(leg.depStop());
        String arrivalPlatform = formatPlatformName(leg.arrStop());

        StringBuilder sb = new StringBuilder();
        sb.append(formatTime(leg.depTime()))
                .append(" ")
                .append(leg.depStop().name());

        if (!departurePlatform.isEmpty()) {
            sb.append(" (").append(departurePlatform).append(")");
        }

        sb.append(" → ")
                .append(leg.arrStop().name())
                .append(" (arr. ")
                .append(formatTime(leg.arrTime()));

        if (!arrivalPlatform.isEmpty()) {
            sb.append(" ").append(arrivalPlatform);
        }

        sb.append(")");

        return sb.toString();
    }

    public static String formatRouteDestination(Journey.Leg.Transport transportLeg) {
        Objects.requireNonNull(transportLeg, "transportLeg is null");

        StringBuilder sb = new StringBuilder();
        sb.append(transportLeg.route())
                .append(" Direction ")
                .append(transportLeg.destination());

        return sb.toString();
    }
}
