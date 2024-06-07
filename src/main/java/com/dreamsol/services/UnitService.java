package com.dreamsol.services;

import com.dreamsol.dtos.requestDtos.UnitRequestDto;
import com.dreamsol.dtos.responseDtos.UnitResponseDto;

import io.jsonwebtoken.io.IOException;

import org.springframework.http.ResponseEntity;

public interface UnitService {
    ResponseEntity<UnitResponseDto> createUnit(UnitRequestDto unitRequestDto);

    ResponseEntity<UnitResponseDto> updateUnit(Long id, UnitRequestDto unitRequestDto);

    ResponseEntity<UnitResponseDto> getUnitById(Long id);

    ResponseEntity<?> getUnits(int pageSize, int page, String sortBy, String SortDirection, String status);

    ResponseEntity<?> deleteUnit(Long id);

    ResponseEntity<?> downloadDataAsExcel();

    ResponseEntity<?> downloadExcelSample() throws IOException, java.io.IOException;

}
