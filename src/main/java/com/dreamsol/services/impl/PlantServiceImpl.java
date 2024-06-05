package com.dreamsol.services.impl;

import com.dreamsol.dtos.requestDtos.PlantRequestDto;
import com.dreamsol.dtos.responseDtos.PlantResponseDto;
import com.dreamsol.entites.Plant;
import com.dreamsol.repositories.PlantRepository;
import com.dreamsol.services.PlantService;
import com.dreamsol.exceptions.ResourceNotFoundException;
import com.dreamsol.utility.DtoUtilities;
import com.dreamsol.utility.ExcelUtility;

import lombok.RequiredArgsConstructor;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlantServiceImpl implements PlantService {

    private final PlantRepository plantRepository;
    private final ExcelUtility excelUtility;

    @Override
    public PlantResponseDto createPlant(PlantRequestDto plantRequestDto) {
        Plant plant = DtoUtilities.plantRequestDtoToPlant(plantRequestDto);

        Optional<Plant> dbPlant = plantRepository.findByPlantNameIgnoreCase(plantRequestDto.getPlantName());
        if (dbPlant.isPresent()) {
            throw new RuntimeException("Plant with name " + plantRequestDto.getPlantName() + " already Exist");
        }

        Plant savedPlant = plantRepository.save(plant);
        return DtoUtilities.plantToPlantResponseDto(savedPlant);
    }

    @Override
    public PlantResponseDto updatePlant(Long id, PlantRequestDto plantRequestDto) {
        Plant plant = plantRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Plant", "Id", id));
        Plant updatedPlant = DtoUtilities.plantRequestDtoToPlant(plant, plantRequestDto);
        updatedPlant = plantRepository.save(updatedPlant);
        return DtoUtilities.plantToPlantResponseDto(updatedPlant);
    }

    @Override
    public PlantResponseDto getPlantById(Long id) {
        Plant plant = plantRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Plant", "Id", id));
        return DtoUtilities.plantToPlantResponseDto(plant);
    }

    @Override
    public Page<PlantResponseDto> getPlants(Pageable pageable, String status, Long unitId) {

        boolean bool = false;
        if (status != null) {
            try {
                bool = Boolean.parseBoolean(status);
            } catch (Exception e) {
                return plantRepository.findByUnitId(pageable, unitId)
                        .map(DtoUtilities::plantToPlantResponseDto);
            }
            return plantRepository.findByStatusAndUnitId(pageable, bool, unitId)
                    .map(DtoUtilities::plantToPlantResponseDto);
        }

        return plantRepository.findAll(pageable)
                .map(DtoUtilities::plantToPlantResponseDto);
    }

    @Override
    public void deletePlant(Long id) {
        Plant plant = plantRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Plant", "Id", id));
        plant.setStatus(false);
        plant.setUpdatedAt(LocalDateTime.now());
        plantRepository.save(plant);
    }

    @Override
    public ResponseEntity<?> downloadPlantDataAsExcel() {
        try {
            List<Plant> plantList = plantRepository.findAll();
            if (plantList.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No plants available!");
            }

            List<PlantResponseDto> plantResDtoList = plantList.stream()
                    .map(DtoUtilities::plantToPlantResponseDto)
                    .collect(Collectors.toList());

            String fileName = "plant_excel_data.xlsx";
            String sheetName = fileName.substring(0, fileName.indexOf('.'));
            Resource resource = excelUtility.downloadDataAsExcel(plantResDtoList, sheetName);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName)
                    .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error! " + e);
        }
    }

    @Override
    public ResponseEntity<?> downloadPlantExcelSample() throws IOException {
        String fileName = "plant_excel_sample.xlsx";
        String sheetName = fileName.substring(0, fileName.indexOf('.'));
        Resource resource = excelUtility.downloadExcelSample(PlantRequestDto.class, sheetName);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(resource);
    }
}
