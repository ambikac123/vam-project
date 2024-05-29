package com.dreamsol.dtos.requestDtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import javax.validation.constraints.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VehicleLicenceReqDto extends CommonAutoIdEntityRequestDto {

    @NotEmpty(message = "Owner name must be provided.")
    @Size(min = 3, max = 50, message = "Driver name should be between 3 and 50 characters.")
    @Pattern(regexp = "^[A-Za-z]+(?:[\\s'][A-Za-z]+)*$", message = "Driver name should only contain alphabets and spaces.")
    @Schema(description = "Name of the driver", example = "John Doe")
    private String vehicleOwner;

    @NotEmpty(message = "Vehicle number must be provided.")
    @Size(min = 5, max = 20, message = "Vehicle number should be between 5 and 20 characters.")
    @Pattern(regexp = "^[A-Za-z0-9-]+$", message = "Vehicle number should only contain alphabets, numbers, and hyphens.")
    @Schema(description = "Vehicle number", example = "AB-123-CD-4567")
    private String vehicleNumber;

    @NotEmpty(message = "Vehicle type must be provided.")
    @Size(min = 3, max = 30, message = "Vehicle type should be between 3 and 30 characters.")
    @Pattern(regexp = "^[A-Za-z\\s]+$", message = "Vehicle type should only contain alphabets and spaces.")
    @Schema(description = "Type of the vehicle", example = "Sedan")
    private String vehicleType;

    @NotNull(message = "Insurance date is required.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Schema(description = "Insurance date of the vehicle", example = "2023-12-31")
    private LocalDate insuranceDate;

    @NotNull(message = "PUC date is required.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Schema(description = "PUC date of the vehicle", example = "2023-12-31")
    private LocalDate pucDate;

    @NotNull(message = "Registration date is required.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Schema(description = "Registration date of the vehicle", example = "2023-12-31")
    private LocalDate registrationDate;

    @NotEmpty(message = "Brief must be provided.")
    @Size(min = 10, max = 200, message = "Brief should be between 10 and 200 characters.")
    @Schema(description = "Brief description about the driver", example = "Experienced driver with a clean record.")
    private String brief;

}
