package com.dreamsol.services;

import com.dreamsol.dtos.requestDtos.PlantRequestDto;
import com.dreamsol.dtos.responseDtos.PlantResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface PlantService {
    PlantResponseDto createPlant(PlantRequestDto plantRequestDto);

    PlantResponseDto updatePlant(Long id, PlantRequestDto plantRequestDto);

    PlantResponseDto getPlantById(Long id);

    Page<PlantResponseDto> getPlants(Pageable pageable, String status, Long unitId);

    void deletePlant(Long id);

    ResponseEntity<?> downloadPlantDataAsExcel();

    ResponseEntity<?> downloadPlantExcelSample() throws java.io.IOException;
}
