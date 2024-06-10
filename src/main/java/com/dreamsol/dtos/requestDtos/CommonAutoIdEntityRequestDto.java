package com.dreamsol.dtos.requestDtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.MappedSuperclass;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
public class CommonAutoIdEntityRequestDto {

    private boolean status=true;

    private Long unitId;
    // @NotEmpty(message = "CreatedBy Field cannot be Empty")
    // @Pattern(regexp = "^[a-zA-Z]+([\s][a-zA-Z]+)*$", message = "Only Alphabets
    // are allowed")

   /* @Size(min = 2, max = 50, message = "Enter a Valid Field value")
    private String createdBy;

    // @NotEmpty(message = "UpdatedBy Field cannot be Empty")

    // @Pattern(regexp = "^[a-zA-Z]+([\s][a-zA-Z]+)*$", message = "Only Alphabets
    // are allowed")

    @Size(min = 2, max = 50, message = "Enter a Valid Field value")
    private String updatedBy;*/

    // @Pattern(regexp = "^[0-9])", message = "Only Numbers are allowed")
}
