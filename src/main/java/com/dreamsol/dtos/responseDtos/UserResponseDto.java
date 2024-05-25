package com.dreamsol.dtos.responseDtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto extends CommonAutoIdEntityResponseDto
{
    private String name;
    private String email;
    private Long mobile;
}
