package com.dreamsol.utility;

import com.dreamsol.dtos.responseDtos.ExcelValidateDataResponseDto;
import com.dreamsol.dtos.responseDtos.ValidatedData;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
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

import javax.persistence.Column;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class ExcelUtility
{
    private static final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private static final Validator validator = factory.getValidator();
    // Checks whether the given file is an Excel file or not
    public boolean isExcelFile(MultipartFile file) {
        String contentType = file.getContentType();
        if(contentType!=null)
            return contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") ||
                    contentType.equals("application/vnd.ms-excel");
        else
            return false;
    }


    // To download Excel format
    public Resource downloadExcelSample(Class<?> currentClass, String sheetName)
    {
        Map<String,String> headersMap = getRequiredMap(currentClass,"header");
        Map<String,String> columnTypeMap = getRequiredMap(currentClass,"columntype");
        Map<String,String> dataExampleMap = getRequiredMap(currentClass,"example");
        return generateExcelSample(columnTypeMap,headersMap,dataExampleMap,sheetName);
    }

    // To generate Excel format
    private Resource generateExcelSample(Map<String,String> columnTypeMap, Map<String,String> headersMap, Map<String,String> dataExampleMap,String sheetName)
    {
        try(Workbook workbook = new XSSFWorkbook())
        {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Sheet sheet = workbook.createSheet(sheetName);
            Set<String> headersNameSet = headersMap.keySet();
            Row headerRow = sheet.createRow(0);
            Row exampleRow = sheet.createRow(1);

            int cellIndex = 0;
            for(String headerName : headersNameSet)
            {
                createExcelHeader(workbook,columnTypeMap,headerName,headerRow,cellIndex);
                Cell exampleCell = exampleRow.createCell(cellIndex);
                exampleCell.setCellValue(dataExampleMap.get(headerName));
                exampleCell.setCellStyle(getExampleCellStyle(workbook));
                cellIndex++;
            }

            workbook.write(byteArrayOutputStream);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            return new InputStreamResource(byteArrayInputStream);
        }catch (Exception e)
        {
            throw new RuntimeException(e.getMessage());
        }
    }

    // To download data in excel file
    public Resource downloadDataAsExcel(List<?> dataList,String sheetName)
    {
        Class<?> currentClass = dataList.get(0).getClass();
        Map<String,String> headersMap = getRequiredMap(currentClass,"header");
        return convertListToExcel(dataList,headersMap,sheetName);
    }
    public ExcelValidateDataResponseDto validateExcelData(MultipartFile file, Class<?> currentClass)
    {
        ExcelValidateDataResponseDto validateDataResponse = new ExcelValidateDataResponseDto();
        List<?> dataList = convertExcelToList(file,currentClass);
        List<ValidatedData> validDataList = new ArrayList<>();
        List<ValidatedData> invalidDataList = new ArrayList<>();
        for(Object data : dataList)
        {
            String message = isValidData(data);
            if(message.equalsIgnoreCase("correct")) {
                ValidatedData validData = new ValidatedData();
                validData.setData(data);
                validData.setMessage(message);
                validDataList.add(validData);
            }
            else{
                ValidatedData invalidData = new ValidatedData();
                invalidData.setData(data);
                invalidData.setMessage(message);
                invalidDataList.add(invalidData);
            }
        }
        validateDataResponse.setValidDataList(validDataList);
        validateDataResponse.setInvalidDataList(invalidDataList);
        validateDataResponse.setTotalData(dataList.size());
        validateDataResponse.setTotalValidData(validDataList.size());
        validateDataResponse.setTotalInvalidData(invalidDataList.size());
        validateDataResponse.setMessage("Process completed successfully!");
        return validateDataResponse;
    }
    private String isValidData(Object data)
    {
        Set<ConstraintViolation<Object>> violations = validator.validate(data);
        System.out.println(violations);
        StringBuilder message = new StringBuilder();
        for(ConstraintViolation<Object> violation : violations)
        {
            message.append(violation.getMessage()).append(", ");
        }
        return message.toString();
    }
    private List<?> convertExcelToList(MultipartFile file, Class<?> currentClass)
    {
        try {
            List<Object> dataList = new ArrayList<>();
            InputStream inputStream = file.getInputStream();
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            Map<String,String> headersMap = getRequiredMap(currentClass,"header");
            for(Row row : sheet)
            {
                if(row.getRowNum()<=1)
                    continue;
                Object currentObject = currentClass.getDeclaredConstructor().newInstance();
                int cellIndex = 0;
                addCellValueToField(currentClass,currentObject,row,cellIndex);
                dataList.add(currentObject);
            }
            return dataList;
        }catch (Exception e)
        {
            throw new RuntimeException(e.getMessage());
        }
    }
    private void addCellValueToField(Class<?> currentClass, Object currentObject, Row row, int cellIndex) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Field[] fields = currentClass.getDeclaredFields();
        for(Field field : fields)
        {
            field.setAccessible(true);
            if(field.getType().isPrimitive() || !field.getType().getName().startsWith("com.dreamsol"))
            {
                Cell cell = row.getCell(cellIndex);
                setCellValueToField(currentObject, cell,field);
            }else{
                Class<?> currentSubClass = field.getType();
                Object currentSubObject = currentSubClass.getDeclaredConstructor().newInstance();
                addCellValueToField(currentSubClass,currentSubObject,row,cellIndex);
                field.set(currentObject,currentSubObject);
            }
            cellIndex++;
        }
    }
    private void setCellValueToField(Object currentObject, Cell cell, Field field) throws IllegalAccessException {
        if(cell != null)
        {
            CellType cellType = cell.getCellType();
            if(cellType == CellType.STRING)
            {
                field.set(currentObject,cell.getStringCellValue());
            } else if (cellType == CellType.NUMERIC)
            {
                String fieldTypeName = field.getType().getSimpleName();
                if(fieldTypeName.equalsIgnoreCase("short"))
                    field.set(currentObject,(short)cell.getNumericCellValue());
                else if(fieldTypeName.equalsIgnoreCase("int") || fieldTypeName.equalsIgnoreCase("integer"))
                    field.set(currentObject,(int)cell.getNumericCellValue());
                else if(fieldTypeName.equalsIgnoreCase("long"))
                    field.set(currentObject,(long)cell.getNumericCellValue());
                else if(fieldTypeName.equalsIgnoreCase("float"))
                    field.set(currentObject,(float)cell.getNumericCellValue());
                else
                    field.set(currentObject,cell.getNumericCellValue());
            } else if (cellType == CellType.BOOLEAN){
                field.set(currentObject,cell.getBooleanCellValue());
            } else {
                field.set(currentObject,cell.getLocalDateTimeCellValue());
            }
        }
    }
    private Map<String,String> getRequiredMap(Class<?> currentClass,String requiredMapType)
    {
        Map<String,String> requiredMap = new LinkedHashMap<>();
        Class<?> superClass = currentClass.getSuperclass();
        addFieldsToMap(requiredMap,currentClass.getDeclaredFields(),requiredMapType);
        if(superClass !=null && superClass != Object.class)
            addFieldsToMap(requiredMap,superClass.getDeclaredFields(),requiredMapType);
        return requiredMap;
    }
    private void addFieldsToMap(Map<String,String> requiredMap, Field[] fields, String requiredMapType)
    {
        for(Field field : fields)
        {
            if(field.getType().isPrimitive() || !field.getType().getName().startsWith("com.dreamsol"))
            {
                if(requiredMapType.equalsIgnoreCase("header"))
                {
                    String fieldName = field.getName();
                    String headerName = convertCamelCaseToWords(fieldName);
                    requiredMap.put(headerName, fieldName);
                }else if(requiredMapType.equalsIgnoreCase("columntype"))
                {
                    Column column = field.getAnnotation(javax.persistence.Column.class);
                    NotNull notNull = field.getAnnotation(javax.validation.constraints.NotNull.class);
                    NotEmpty notEmpty = field.getAnnotation(javax.validation.constraints.NotEmpty.class);
                    NotBlank notBlank = field.getAnnotation(javax.validation.constraints.NotBlank.class);
                    if(notNull != null || notEmpty != null || notBlank != null || (column != null && !column.nullable()))
                    {
                        requiredMap.put(convertCamelCaseToWords(field.getName()),"Mandatory");
                    }else{
                        requiredMap.put(convertCamelCaseToWords(field.getName()),"Optional");
                    }
                }else if(requiredMapType.equalsIgnoreCase("example")){
                    requiredMap.put(convertCamelCaseToWords(field.getName()),getSampleValueFromField(field));
                }
            }else{
                addFieldsToMap(requiredMap,field.getType().getDeclaredFields(),requiredMapType);
            }
        }
    }
    private Resource convertListToExcel(List<?> list, Map<String,String> headersMap, String sheetName)
    {
        try (Workbook workbook = new XSSFWorkbook())
        {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Sheet sheet = workbook.createSheet(sheetName);
            Set<String> headersNameSet = headersMap.keySet();

            CellStyle style1 = workbook.createCellStyle();
            style1.setAlignment(HorizontalAlignment.CENTER);
            style1.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            style1.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Font font1 = workbook.createFont();
            font1.setColor(IndexedColors.WHITE.getIndex());
            font1.setBold(true);
            style1.setFont(font1);

            int colIndex = 0;
            Row headerRow = sheet.createRow(colIndex);
            for(String headerName : headersNameSet)
            {
                Cell cell = headerRow.createCell(colIndex);
                cell.setCellValue(headerName);
                cell.setCellStyle(style1);
                colIndex++;
            }

            // Create data rows
            int rowIndex = 1;
            Class<?> mainEntity = list.get(0).getClass();
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
        else if(fieldType.equalsIgnoreCase("short"))
            cell.setCellValue((short) field.get(item));
        else if(fieldType.equalsIgnoreCase("int") || fieldType.equalsIgnoreCase("integer"))
            cell.setCellValue((int) field.get(item));
        else if(fieldType.equalsIgnoreCase("long"))
            cell.setCellValue((long) field.get(item));
        else if(fieldType.equalsIgnoreCase("float"))
            cell.setCellValue((float) field.get(item));
        else if(fieldType.equalsIgnoreCase("double"))
            cell.setCellValue((double) field.get(item));
        else if(fieldType.equalsIgnoreCase("boolean"))
            cell.setCellValue((boolean) field.get(item));
        else if(item!=null)
            cell.setCellValue(field.get(item).toString());
        else
            cell.setCellValue("NA");
    }

    private String getSampleValueFromField(Field field) {
        String fieldType = field.getType().getSimpleName();
        switch (fieldType.toLowerCase())
        {
            case "string":
                return "text";
            case "int":
            case "integer": // Adding integer to handle the Integer wrapper class
            case "long":
            case "double":
            case "float":
                return "number";
            case "boolean":
                return "true/false";
            case "localdate":
            case "localdatetime":
                return "date/time";
            default:
                return "NA";
        }
    }
    private static String convertCamelCaseToWords(String input) {
        // Use a regular expression to insert a space before each uppercase letter
        String spacedString = input.replaceAll("([a-z])([A-Z])", "$1 $2");
        return  Character.toUpperCase(spacedString.charAt(0))+spacedString.substring(1);
    }

    // Style for mandatory cells
    private CellStyle getMandatoryCellStyle(Workbook workbook)
    {
        CellStyle mandatoryCellStyle = workbook.createCellStyle();
        mandatoryCellStyle.setFillForegroundColor(IndexedColors.DARK_RED.getIndex());
        mandatoryCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        mandatoryCellStyle.setAlignment(HorizontalAlignment.CENTER);
        Font mandatoryCellFont = workbook.createFont();
        mandatoryCellFont.setBold(true);
        mandatoryCellFont.setColor(IndexedColors.WHITE.getIndex());
        mandatoryCellStyle.setFont(mandatoryCellFont);
        return mandatoryCellStyle;
    }

    //Style for optional cells
    private CellStyle getOptionalCellStyle(Workbook workbook)
    {
        CellStyle optionalCellStyle = workbook.createCellStyle();
        optionalCellStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        optionalCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        optionalCellStyle.setAlignment(HorizontalAlignment.CENTER);
        Font optionalCellFont = workbook.createFont();
        optionalCellFont.setBold(true);
        optionalCellFont.setColor(IndexedColors.WHITE.getIndex());
        optionalCellStyle.setFont(optionalCellFont);
        return optionalCellStyle;
    }

    // Style for example cells
    private CellStyle getExampleCellStyle(Workbook workbook)
    {
        CellStyle exampleCellStyle = workbook.createCellStyle();
        exampleCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        exampleCellStyle.setFillPattern(FillPatternType.FINE_DOTS);
        exampleCellStyle.setAlignment(HorizontalAlignment.CENTER);
        Font exampleCellFont = workbook.createFont();
        exampleCellFont.setItalic(true);
        exampleCellFont.setColor(IndexedColors.GREY_50_PERCENT.getIndex());
        exampleCellStyle.setFont(exampleCellFont);
        return exampleCellStyle;
    }
    private void createExcelHeader(Workbook workbook,Map<String,String> columnTypeMap,String headerName,Row headerRow,int cellIndex)
    {
        if(columnTypeMap.get(headerName).equalsIgnoreCase("mandatory"))
        {
            Cell headerCell = headerRow.createCell(cellIndex);
            headerCell.setCellValue(headerName+"*");
            headerCell.setCellStyle(getMandatoryCellStyle(workbook));
        }else {
            Cell headerCell = headerRow.createCell(cellIndex);
            headerCell.setCellValue(headerName);
            headerCell.setCellStyle(getOptionalCellStyle(workbook));
        }
    }
}
