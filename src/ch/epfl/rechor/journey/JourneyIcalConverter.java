package ch.epfl.rechor.journey;

import ch.epfl.rechor.FormatterFr;
import ch.epfl.rechor.IcalBuilder;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.StringJoiner;
import java.util.UUID;

/**
 * Class used to convert journeys into Strings using the Icalender format
 * @author Cem Celik (399448)
 */
public class JourneyIcalConverter {

    /**
     * Private constructor with no parameters to prevent instantiation
     */
    private JourneyIcalConverter() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    /**
     *
     * @param journey
     * @return A String which describes certain aspects of a trip/journey according to the principles of
     * the Icalender format providing values such as the local time, start time of journey, description,
     * and summary.
     */
    public static String toIcalendar(Journey journey) {

        IcalBuilder builder = new IcalBuilder();

        StringJoiner descriptionJoiner = new StringJoiner("/n"); // the icalbuilder takes care of adding spaces when adding new line

        //Loop that formats the description to let it know if a leg of the journey is on foot or by public transport
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
        builder.add(IcalBuilder.Name.DESCRIPTION, description); //TODO missing departure time and location
        builder.end();
        builder.end();


        return builder.build();
    }
}
