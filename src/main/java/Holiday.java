import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Holiday implements Comparable<Holiday> {
    private LocalDate date;
    private String name;

    Holiday(LocalDate date, String name) {
        this.date = date;
        this.name = name;
    }

    LocalDate getDate() {
        return date;
    }

    String getName() {
        return name;
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
    DateTimeFormatter DTF = DateTimeFormatter.ofPattern("E, d MMM");
    @Override
    public String toString() {



        return  DTF.format(date) +
                " - " + name;
    }
}
