package com.dreamsol.services.impl;

import com.dreamsol.dtos.requestDtos.PlantRequestDto;
import com.dreamsol.dtos.responseDtos.PlantResponseDto;
import com.dreamsol.entites.Plant;
import com.dreamsol.repositories.PlantRepository;
import com.dreamsol.services.PlantService;
import com.dreamsol.exceptions.ResourceNotFoundException;
import com.dreamsol.utility.DtoUtilities;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlantServiceImpl implements PlantService {

    private final PlantRepository plantRepository;

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
}
