public class Main {

    public static void main(String args[]) {
        FileReader fr = new FileReader();
        fr.init();
        fr.printInfo();

        //If U need smth from keyboard
//        ScreenEnterpreter se = new ScreenEnterpreter();
//        se.scan();
//        fr.getHolidayList(se.getDate(), se.getDays()).forEach(v -> System.out.println(v.toString()));
    }
}
