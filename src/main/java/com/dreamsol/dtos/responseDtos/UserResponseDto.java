package com.dreamsol.dtos.responseDtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto extends CommonAutoIdEntityResponseDto
{
    private String name;
    private String email;
    private Long mobile;
    private DepartmentResponseDto department;
}
