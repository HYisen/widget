package net.alexhyisen.widget;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Exam {
    private static void createIn() throws IOException {
        var sheet = new XSSFWorkbook("origin.xlsx").getSheetAt(0);
        var lines = StreamSupport
                .stream(sheet.spliterator(), false)
                .map(v -> String.format(
                        "%s %s %s %s",
                        v.getCell(0).getStringCellValue(),
                        v.getCell(1).getStringCellValue(),
                        v.getCell(2).getStringCellValue(),
                        v.getCell(3).getStringCellValue())
                )
                .collect(Collectors.toList());
        Files.write(Paths.get(".", "in"), lines);
    }

    private static void exam() {

    }

    public static void main(String[] args) throws IOException {
        if (!Files.exists(Paths.get(".", "in"))) {
            createIn();
        } else {
            exam();
        }
    }
}
