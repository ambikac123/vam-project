package com.dreamsol.services;

import com.dreamsol.dtos.requestDtos.UnitRequestDto;
import com.dreamsol.dtos.responseDtos.UnitResponseDto;

import io.jsonwebtoken.io.IOException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface UnitService {
    UnitResponseDto createUnit(UnitRequestDto unitRequestDto);

    UnitResponseDto updateUnit(Long id, UnitRequestDto unitRequestDto);

    UnitResponseDto getUnitById(Long id);

    Page<UnitResponseDto> getUnits(Pageable pageable, String status);

    void deleteUnit(Long id);

    ResponseEntity<?> downloadDataAsExcel();

    ResponseEntity<?> downloadExcelSample() throws IOException, java.io.IOException;

}
