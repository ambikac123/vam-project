package com.dreamsol.dtos.responseDtos;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UnitResponseDto extends CommonAutoIdEntityResponseDto {
    private String unitName;
    private String unitIp;
    private String unitCity;
    private String passAddress;
    private String passDisclaimer;
    private Set<DepartmentResponseDto> departments;
}
