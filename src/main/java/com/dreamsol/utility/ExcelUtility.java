package com.dreamsol.utility;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class ExcelUtility
{
    // Checks whether the given file is an Excel file or not
    public boolean isExcelFile(MultipartFile file)
    {
        String contentType = file.getContentType();
        return contentType != null && contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }
    public Resource downloadDataSampleAsExcel(Object firstObject)
    {
        Map<String,String> headersMap = getExcelHeadersMap(firstObject);
        return null;
    }
    public Resource downloadDataAsExcel(List<?> dataList,String sheetName)
    {
        Map<String,String> headersMap = getExcelHeadersMap(dataList.get(0));
        return convertListToExcel(dataList,headersMap,sheetName);
    }
    private Map<String, String> getExcelHeadersMap(Object firstObject) {
    Map<String,String> headersMap = new LinkedHashMap<>();

    // Use the first object in the list to determine the fields
    Class<?> currentClass = firstObject.getClass();
    Class<?> superClass = currentClass.getSuperclass();

    // Add fields from the current class
    addFieldsToHeadersMap(headersMap, currentClass.getDeclaredFields());

    // Add fields from the superclass if it exists
    if (superClass != null && superClass != Object.class) {
        addFieldsToHeadersMap(headersMap, superClass.getDeclaredFields());
    }
    return headersMap;
}

    private void addFieldsToHeadersMap(Map<String, String> headersMap,Field[] fields){
        for(Field field : fields)
        {
            if(field.getType().isPrimitive() || !field.getType().getName().startsWith("com.dreamsol"))
            {
                String fieldName = field.getName();
                String headerName = convertCamelCaseToWords(fieldName);
                headersMap.put(headerName, field.getName());
            }else{
                Field[] fields1 = field.getType().getDeclaredFields();
                addFieldsToHeadersMap(headersMap,fields1);
            }
        }
    }
    private static String convertCamelCaseToWords(String input) {
        // Use a regular expression to insert a space before each uppercase letter
        String spacedString = input.replaceAll("([a-z])([A-Z])", "$1 $2");
        return  Character.toUpperCase(spacedString.charAt(0))+spacedString.substring(1);
    }
    private Resource convertListToExcel(List<?> list, Map<String,String> headersMap, String sheetName)
    {
        try (Workbook workbook = new XSSFWorkbook())
        {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Sheet sheet = workbook.createSheet(sheetName);
            Map<Integer,String> cellHeaderMap = new HashMap<>();
            Set<String> headersNameSet = headersMap.keySet();

            CellStyle style1 = workbook.createCellStyle();
            style1.setAlignment(HorizontalAlignment.CENTER);

            Font font1 = workbook.createFont();
            font1.setFontName("arial");
            font1.setBold(true);
            style1.setFont(font1);

            int colIndex = 0;
            Row headerRow = sheet.createRow(colIndex);
            for(String headerName : headersNameSet)
            {
                Cell cell = headerRow.createCell(colIndex);
                cell.setCellValue(headerName);
                cell.setCellStyle(style1);
                cellHeaderMap.put(colIndex++,headerName);
            }
            System.out.println("cellHeaderMap: "+cellHeaderMap);
            System.out.println("headersMap: "+headersMap);
            System.out.println(list);
            // Create data rows
            int rowIndex = 1;
            Class<?> mainEntity = list.get(0).getClass();
            System.out.println(mainEntity.getSimpleName());
            for(Object item : list)
            {
                Row row = sheet.createRow(rowIndex++);
                int cellIndex = 0;
                cellIndex = fieldsFromClass(row, mainEntity, item,cellIndex);
                fieldsFromClass(row, mainEntity.getSuperclass(),item,cellIndex);
            }

            workbook.write(byteArrayOutputStream);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            return new InputStreamResource(byteArrayInputStream);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    private int fieldsFromClass(Row row, Class<?> classType,Object item,int cellIndex) throws IllegalAccessException {
        Field[] fields = classType.getDeclaredFields();
        for(Field field : fields)
        {
            field.setAccessible(true);
            if(field.getType().isPrimitive() || !field.getType().getName().startsWith("com.dreamsol"))
            {
                System.out.println(field.getName()+" : "+cellIndex);
                Cell cell = row.createCell(cellIndex++);
                setCellValueFromField(cell,field,item);
            }else if(field.get(item)!=null){
                    cellIndex = fieldsFromClass(row,field.getType(),field.get(item),cellIndex);
            }else{
                Field[] fields1 = field.getType().getDeclaredFields();
                for(Field field1 : fields1)
                {
                    System.out.println(field1.getName()+" : "+cellIndex);
                    Cell cell = row.createCell(cellIndex++);
                    cell.setCellValue("NA");
                }
            }

        }
        return cellIndex;
    }
    private void setCellValueFromField(Cell cell, Field field, Object item) throws IllegalAccessException {
        String fieldType = field.getType().getSimpleName();
        if(fieldType.equalsIgnoreCase("string"))
            cell.setCellValue((String) field.get(item));
        else if(fieldType.equalsIgnoreCase("long"))
            cell.setCellValue((long) field.get(item));
        else if(fieldType.equalsIgnoreCase("boolean"))
            cell.setCellValue((boolean) field.get(item));
        else if(item!=null)
            cell.setCellValue(field.get(item).toString());
        else
            cell.setCellValue("NA");
    }

}
