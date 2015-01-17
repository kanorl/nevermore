package com.shadow.resource.reader;

import com.google.common.collect.Maps;
import com.shadow.resource.ResourceConfiguration;
import com.shadow.resource.annotation.Resource;
import com.shadow.util.codec.JsonUtil;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author nevermore on 2015/1/15
 */
@Component
public class ExcelReader implements ResourceReader {

    @Autowired
    private ResourceConfiguration cfg;

    @Override
    public <T> List<T> read(Class<T> resourceType) {
        String filePath = getResourceFilePath(resourceType);
        try {
            File file = ResourceUtils.getFile(filePath);
            return file2ResourceBeans(file, resourceType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private <T> List<T> file2ResourceBeans(File file, Class<?> resourceType) {
        try {
            InputStream in = new FileInputStream(file);
            Workbook workbook = WorkbookFactory.create(in);
            List<Sheet> sheets = filteredSheets(workbook, resourceType);
            if (sheets.isEmpty()) {
                return Collections.emptyList();
            }
            Map<String, Integer> columnIndexes = columnIndexes(sheets.get(0));
            int firstDataRow = firstDataRow(sheets.get(0));
            List<T> beans = new ArrayList<>();
            for (Sheet sheet : sheets) {
                for (Row row : sheet) {
                    if (row.getRowNum() < firstDataRow) {
                        continue;
                    }
                    if (isIgnoreRow(row)) {
                        continue;
                    }
                    T obj = createBeanWithRowData(row, columnIndexes, resourceType);
                    beans.add(obj);
                    if (isTheLastRow(row)) {
                        break;
                    }
                }
            }
            return beans;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isTheLastRow(Row row) {
        return cfg.getEndTag().equalsIgnoreCase(getCellStringValue(row.getCell(0)));
    }

    private boolean isIgnoreRow(Row row) {
        return cfg.getIgnoreTag().equalsIgnoreCase(getCellStringValue(row.getCell(0)));
    }

    @SuppressWarnings("unchecked")
    private <T> T createBeanWithRowData(Row row, Map<String, Integer> columnIndexes, Class<?> resourceType) throws Exception {
        T bean = (T) resourceType.newInstance();
        for (Field field : resourceType.getDeclaredFields()) {
            Integer index = columnIndexes.get(field.getName());
            if (index == null) {
                continue;
            }
            Cell cell = row.getCell(index);
            String value = getCellStringValue(cell);
            if (value == null) {
                continue;
            }
            Object filedValue = field.getType() == String.class ? value : JsonUtil.toObject(value, field.getType());
            ReflectionUtils.makeAccessible(field);
            field.set(bean, filedValue);
        }
        return bean;
    }

    private int firstDataRow(Sheet sheet) {
        for (Row row : sheet) {
            Cell cell = row.getCell(0);
            if (cell != null && getCellStringValue(cell).equalsIgnoreCase(cfg.getTitleTag())) {
                return row.getRowNum() + 1;
            }
        }
        throw new IllegalStateException();
    }

    private Map<String, Integer> columnIndexes(Sheet sheet) {
        int titleRow = 1;
        Row row = sheet.getRow(titleRow);
        if (row == null) {
            throw new IllegalStateException();
        }
        int numberOfColumns = row.getPhysicalNumberOfCells();
        Map<String, Integer> result = Maps.newHashMapWithExpectedSize(numberOfColumns);
        for (int i = 0; i < numberOfColumns; i++) {
            Cell cell = row.getCell(i);
            if (cell == null) {
                continue;
            }
            result.put(getCellStringValue(cell), i);
        }
        return result;
    }

    private List<Sheet> filteredSheets(Workbook workbook, Class<?> resourceType) {
        int numberOfSheets = workbook.getNumberOfSheets();
        List<Sheet> sheets = new ArrayList<>(numberOfSheets);
        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            Row row = sheet.getRow(0);
            if (row == null) {
                continue;
            }
            Cell cell = row.getCell(0);
            if (cell == null) {
                continue;
            }
            if (resourceType.getSimpleName().equals(getCellStringValue(cell))) {
                sheets.add(sheet);
            }
        }
        return sheets;
    }

    private String getCellStringValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        if (cell.getCellType() != Cell.CELL_TYPE_STRING) {
            cell.setCellType(Cell.CELL_TYPE_STRING);
        }
        return cell.getStringCellValue();
    }

    private String getResourceFilePath(Class<?> resourceType) {
        return cfg.getResourceLocation() + File.separator + resourceType.getAnnotation(Resource.class).dir() + File.separator + resourceType.getSimpleName() + cfg.getFileSuffix();
    }
}
