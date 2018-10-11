import org.apache.any23.encoding.TikaEncodingDetector;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Main {
    private static TreeSet<Holiday> holidays;
    static DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy/M[M]/d[d]");

    public static void main(String args[]) {
        init();
//        LocalDate date = LocalDate.parse("2013/9/7", DTF);
//        getHolidayList(date, 0).forEach(v -> System.out.println(v.toString()));

        ScreenEnterpreter se = new ScreenEnterpreter();
        se.scan();
        getHolidayList(se.getDate(), se.getDays()).forEach(v -> System.out.println(v.toString()));

    }

    private static Charset guessCharset(InputStream is) throws IOException {
        return Charset.forName(new TikaEncodingDetector().guessEncoding(is));
    }

    private static void init() {
        String fileName = "holidays.txt";
        holidays = new TreeSet<>();
        //read file into stream, try-with-resources
        try (Stream<String> stream = Files.lines(Paths.get(fileName), guessCharset(Files.newInputStream(Paths.get(fileName))))) {
            stream.forEach(line -> {
                //       System.out.println(line);
                Pattern pattern = Pattern.compile(" ");
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    String date = line.substring(0, matcher.start());
                    String name = line.substring(matcher.end());
                    addHoliday(date, name);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void addHoliday(String dateStr, String name) {
        LocalDate date = LocalDate.parse(dateStr, DTF);
        Holiday holiday = new Holiday(date, name);
        holidays.add(holiday);
    }

    static List<Holiday> getHolidayList(LocalDate date, long number) {
        List<Holiday> holidatList = new ArrayList<>();
        LocalDate dueDate = date.plusDays(number);

        for (Holiday holiday : holidays) {
            LocalDate holidayDate = holiday.getDate();
            if (holidayDate.isAfter(dueDate)) {
                break;
            }

            if (holidayDate.isEqual(date) || holidayDate.isAfter(date)) {
                holidatList.add(holiday);
            }
        }
        return holidatList;
    }

}
