package com.dreamsol.dtos.responseDtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ContactResponseDto extends CommonAutoIdEntityResponseDto {
    private String employeeId;
    private long mobileNumber;
    private String email;
    private String contactName;
    private String communicationName;
    private UnitResponseDto unit;
    private DepartmentResponseDto department;
}
