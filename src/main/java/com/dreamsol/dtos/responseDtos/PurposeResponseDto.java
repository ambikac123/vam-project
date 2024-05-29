package com.dreamsol.dtos.responseDtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PurposeResponseDto extends CommonAutoIdEntityResponseDto {
    private String purposeFor;
    private String purposeBrief;
    private boolean alert;
}
