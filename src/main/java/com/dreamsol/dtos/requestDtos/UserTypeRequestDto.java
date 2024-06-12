package com.dreamsol.dtos.requestDtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserTypeRequestDto extends CommonAutoIdEntityRequestDto
{
    @NotEmpty
    private String userTypeName;

    @NotEmpty
    private String userTypeCode;

    private boolean status;

}
