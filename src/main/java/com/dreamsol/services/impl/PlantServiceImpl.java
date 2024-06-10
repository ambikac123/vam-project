package com.dreamsol.services.impl;

import com.dreamsol.dtos.requestDtos.PlantRequestDto;
import com.dreamsol.dtos.responseDtos.DropDownDto;
import com.dreamsol.dtos.responseDtos.PlantResponseDto;
import com.dreamsol.entites.Plant;
import com.dreamsol.repositories.PlantRepository;
import com.dreamsol.securities.JwtUtil;
import com.dreamsol.services.PlantService;
import com.dreamsol.exceptions.ResourceNotFoundException;
import com.dreamsol.utility.DtoUtilities;
import com.dreamsol.utility.ExcelUtility;

import lombok.RequiredArgsConstructor;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
    private final JwtUtil jwtUtil;

    @Override
    public ResponseEntity<PlantResponseDto> createPlant(PlantRequestDto plantRequestDto) {
        Plant plant = DtoUtilities.plantRequestDtoToPlant(plantRequestDto);

        Optional<Plant> dbPlant = plantRepository.findByPlantNameIgnoreCase(plantRequestDto.getPlantName());
        if (dbPlant.isPresent()) {
            throw new RuntimeException("Plant with name " + plantRequestDto.getPlantName() + " already Exist");
        }
        plant.setCreatedBy(jwtUtil.getCurrentLoginUser());
        plant.setUpdatedBy(jwtUtil.getCurrentLoginUser());
        plant.setStatus(true);
        Plant savedPlant = plantRepository.save(plant);
        return ResponseEntity.ok(DtoUtilities.plantToPlantResponseDto(savedPlant));
    }

    @Override
    public ResponseEntity<PlantResponseDto> updatePlant(Long id, PlantRequestDto plantRequestDto) {
        Plant plant = plantRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Plant", "Id", id));
        Plant updatedPlant = DtoUtilities.plantRequestDtoToPlant(plant, plantRequestDto);
        updatedPlant.setUpdatedBy(jwtUtil.getCurrentLoginUser());
        updatedPlant = plantRepository.save(updatedPlant);
        return ResponseEntity.ok(DtoUtilities.plantToPlantResponseDto(updatedPlant));
    }

    @Override
    public ResponseEntity<PlantResponseDto> getPlantById(Long id) {
        Plant plant = plantRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Plant", "Id", id));
        return ResponseEntity.ok(DtoUtilities.plantToPlantResponseDto(plant));
    }

    @Override
    public ResponseEntity<?> getPlants(String plantName, int pageSize, int page, String sortBy, String sortDirection,
            String status,
            Long unitId) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("Asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        PageRequest pageRequest = PageRequest.of(page, pageSize, Sort.by(direction, sortBy));
        Boolean statusBoolean = status != null ? Boolean.parseBoolean(status) : null;

        Page<Plant> plantsPage = plantRepository.findByStatusAndUnitIdAndPlantName(statusBoolean, unitId,
                plantName, pageRequest);

        Page<PlantResponseDto> plantResponseDtos = plantsPage.map(DtoUtilities::plantToPlantResponseDto);
        return ResponseEntity.ok(plantResponseDtos);

    }

    @Override
    public ResponseEntity<?> deletePlant(Long id) {
        Plant plant = plantRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Plant", "Id", id));

        if (plant.isStatus()) {
            plant.setStatus(false);
            plant.setUpdatedAt(LocalDateTime.now());
            plant.setUpdatedBy(jwtUtil.getCurrentLoginUser());
            plantRepository.save(plant);
            return ResponseEntity.ok().body("Plant has been deleted");
        } else {
            throw new ResourceNotFoundException("Plant", "Id", id);
        }
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

    public ResponseEntity<?> getDropDown() {
        List<Plant> plants = plantRepository.findAll();
        return ResponseEntity.ok(plants.stream().map(this::plantToDropDownRes).collect(Collectors.toList()));
    }

    private DropDownDto plantToDropDownRes(Plant Plant) {
        DropDownDto dto = new DropDownDto();
        dto.setId(Plant.getId());
        dto.setName(Plant.getPlantName());
        return dto;
    }
}
