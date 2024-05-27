package com.dreamsol.dtos.responseDtos;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.MappedSuperclass;

@Getter
@Setter
@MappedSuperclass
public class CommonAutoIdEntityResponseDto {

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String createdBy;

    private String updatedBy;

    private boolean status;
}
