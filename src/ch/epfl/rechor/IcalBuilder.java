package ch.epfl.rechor;

import java.time.LocalDateTime;
import java.util.ArrayList;

public final class IcalBuilder {
    private final int MAX_LINE_LENGTH = 75;
    private static final String CRLF = "\r\n";
    private final StringBuilder sb = new StringBuilder();
    private final ArrayList<Component> buildingComponents = new ArrayList<Component>();

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

        sb.append(text).append(CRLF);

        return this;
    }

    public IcalBuilder add(Name name, LocalDateTime dateTime) {
        String formattedDateTime = String.format("%04d%02d%02dT%02d%02d%02d",
                dateTime.getYear(), dateTime.getMonthValue(), dateTime.getDayOfMonth(),
                dateTime.getHour(), dateTime.getMinute(), dateTime.getSecond());
        return add(name, formattedDateTime);
    }

    public IcalBuilder begin(Component component) {
        buildingComponents.add(component);
        return add(Name.BEGIN, component.name());
    }

    public IcalBuilder end() {
        if (buildingComponents.isEmpty()) {
            throw new IllegalStateException("There are no components to build");
        }

        //closes the most recent component and removes it from our list
        Component component = buildingComponents.remove(buildingComponents.size() - 1);
        return add(Name.END, component.name());
    }

    public String build() {
        if (!buildingComponents.isEmpty()) {
            throw new IllegalStateException("Some components have not been closed");
        }
        return sb.toString();
    }

}

