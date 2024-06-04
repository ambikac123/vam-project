package com.dreamsol.dtos.responseDtos;

import com.dreamsol.dtos.requestDtos.CommonAutoIdEntityRequestDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserTypeResponseDto extends CommonAutoIdEntityResponseDto
{
    private String userTypeName;

    private String userTypeCode;
}
