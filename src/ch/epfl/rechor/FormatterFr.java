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


/**
 * Provides formatting utilities for journey-related data.
 * <p>
 * This final class offers static methods to format durations, times, platform names,
 * and journey legs (both for transport and walking segments)
 *
 * @author Tom BEAUGE
 */
public final class FormatterFr {
    private FormatterFr() {
    }

    /**
     *
     * @param duration
     * @return the formatted version of the duration of the journey in terms of hours and minutes
     * @throws NullPointerException if duration is null
     */
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

    /**
     *
     * @param dateTime
     * @return formatted version of the time including day/month/year
     * @throws NullPointerException if dateTime is null
     */
    public static String formatTime(LocalDateTime dateTime) {
        Objects.requireNonNull(dateTime);


        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendValue(ChronoField.HOUR_OF_DAY)
                .appendLiteral('h')
                .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
                .toFormatter();

        return dateTime.format(formatter);
    }

    /**
     *
     * @param stop
     * @return formatted version of the stops name after checking if it is a quai or a voie
     */
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

    /**
     *
     * @param footLeg
     * @return formatted version of the time spent walking depending on if you change stops
     * where it is called "changement" or if it is the same stop where it is called "trajet à pied".
     * @throws NullPointerException if footLeg is null
     */
    public static String formatLeg(Journey.Leg.Foot footLeg) {
        Objects.requireNonNull(footLeg);

        StringBuilder sb = new StringBuilder();
        sb.append(footLeg.isTransfer() ? "changement" : "trajet à pied");
        sb.append(" (").append(formatDuration(footLeg.duration())).append(")");

        return sb.toString();
    }

    /**
     * @param leg
     * @return formatted version of a leg of the public transport
     * @throws NullPointerException if leg is null
     */
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

    /**
     * @param transportLeg
     * @return formatted version of the line the type of public transport takes.
     * @throws NullPointerException if transportLeg is null and gives a message
     */
    public static String formatRouteDestination(Journey.Leg.Transport transportLeg) {
        Objects.requireNonNull(transportLeg, "transportLeg is null");

        StringBuilder sb = new StringBuilder();
        sb.append(transportLeg.route())
                .append(" Direction ")
                .append(transportLeg.destination());

        return sb.toString();
    }
}
