package ch.epfl.rechor;

import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * A builder class for creating iCalendar (.ics) files.
 * This class allows structured generation of iCalendar components
 * such as VCALENDAR and VEVENT with their respective attributes.
 */
public final class IcalBuilder {

    /** The maximum allowed line length in an iCalendar file before folding. */
    private final int MAX_LINE_LENGTH = 75;

    /** Carriage return and line feed characters for line breaks. */
    private static final String CRLF = "\r\n";

    /** StringBuilder to store the generated iCalendar content. */
    private final StringBuilder sb = new StringBuilder();

    /** Stack to track the currently open components. */
    private final ArrayList<Component> buildingComponents = new ArrayList<>();

    /**
     * Enum representing the different components of an iCalendar file.
     */
    public enum Component {
        VCALENDAR,
        VEVENT;
    }

    /**
     * Enum representing the different fields (properties) of an iCalendar component.
     */
    public enum Name {
        BEGIN,
        END,
        PRODID,
        VERSION,
        UID,
        DTSTAMP,
        DTSTART,
        DTEND,
        SUMMARY,
        DESCRIPTION;
    }

    /**
     * Adds a key-value pair to the iCalendar content.
     * If the line exceeds the maximum allowed length, it is folded according to iCalendar specifications.
     *
     * @param name The iCalendar property name.
     * @param value The value associated with the property.
     * @return The current IcalBuilder instance.
     */
    public IcalBuilder add(Name name, String value) {
        int nChar = 0;
        StringBuilder text = new StringBuilder(name + ": " + value);
        int textLength = text.length();

        while (nChar < textLength) {
            if (nChar % MAX_LINE_LENGTH == 0) {
                text.insert(nChar, CRLF + " ");
                textLength += 3; // Accounts for CRLF character count as well as space.
            }
            nChar++;
        }

        sb.append(text).append(CRLF);
        return this;
    }

    /**
     * Adds a key-value pair where the value is a LocalDateTime.
     * The date-time value is formatted in iCalendar format (YYYYMMDDTHHMMSS).
     *
     * @param name The iCalendar property name.
     * @param dateTime The LocalDateTime object to format.
     * @return The current IcalBuilder instance.
     */
    public IcalBuilder add(Name name, LocalDateTime dateTime) {
        String formattedDateTime = String.format("%04d%02d%02dT%02d%02d%02d",
                dateTime.getYear(), dateTime.getMonthValue(), dateTime.getDayOfMonth(),
                dateTime.getHour(), dateTime.getMinute(), dateTime.getSecond());
        return add(name, formattedDateTime);
    }

    /**
     * Begins a new iCalendar component and adds it to the structure.
     *
     * @param component The component to begin (e.g., VCALENDAR, VEVENT).
     * @return The current IcalBuilder instance.
     */
    public IcalBuilder begin(Component component) {
        buildingComponents.add(component);
        return add(Name.BEGIN, component.name());
    }

    /**
     * Ends the most recently opened iCalendar component.
     *
     * @return The current IcalBuilder instance.
     * @throws IllegalStateException if no components are open.
     */
    public IcalBuilder end() {
        if (buildingComponents.isEmpty()) {
            throw new IllegalStateException("There are no components to build");
        }

        // Closes the most recent component and removes it from the list.
        Component component = buildingComponents.remove(buildingComponents.size() - 1);
        return add(Name.END, component.name());
    }

    /**
     * Builds and returns the final iCalendar content as a string.
     *
     * @return The generated iCalendar content.
     * @throws IllegalStateException if some components are left unclosed.
     */
    public String build() {
        if (!buildingComponents.isEmpty()) {
            throw new IllegalStateException("Some components have not been closed");
        }
        return sb.toString();
    }
}
