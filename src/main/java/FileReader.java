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
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

class FileReader {
    static DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy/M[M]/d[d]");
    private ConcurrentSkipListSet<Holiday> holidays;
    private ConcurrentHashMap<Month, Integer> monthCount;
    private ConcurrentHashMap<LocalDate, Integer> datesCount;

    private String date = "";
    private String name = "";
    private String country = "";

    public FileReader() {
        holidays = new ConcurrentSkipListSet<>();
        monthCount = new ConcurrentHashMap<>(12);
        datesCount = new ConcurrentHashMap<>(365);
    }


    private void addHoliday(String dateStr, String name, String country) {
        // strValue = countryMap.putIfAbsent(country, country);

        LocalDate date = LocalDate.parse(dateStr, DTF);

        // counting holidays per month
        Month m = date.getMonth();
        if (monthCount.get(m) != null) monthCount.replace(m, monthCount.get(m) + 1);
        else monthCount.put(m, 1);

        //counting holidays per date
        if (datesCount.get(date) != null) datesCount.replace(date, datesCount.get(date) + 1);
        else datesCount.put(date, 1);

        Holiday holiday = new Holiday(date, name, country);
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

    void printInfo() {
        Map.Entry entry = monthCount.entrySet().stream().min(Comparator.comparing(Map.Entry::getValue)).get();


        System.out.println("The least quantity was in " + entry.getKey().toString() + " " + entry.getValue());

        Map.Entry entry2 = monthCount.entrySet().stream().max(Comparator.comparing(Map.Entry::getValue)).get();

        System.out.println("The mast quantity was in " + entry2.getKey().toString() + " " + entry2.getValue());

        monthCount.forEach((k, v) -> System.out.println(k + " count " + v + "\n"));
        System.out.println("Map size " + holidays.size());
    }

    void init() {
        String fileName = "holidays.txt";
        //read file into stream, try-with-resources
        CopyOnWriteArrayList<String> stringArray = null;
        try (Stream<String> stream = Files.lines(Paths.get(fileName), guessCharset(Files.newInputStream(Paths.get(fileName))))) {
            stringArray = new CopyOnWriteArrayList<>(stream.toArray(String[]::new));
        } catch (IOException e) {
            System.out.println(fileName + " Dos not exist or can't be read");
            e.printStackTrace();
        }
        if (stringArray == null) return;
        Thread thread1 = new MyLineParser(stringArray, 0, stringArray.size() / 2);
        Thread thread2 = new MyLineParser(stringArray, stringArray.size() / 2 + stringArray.size() % 2, stringArray.size());
        thread1.start();
        thread2.start();
        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private synchronized void parseLine(String line) {
        System.out.println(line);

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

    class MyLineParser extends Thread {
        CopyOnWriteArrayList<String> stringArray;
        int start;
        int end;

        public MyLineParser(CopyOnWriteArrayList<String> stringArray, int start, int end) {
            this.stringArray = stringArray;
            this.start = start;
            this.end = end;
        }

        @Override
        public void run() {
            for (int i = start; i < end; i++) {
                parseLine(stringArray.get(i));
            }
        }
    }
}
