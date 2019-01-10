package net.alexhyisen.widget;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

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

    private static void createIn() throws IOException {
        var sheet = new XSSFWorkbook("origin.xlsx").getSheetAt(0);
        var lines = StreamSupport
                .stream(sheet.spliterator(), false)
                .skip(1)
                .map(v -> String.format(
                        "%.0f %.0f %.0f %.0f",
                        v.getCell(0).getNumericCellValue(),
                        v.getCell(1).getNumericCellValue(),
                        v.getCell(2).getNumericCellValue(),
                        v.getCell(3).getNumericCellValue())
                )
                .collect(Collectors.toList());
        Files.write(Paths.get(".", "in"), lines);
    }

    private static void exam() throws IOException {
        var sheet = new XSSFWorkbook("origin.xlsx").getSheetAt(0);
        var workbook = new XSSFWorkbook();
        var infoSheet = workbook.createSheet("info");
        var dataSheet = workbook.createSheet("data");
        Utility.setRowValue(
                dataSheet.createRow(0),
                "status",
                "comment",
                "startYear",
                "startMonth",
                "startDay",
                "elapseDay",
                "expectYear",
                "expectMonth",
                "expectDay",
                "actualYear",
                "actualMonth",
                "actualDay"
        );

        final List<String> out = Files.readAllLines(Path.of("out"));
        int count = 0;
        for (int k = 0; k < out.size(); k++) {
            var row = sheet.getRow(k + 1);
            String expect = "";
            if (row.getCell(4) != null
                    || row.getCell(5) != null
                    || row.getCell(6) != null) {
                expect = String.format(
                        "%.0f %.0f %.0f",
                        row.getCell(4).getNumericCellValue(),
                        row.getCell(5).getNumericCellValue(),
                        row.getCell(6).getNumericCellValue());
            }
            var limb = out.get(k).split(" ");
            var status = "pass";
            if (!out.get(k).equals(expect)) {
                status = "fail";
                count++;
            }
            var line = dataSheet.createRow(k + 1);
            line.createCell(0).setCellValue(status);
            var commentCell = row.getCell(7);
            if (commentCell != null) {
                line.createCell(1).setCellValue(commentCell.getStringCellValue());
            }
            line.createCell(2).setCellValue(row.getCell(0).getNumericCellValue());
            line.createCell(3).setCellValue(row.getCell(1).getNumericCellValue());
            line.createCell(4).setCellValue(row.getCell(2).getNumericCellValue());
            line.createCell(5).setCellValue(row.getCell(3).getNumericCellValue());
            var cell = row.getCell(4);
            if (cell != null) {
                line.createCell(6).setCellValue(cell.getNumericCellValue());
            }
            cell = row.getCell(5);
            if (cell != null) {
                line.createCell(7).setCellValue(cell.getNumericCellValue());
            }
            cell = row.getCell(6);
            if (cell != null) {
                line.createCell(8).setCellValue(cell.getNumericCellValue());
            }
            if (limb.length == 3) {
                line.createCell(9).setCellValue(Integer.valueOf(limb[0]));
                line.createCell(10).setCellValue(Integer.valueOf(limb[1]));
                line.createCell(11).setCellValue(Integer.valueOf(limb[2]));
            }
        }

        var row = infoSheet.createRow(0);
        row.createCell(0).setCellValue("total");
        row.createCell(1).setCellValue(out.size());
        row = infoSheet.createRow(1);
        row.createCell(0).setCellValue("fail");
        row.createCell(1).setCellValue(count);
        row.createCell(2).setCellValue((double) count / out.size());
        row = infoSheet.createRow(2);
        row.createCell(0).setCellValue("pass");
        row.createCell(1).setCellValue(out.size() - count);
        row.createCell(2).setCellValue((double) (out.size() - count) / out.size());

        workbook.write(new FileOutputStream("report.xlsx"));
    }

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            if (!Files.exists(Paths.get(".", "in"))) {
                createIn();
            } else {
                exam();
            }
        } else if (args[0].equals("-p") || args[0].equals("--parse")) {
            parse();
        } else if (args[0].equals("-i") || args[0].equals("--genIn")) {
            genIn();
        } else if (args[0].equals("-o") || args[0].equals("--genOrig")) {
            genOrigin();
        }
    }
}
