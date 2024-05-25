package com.dreamsol.dtos.requestDtos;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@MappedSuperclass
public class CommonAutoIdEntityRequestDto {
    @NotBlank
    private boolean status;

    @NotBlank(message = "CreatedBy Field cannot be Empty")
    //@Pattern(regexp = "^[a-zA-Z]+([\s][a-zA-Z]+)*$", message = "Only Alphabets are allowed")
    @Size(min = 2, max = 50, message = "Enter a Valid Field value")
    private String createdBy;

    @NotBlank(message = "UpdatedBy Field cannot be Empty")
    //@Pattern(regexp = "^[a-zA-Z]+([\s][a-zA-Z]+)*$", message = "Only Alphabets are allowed")
    @Size(min = 2, max = 50, message = "Enter a Valid Field value")
    private String updatedBy;
}
