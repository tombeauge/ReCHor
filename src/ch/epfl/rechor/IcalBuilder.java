package ch.epfl.rechor;

import java.time.LocalDateTime;

public final class IcalBuilder {

    public enum Component{
        VCALENDAR,
        VEVENT
    }

    public enum Name{
        BEGIN,
        END,
        PRODID,
        VERSION,
        UID,
        DTSTAMP,
        DTSTART,
        DTEND,
        SUMMARY,
        DESCRIPTION
    }

    public IcalBuilder add(Name name, String value) {
        return
    }

    public IcalBuilder add(Name name, LocalDateTime dateTime) {
        return
    }

    public IcalBuilder begin(Component component) {
        return
    }

    public IcalBuilder end() {
        return
    }

    public String build() {
        return
    }

}
