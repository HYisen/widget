package net.alexhyisen.widget;

import org.apache.poi.xssf.usermodel.XSSFRow;

class Utility {
    static void setRowValue(XSSFRow row, String... values) {
        for (int k = 0; k < values.length; k++) {
            row.createCell(k).setCellValue(values[k]);
        }
    }
}
