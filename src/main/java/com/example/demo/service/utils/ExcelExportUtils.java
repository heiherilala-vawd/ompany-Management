package com.example.demo.service.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public final class ExcelExportUtils {

  private static final int MAX_DEPTH = 2;
  private static final Set<Class<?>> SIMPLE_TYPES =
      Set.of(BigDecimal.class, BigInteger.class, UUID.class, java.util.Date.class);

  private ExcelExportUtils() {}

  public static ByteArrayInputStream generateExcel(List<?> entities, String sheetName) {
    if (entities == null || entities.isEmpty()) {
      return new ByteArrayInputStream(new byte[0]);
    }

    Class<?> entityClass = entities.get(0).getClass();
    List<ColumnDefinition> columns = buildColumns(entityClass);

    try (Workbook workbook = new XSSFWorkbook()) {
      Sheet sheet = workbook.createSheet(sheetName != null ? sheetName : "Export");

      CellStyle headerStyle = createHeaderStyle(workbook);
      CellStyle dataStyle = createDataStyle(workbook);

      Row groupRow = sheet.createRow(0);
      Row fieldRow = sheet.createRow(1);

      for (int i = 0; i < columns.size(); i++) {
        ColumnDefinition col = columns.get(i);
        Cell gc = groupRow.createCell(i);
        gc.setCellValue(col.groupName);
        gc.setCellStyle(headerStyle);

        Cell fc = fieldRow.createCell(i);
        fc.setCellValue(col.fieldName);
        fc.setCellStyle(headerStyle);
      }

      mergeGroupCells(sheet, columns);

      for (int rowIdx = 0; rowIdx < entities.size(); rowIdx++) {
        Row row = sheet.createRow(rowIdx + 2);
        Object entity = entities.get(rowIdx);
        for (int colIdx = 0; colIdx < columns.size(); colIdx++) {
          Cell cell = row.createCell(colIdx);
          Object value = columns.get(colIdx).getValue(entity);
          setCellValue(cell, value, dataStyle);
          cell.setCellStyle(dataStyle);
        }
      }

      for (int i = 0; i < columns.size(); i++) {
        sheet.autoSizeColumn(i);
      }

      ByteArrayOutputStream out = new ByteArrayOutputStream();
      workbook.write(out);
      return new ByteArrayInputStream(out.toByteArray());
    } catch (Exception e) {
      throw new RuntimeException("Failed to generate Excel", e);
    }
  }

  private static List<ColumnDefinition> buildColumns(Class<?> clazz) {
    List<ColumnDefinition> columns = new ArrayList<>();
    Set<Class<?>> visited = new HashSet<>();
    buildColumnsRecursive(clazz, clazz.getSimpleName(), new ArrayList<>(), columns, visited);
    return columns;
  }

  private static void buildColumnsRecursive(
      Class<?> clazz,
      String groupName,
      List<Field> fieldChain,
      List<ColumnDefinition> columns,
      Set<Class<?>> visited) {
    if (visited.contains(clazz) || groupName == null) {
      return;
    }
    visited.add(clazz);

    for (Field field : getAllFields(clazz)) {
      if (shouldSkip(field)) {
        continue;
      }

      Class<?> type = field.getType();

      if (isSimpleType(type)) {
        List<Field> path = new ArrayList<>(fieldChain);
        path.add(field);
        columns.add(new ColumnDefinition(groupName, field.getName(), path));
      } else if (!isCollectionType(type)) {
        List<Field> newChain = new ArrayList<>(fieldChain);
        newChain.add(field);
        buildColumnsRecursive(type, type.getSimpleName(), newChain, columns, visited);
      }
    }
  }

  private static List<Field> getAllFields(Class<?> clazz) {
    List<Field> fields = new ArrayList<>();
    Class<?> current = clazz;
    while (current != null && current != Object.class) {
      fields.addAll(0, Arrays.asList(current.getDeclaredFields()));
      current = current.getSuperclass();
    }
    return fields;
  }

  private static boolean shouldSkip(Field field) {
    int mod = field.getModifiers();
    if (Modifier.isStatic(mod) || Modifier.isTransient(mod) || field.isSynthetic()) {
      return true;
    }
    Class<?> type = field.getType();
    return Collection.class.isAssignableFrom(type)
        || Map.class.isAssignableFrom(type)
        || type.isArray();
  }

  private static boolean isSimpleType(Class<?> type) {
    if (type.isPrimitive() || type.isEnum()) {
      return true;
    }
    if (type.getName().startsWith("java.lang")) {
      return true;
    }
    if (type.getName().startsWith("java.time")) {
      return true;
    }
    return SIMPLE_TYPES.contains(type);
  }

  private static boolean isCollectionType(Class<?> type) {
    return Collection.class.isAssignableFrom(type)
        || Map.class.isAssignableFrom(type)
        || type.isArray();
  }

  private static CellStyle createHeaderStyle(Workbook workbook) {
    CellStyle style = workbook.createCellStyle();
    style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    style.setBorderBottom(BorderStyle.THIN);
    style.setBorderTop(BorderStyle.THIN);
    style.setBorderLeft(BorderStyle.THIN);
    style.setBorderRight(BorderStyle.THIN);
    Font font = workbook.createFont();
    font.setBold(true);
    style.setFont(font);
    style.setAlignment(HorizontalAlignment.CENTER);
    style.setVerticalAlignment(VerticalAlignment.CENTER);
    return style;
  }

  private static CellStyle createDataStyle(Workbook workbook) {
    CellStyle style = workbook.createCellStyle();
    style.setBorderBottom(BorderStyle.THIN);
    style.setBorderTop(BorderStyle.THIN);
    style.setBorderLeft(BorderStyle.THIN);
    style.setBorderRight(BorderStyle.THIN);
    return style;
  }

  private static void mergeGroupCells(Sheet sheet, List<ColumnDefinition> columns) {
    int start = 0;
    for (int i = 1; i <= columns.size(); i++) {
      if (i == columns.size() || !columns.get(i).groupName.equals(columns.get(start).groupName)) {
        if (i - start > 1) {
          sheet.addMergedRegion(new CellRangeAddress(0, 0, start, i - 1));
        }
        start = i;
      }
    }
  }

  private static void setCellValue(Cell cell, Object value, CellStyle style) {
    if (value == null) {
      return;
    }
    if (value instanceof Boolean b) {
      cell.setCellValue(b);
    } else if (value instanceof Number n) {
      cell.setCellValue(n.doubleValue());
    } else if (value instanceof LocalDate d) {
      cell.setCellValue(d.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    } else if (value instanceof LocalDateTime d) {
      cell.setCellValue(d.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
    } else if (value instanceof Instant i) {
      cell.setCellValue(i.toString());
    } else if (value instanceof Enum e) {
      cell.setCellValue(e.name());
    } else {
      cell.setCellValue(value.toString());
    }
  }

  private static class ColumnDefinition {

    final String groupName;
    final String fieldName;
    final Field[] fieldPath;

    ColumnDefinition(String groupName, String fieldName, List<Field> fieldPath) {
      this.groupName = groupName;
      this.fieldName = fieldName;
      this.fieldPath = fieldPath.toArray(new Field[0]);
      for (Field f : this.fieldPath) {
        f.setAccessible(true);
      }
    }

    Object getValue(Object root) {
      Object current = root;
      for (Field field : fieldPath) {
        if (current == null) {
          return null;
        }
        try {
          current = field.get(current);
        } catch (IllegalAccessException e) {
          return null;
        }
      }
      return current;
    }
  }
}
