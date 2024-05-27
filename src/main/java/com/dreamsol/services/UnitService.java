package com.dreamsol.services;

import com.dreamsol.dtos.requestDtos.UnitRequestDto;
import com.dreamsol.dtos.responseDtos.UnitResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UnitService {
    UnitResponseDto createUnit(UnitRequestDto unitRequestDto);

    UnitResponseDto updateUnit(Long id, UnitRequestDto unitRequestDto);

    UnitResponseDto getUnitById(Long id);

    Page<UnitResponseDto> getUnits(Pageable pageable, String search);

    void deleteUnit(Long id);
}
