package com.dreamsol.services;

import com.dreamsol.dtos.requestDtos.PlantRequestDto;
import com.dreamsol.dtos.responseDtos.PlantResponseDto;
import org.springframework.http.ResponseEntity;

public interface PlantService {
    ResponseEntity<PlantResponseDto> createPlant(PlantRequestDto plantRequestDto);

    ResponseEntity<PlantResponseDto> updatePlant(Long id, PlantRequestDto plantRequestDto);

    ResponseEntity<PlantResponseDto> getPlantById(Long id);

    ResponseEntity<?> getPlants(String plantName, int pageSize, int page, String sortBy, String SortDirection,
            String status,
            Long unitId);

    ResponseEntity<?> deletePlant(Long id);

    ResponseEntity<?> downloadPlantDataAsExcel();

    ResponseEntity<?> downloadPlantExcelSample() throws java.io.IOException;

    ResponseEntity<?> getDropDown();

}
