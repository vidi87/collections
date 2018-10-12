import org.apache.any23.encoding.TikaEncodingDetector;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

class FileReader {
    static DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy/M[M]/d[d]");
    private ConcurrentSkipListSet<Holiday> holidays;
    private Map<String, String> countryMap;
    private String strValue;
    private ConcurrentMap<Month, Integer> monthCount;
    private ConcurrentHashMap<LocalDate, Integer> datesCount;

    private String date = "";
    private String name = "";
    private String country = "";

    public FileReader() {
        holidays = new ConcurrentSkipListSet<>();
        countryMap = new WeakHashMap<>();
        monthCount = new ConcurrentHashMap<>(12);
        datesCount = new ConcurrentHashMap<>(365);
    }


    private void addHoliday(String dateStr, String name, String country) {
        strValue = countryMap.putIfAbsent(country, country);

        LocalDate date = LocalDate.parse(dateStr, DTF);
        Holiday holiday = new Holiday(date, name, strValue);
        holidays.add(holiday);
    }


    //TODO переписать под многопоточность
    List<Holiday> getHolidayList(LocalDate date, long number) {
        List<Holiday> holidayList = new ArrayList<>();

        LocalDate dueDate = date.plusDays(number);

        for (Holiday holiday : holidays) {
            LocalDate holidayDate = holiday.getDate();
            if (holidayDate.isAfter(dueDate)) {
                break;
            }

            if (holidayDate.isEqual(date) || holidayDate.isAfter(date)) {
                holidayList.add(holiday);
            }
        }
        return holidayList;
    }

    void init() {
        String fileName = "holidays.txt";
        //read file into stream, try-with-resources
        String[] stringArray;
        try (Stream<String> stream = Files.lines(Paths.get(fileName), guessCharset(Files.newInputStream(Paths.get(fileName))))) {
            stringArray = stream.toArray(String[]::new);
        } catch (IOException e) {
            System.out.println(fileName + " Dos not exist or can't be read");
            e.printStackTrace();
        }


    }

    private void parseLine(String line) {
        Pattern pattern = Pattern.compile(" ");
        Matcher matcher = pattern.matcher(line);

        if (matcher.find()) {
            date = line.substring(0, matcher.start());
            name = line.substring(matcher.end(), line.indexOf('('));
        }

        country = line.substring(line.indexOf("(") + 1, line.indexOf(')'));
        addHoliday(date, name, country);
    }

    private Charset guessCharset(InputStream is) throws IOException {
        return Charset.forName(new TikaEncodingDetector().guessEncoding(is));
    }
}
