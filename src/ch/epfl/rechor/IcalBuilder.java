package ch.epfl.rechor;

import java.time.LocalDateTime;

public final class IcalBuilder {
    private final int MAX_LINE_LENGTH = 75;
    private static final String CRLF = "\r\n";
    private final StringBuilder builder = new StringBuilder();

    public enum Component {
        VCALENDAR,
        VEVENT;
    }

    public enum Name {
        BEGIN,
        END,
        PRODID,
        VERSION,
        UID, DTSTAMP,
        DTSTART,
        DTEND,
        SUMMARY,
        DESCRIPTION;
    }

    public IcalBuilder add(Name name, String value){
        int nChar = 0;

        StringBuilder text = new StringBuilder(name + ": " + value);
        int textLength = text.length();


        while (nChar < textLength) {

            if (nChar % MAX_LINE_LENGTH == 0){
                text.insert(nChar, CRLF + " ");
                textLength += 3; //accounts for CRLF char count as well as space
            }
            nChar++;
        }

        builder.append(text).append(CRLF);

        return this;
    }

    //TODO
    public IcalBuilder add(Name name, LocalDateTime dateTime) {
        return null;
    }

    public IcalBuilder begin(Component component) {
        return null;
    }

    public IcalBuilder end() {
        return null;
    }

    public String build() {
        return null;
    }

}

