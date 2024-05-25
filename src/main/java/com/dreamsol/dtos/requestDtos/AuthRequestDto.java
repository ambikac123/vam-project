package com.dreamsol.dtos.requestDtos;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class AuthRequestDto
{
    @NotEmpty
    private String username;

    @NotEmpty
    private String password;
}

