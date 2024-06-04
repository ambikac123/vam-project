package com.dreamsol.dtos.responseDtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
<<<<<<< Updated upstream
public class ExcelValidateDataResponseDto {
    List<ValidatedData> validDataList;
=======
public class ExcelValidateDataResponseDto
{
    List<?> validDataList;
>>>>>>> Stashed changes
    List<ValidatedData> invalidDataList;
    long totalData;
    long totalValidData;
    long totalInvalidData;
    String message;
}
