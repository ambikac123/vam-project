package com.dreamsol.dtos.requestDtos;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Getter
@Setter
@MappedSuperclass
public class CommonAutoIdEntityRequestDto {

    private boolean status;

    @NotEmpty(message = "CreatedBy Field cannot be Empty")
    @Size(min = 2, max = 50, message = "Enter a Valid Field value")
    private String createdBy;

    @NotEmpty(message = "UpdatedBy Field cannot be Empty")
    @Size(min = 2, max = 50, message = "Enter a Valid Field value")
    private String updatedBy;
}
