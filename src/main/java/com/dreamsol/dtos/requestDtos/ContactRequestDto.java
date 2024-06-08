package com.dreamsol.dtos.requestDtos;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.dreamsol.dtos.responseDtos.DropDownDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ContactRequestDto extends CommonAutoIdEntityRequestDto {

    @NotEmpty(message = "employeeId cannot be Empty")
    @Size(min = 3, max = 100, message = "employeeId length must be less than 100 and more than 3 chracters")
    @Pattern(regexp = "^[a-zA-Z0-9\\s]+$", message = "Only alphabets, numbers, and spaces are allowed in employeeId")
    private String employeeId;

    @Max(value = 9999999999l, message = "mobileNumber should be less thant 10000000000")
    @Min(value = 6000000000l, message = "mobile number should be more than 5999999999")
    private long mobileNumber;

    @Email(message = "email should be a valid email address")
    @NotEmpty(message = "email cannot be Empty")
    @Size(min = 3, max = 100, message = "email length must be less than 100 and more than 3 characters")
    private String email;

    @NotEmpty(message = "contactName cannot be Empty")
    @Pattern(regexp = "^[a-zA-Z0-9\\s]+$", message = "Only alphabets, numbers, and spaces are allowed in contacttName")
    @Size(min = 3, max = 50, message = "contactName length must be less than 50 and more than 3 characters")
    private String contactName;

    @NotEmpty(message = "communicationName cannot be Empty")
    @Pattern(regexp = "^[a-zA-Z0-9\\s]+$", message = "Only alphabets, numbers, and spaces are allowed in communicationName")
    @Size(min = 3, max = 50, message = "communicationName length must be less than 50 and more than 3 characters")
    private String communicationName;

    @Valid
    private DropDownDto department;
}
