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
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class PlantServiceImpl implements PlantService {

    private final PlantRepository plantRepository;

    @Override
    public PlantResponseDto createPlant(PlantRequestDto plantRequestDto) {
        Plant plant = DtoUtilities.plantRequestDtoToPlant(plantRequestDto);
        plantRepository.findByPlantNameIgnoreCase(plantRequestDto.getPlantName()).orElseThrow(
                () -> new RuntimeException("Plant with name " + plantRequestDto.getPlantName() + " already Exist"));
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
    public Page<PlantResponseDto> getPlants(Pageable pageable, String search) {
        LocalDateTime parsedDateTime = null;
        boolean parsedStatus = false;
        if (search != null) {
            // Attempt to parse LocalDateTime and boolean from search string
            try {
                parsedDateTime = LocalDateTime.parse(search, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            } catch (Exception ignored) {
                // Parsing failed, continue
            }

            try {
                parsedStatus = Boolean.parseBoolean(search);
            } catch (Exception ignored) {
                // Parsing failed its ok
            }
            // Search using parsed values
            return plantRepository
                    .findByPlantNameContainingIgnoreCaseOrPlantBriefContainingIgnoreCaseOrCreatedByContainingIgnoreCaseOrUpdatedByContainingIgnoreCaseOrStatusOrCreatedAtOrUpdatedAt(
                            search, search, search, search, parsedStatus, parsedDateTime, parsedDateTime, pageable)
                    .map(DtoUtilities::plantToPlantResponseDto);
        } else {
            return plantRepository.findAll(pageable).map(DtoUtilities::plantToPlantResponseDto);
        }
    }

    @Override
    public void deletePlant(Long id) {
        Plant plant = plantRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Plant", "Id", id));
        plant.setStatus(false);
        plant.setUpdatedAt(LocalDateTime.now());
        plantRepository.save(plant);
    }
}
