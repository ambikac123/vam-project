package com.dreamsol.dtos.requestDtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserTypeRequestDto
{
    @NotEmpty
    private String userTypeName;

    @NotEmpty
    private String userTypeCode;

    private boolean status;
}
