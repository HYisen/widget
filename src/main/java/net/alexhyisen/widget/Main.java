package net.alexhyisen.widget;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {
    public static void genIn() throws IOException {
        var path = Paths.get(".", "in");

        Random rand = new Random(17);
        int min = Math.toIntExact(LocalDate.of(1840, 1, 1).toEpochDay());
        int max = Math.toIntExact(LocalDate.of(2140, 1, 1).toEpochDay());

        final int NUM = 100;
        var one = new long[NUM];
        var two = new int[NUM][1000];
        for (int i = 0; i < one.length; i++) {
            one[i] = min + rand.nextInt(max - min);
            for (int j = 0; j < two[i].length; j++) {
                two[i][j] = rand.nextInt(40000);
            }
        }

        final DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyy M d ");
        final List<String> collect = IntStream.range(0, NUM).parallel()
                .boxed()
                .flatMap(v -> {
                    var date = LocalDate.ofEpochDay(one[v]).format(pattern);
                    return IntStream.range(0, 1000).mapToObj(i -> date + two[v][i]);
                })
                .collect(Collectors.toList());
        Files.write(path, collect, StandardOpenOption.CREATE_NEW);
    }

    private static void parse() throws IOException {
        final List<String> collect = Files.lines(Paths.get(".", "in"))
                .parallel()
                .map(v -> {
                    try {
                        final var limb = v.split(" ");
                        var date = new Date(
                                Integer.valueOf(limb[0]),
                                Integer.valueOf(limb[1]),
                                Integer.valueOf(limb[2]));
                        date.elapse(Integer.valueOf(limb[3]));
                        return date.format();
                    } catch (RuntimeException e) {
                        return "";
                    }
                })
                .collect(Collectors.toList());
        Files.write(Paths.get(".", "out"), collect, StandardOpenOption.CREATE_NEW);
    }

    private static void genOrigin() throws IOException {
        var workbook = new XSSFWorkbook();
        var sheet = workbook.createSheet();
        Utility.setRowValue(
                sheet.createRow(0),
                "startYear",
                "startMonth",
                "startDay",
                "elapseDay",
                "expectYear",
                "expectMonth",
                "expectDay",
                "comment"
        );

        final List<String> raw = Files.readAllLines(Paths.get(".", "in"));

        String[] limb;
        int year, month, day, elapse;
        for (int k = 0; k < raw.size(); k++) {
            limb = raw.get(k).split(" ");

            var row = sheet.createRow(k + 1);
            year = Integer.valueOf(limb[0]);
            month = Integer.valueOf(limb[1]);
            day = Integer.valueOf(limb[2]);
            elapse = Integer.valueOf(limb[3]);
            row.createCell(0).setCellValue(year);
            row.createCell(1).setCellValue(month);
            row.createCell(2).setCellValue(day);
            row.createCell(3).setCellValue(elapse);
            var date = LocalDate.of(year, month, day).plusDays(elapse);
            row.createCell(4).setCellValue(date.getYear());
            row.createCell(5).setCellValue(date.getMonthValue());
            row.createCell(6).setCellValue(date.getDayOfMonth());
        }

        workbook.write(new FileOutputStream("origin.xlsx"));
    }

    public static void main(String[] args) throws IOException {
//        genIn();
        parse();
//        genOrigin();
    }
}
