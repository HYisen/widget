package net.alexhyisen.widget;

public class Date {
    private int year;
    private int month;
    private int day;

    public Date(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;

        if (getDayLimit(month, year) < day) {
            throw new RuntimeException();
        }
    }

    public void elapse(int days) {
        synchronized (this) {
            if (days >= 0) {
                if (days != 0 && month == 2 && day == 29) {
                    nextDay();
                    days -= 1;
                }
                while (days >= 366) {
                    if ((isLeap(year) && !hasLeapt()) || (isLeap(year + 1) && hasLeapt())) {
                        days -= 1;
                    }
                    days -= 365;
                    year += 1;
                }
                for (int i = 0; i < days; i++) {
                    nextDay();
                }
            } else {
                if (month == 2 && day == 29) {
                    prevDay();
                    days += 1;
                }
                while (days <= -366) {
                    if ((isLeap(year) && hasLeapt()) || (isLeap(year - 1) && !hasLeapt())) {
                        days += 1;
                    }
                    days += 365;
                    year -= 1;
                }
                for (int i = 0; i < -days; i++) {
                    prevDay();
                }
            }
        }
    }

    private void nextDay() {
        day += 1;
        int limit = getDayLimit(month, year);
        if (day > limit) {
            day = 1;
            nextMonth();
        }
    }

    private void prevDay() {
        day -= 1;
        if (day == 0) {
            prevMonth();
            day = getDayLimit(month, year);
        }
    }

    private static int getDayLimit(int month, int year) {
        int limit;
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                limit = 31;
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                limit = 30;
                break;
            case 2:
                limit = isLeap(year) ? 29 : 28;
                break;
            default:
                throw new RuntimeException();
        }
        return limit;
    }

    private static boolean isLeap(int year) {
        if (year % 100 == 0) {
            return year % 400 == 0;
        } else {
            return year % 4 == 0;
        }
    }

    private boolean hasLeapt() {
        return month > 2 || (month == 2 && day > 28);
    }

    private void nextMonth() {
        month += 1;
        if (month > 12) {
            month = 1;
            year += 1;
        }
    }

    private void prevMonth() {
        month -= 1;
        if (month == 0) {
            month = 12;
            year -= 1;
        }
    }

    public String format() {
        return String.format("%d %d %d", year, month, day);
    }

    @Override
    public String toString() {
        return String.format("%04d-%02d-%02d", year, month, day);
    }

//    public static void main(String[] args) {
//        var one = new Date(1970, 1, 1);
//
//        for (int i = 0; i < 1000000; i++) {
//            one.elapse(1);
//            String refer = LocalDate.ofEpochDay(i + 1).toString();
////            System.out.println(refer+" vs "+one);
//            if (!one.toString().equals(refer)) {
//                System.out.println(refer + " != " + one);
//                break;
//            }
//        }
//    }
}
