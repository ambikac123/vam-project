package com.dreamsol.dtos.requestDtos;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VisitorRequestDto extends CommonAutoIdEntityRequestDto {

    @NotEmpty(message = "visitorName cannot be Empty")
    @Pattern(regexp = "^[a-zA-Z0-9\\s]+$", message = "Only alphabets, numbers, and spaces are allowed in visitorName")
    @Size(min = 3, max = 50, message = "visitorName length must be between 3 and 50 characters")
    private String visitorName;

    @Pattern(regexp = "^[a-zA-Z0-9\\s]+$", message = "Only alphabets, numbers, and spaces are allowed in visitorCompany")
    private String visitorCompany;

    @Pattern(regexp = "^[a-zA-Z0-9,\\s]+$", message = "Only alphabets, numbers, spaces, and commas are allowed in visitorAddress")
    private String visitorAddress;

    private Long userId;
    private Long purposeId;
    private Long departmentId;

    @Pattern(regexp = "^[a-zA-Z0-9,\\s]+$", message = "Only alphabets, numbers, spaces, and commas are allowed in possessionAllowed")
    private String possessionAllowed;

    @Pattern(regexp = "^[a-zA-Z0-9\\s]+$", message = "Only alphabets, numbers, and spaces are allowed in visitorCardNumber")
    private String visitorCardNumber;

    @Pattern(regexp = "^[a-zA-Z0-9\\s]+$", message = "Only alphabets, numbers, and spaces are allowed in vehicleNumber")
    private String vehicleNumber;

    @Pattern(regexp = "^[a-zA-Z0-9,\\s]+$", message = "Only alphabets, numbers, spaces, and commas are allowed in equipments")
    private String equipments;

    private boolean approvalRequired;

    @Size(max = 25, min = 10, message = "Not A Valid Date")
    private String validFrom;

    @Size(max = 25, min = 10, message = "Not A Valid Date")
    private String validTill;
}
