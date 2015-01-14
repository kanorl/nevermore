package com.shadow.util.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author nevermore on 2015/1/14
 */
public class ExcelReader {

    public static List<List<List<String>>> toList(@Nonnull File file) {
        Objects.requireNonNull(file);
        try {
            InputStream in = new FileInputStream(file);
            Workbook workbook = new XSSFWorkbook(in);
            int sheets = workbook.getNumberOfSheets();
            List<List<List<String>>> result = new ArrayList<>(sheets);
            for (int i = 0; i < sheets; i++) {
                Sheet sheet = workbook.getSheetAt(i);
                List<List<String>> sheetList = new ArrayList<>(sheet.getPhysicalNumberOfRows());
                for (Row row : sheet) {
                    List<String> rowList = new ArrayList<>(row.getLastCellNum());
                    for (int j = 0; j < row.getLastCellNum(); j++) {
                        Cell cell = row.getCell(j);
                        if (cell == null) {
                            rowList.add(null);
                            continue;
                        }
                        cell.setCellType(Cell.CELL_TYPE_STRING);
                        rowList.add(cell.getStringCellValue());
                    }
                    sheetList.add(rowList);
                }
                result.add(sheetList);
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
