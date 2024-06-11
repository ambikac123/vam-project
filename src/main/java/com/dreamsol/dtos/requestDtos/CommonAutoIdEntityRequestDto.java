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
public class CommonAutoIdEntityRequestDto
{

    private boolean status=true;

    private Long unitId;
}
