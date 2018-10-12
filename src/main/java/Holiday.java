import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Holiday implements Comparable<Holiday> {
    private DateTimeFormatter DTF = DateTimeFormatter.ofPattern("E, d MMM");
    private LocalDate date;
    private String name;
    private String country;

    Holiday(LocalDate date, String name, String country) {
        this.date = date;
        this.name = name;
        this.country = country;
    }

    LocalDate getDate() {
        return date;
    }

    String getName() {
        return name;
    }

    String getCountry() {
        return country;
    }

    public int compareTo(Holiday h) {
        int param = this.getDate().compareTo(h.getDate());
        if (param != 0) {
            return param;
        }

        if (this.name.equals(h.getName())) {
            return 0;
        }
        return 1;
    }

    @Override
    public String toString() {
        return DTF.format(date) +
                " - " + name + "(" + country + ")";
    }
}
