package ch.epfl.rechor.journey;

import ch.epfl.rechor.FormatterFr;

import java.time.LocalDateTime;
import java.util.StringJoiner;
import java.util.UUID;

public class JourneyIcalConverter {

    String toIcalendar(Journey journey) {
        String UID = UUID.randomUUID().toString();
        String DTstamp = LocalDateTime.now().toString();
        String DTstart = journey.depTime().toString();
        String DTend = journey.arrTime().toString();
        String summary = journey.depStop().toString() + " → " + journey.arrStop().toString();

        StringJoiner sj = new StringJoiner("\n");
        for (Journey.Leg leg : journey.legs()) {
            switch (leg) {
                case Journey.Leg.Foot f -> System.out.println("étape à pied : " + f);
                case Journey.Leg.Transport t -> System.out.println("étape en transport : " + t);
            }
        }

        String description;
        //description = sj.add(FormatterFr.formatTime(journey.depTime())).add(FormatterFr.formatPlatformName(journey.depStop())).add();
        //TODO
        description = FormatterFr.formatTime(journey.depTime()) + " " + summary;


        return "BEGIN:VCALENDAR\n" +
                "VERSION:2.0\n" +
                "PRODID:ReCHor\n" +
                "BEGIN:VEVENT\n" +
                "UID:" + UID + "\n" +
                "DTSTAMP" + DTstamp + "\n" +
                "DTSTART" + DTstart + "\n" +
                "DTEND" + DTend + "\n" +
                "SUMMARY" + summary + "\n" +
                "DESCRIPTION" + description + "\n" +
                "END:VEVENT\n" +
                "END:VCALENDAR";


    }

}
