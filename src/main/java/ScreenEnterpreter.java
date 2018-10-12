import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

class ScreenEnterpreter {
    private LocalDate date = null;
    private long days = 0;

    private void scanDate(Scanner sc) {
        System.out.println("Enter date with format year/month/day");
        String dateStr = sc.nextLine();
        try {
            date = LocalDate.parse(dateStr, FileReader.DTF);
        } catch (DateTimeParseException exception) {
            System.out.println("Date format is wrong, try by example 2013/12/8");
            scanDate(sc);
        }
    }

    private void scanDays(Scanner sc) {
        System.out.println("Enter number of days:");
        String daysStr = sc.nextLine();
        try {
            days = Long.parseLong(daysStr);
        } catch (NumberFormatException exception) {
            System.out.println("Data is wrong, you should input a number");
            scanDays(sc);
        }
    }

    void scan() {
        Scanner sc = new Scanner(System.in);
        scanDate(sc);
        scanDays(sc);
    }

    LocalDate getDate() {
        return date;
    }

    long getDays() {
        return days;
    }
}
