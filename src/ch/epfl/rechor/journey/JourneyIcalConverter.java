package ch.epfl.rechor.journey;

import ch.epfl.rechor.FormatterFr;
import ch.epfl.rechor.IcalBuilder;

import java.time.LocalDateTime;
import java.util.StringJoiner;
import java.util.UUID;

public class JourneyIcalConverter {

    private JourneyIcalConverter() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    public static String toIcalendar(Journey journey) {

        IcalBuilder builder = new IcalBuilder();

        StringJoiner descriptionJoiner = new StringJoiner("\n "); // Line break in iCalendar format (CRLF)
        for (Journey.Leg leg : journey.legs()) {
            switch (leg) {
                case Journey.Leg.Foot f -> descriptionJoiner.add(FormatterFr.formatLeg(f));
                case Journey.Leg.Transport t -> descriptionJoiner.add(FormatterFr.formatLeg(t));
            }
        }
        String description = descriptionJoiner.toString();

        builder.begin(IcalBuilder.Component.VCALENDAR);
        builder.add(IcalBuilder.Name.VERSION, "2.0");
        builder.add(IcalBuilder.Name.PRODID, "ReCHor");
        builder.begin(IcalBuilder.Component.VEVENT);
        builder.add(IcalBuilder.Name.UID, UUID.randomUUID().toString());
        builder.add(IcalBuilder.Name.DTSTAMP, LocalDateTime.now());
        builder.add(IcalBuilder.Name.DTSTART, journey.depTime());
        builder.add(IcalBuilder.Name.DTEND, journey.arrTime());
        builder.add(IcalBuilder.Name.SUMMARY, journey.depStop().name() + " → " + journey.arrStop().name());
        builder.add(IcalBuilder.Name.DESCRIPTION, FormatterFr.formatTime(journey.depTime()) + " " + description);
        builder.end();
        builder.end();


        return builder.build();
    }
}
