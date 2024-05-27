package com.dreamsol.controllers;

import com.dreamsol.dtos.requestDtos.PlantRequestDto;
import com.dreamsol.dtos.responseDtos.PlantResponseDto;
import com.dreamsol.services.PlantService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/plants")
public class PlantController {

    @Autowired
    private PlantService plantService;

    @PostMapping("/create-plant")
    public ResponseEntity<PlantResponseDto> createPlant(@Valid @RequestBody PlantRequestDto plantRequestDto) {
        PlantResponseDto plantResponseDto = plantService.createPlant(plantRequestDto);
        return ResponseEntity.ok(plantResponseDto);
    }

    @PutMapping("update-plant/{id}")
    public ResponseEntity<PlantResponseDto> updatePlant(@PathVariable Long id,
            @Valid @RequestBody PlantRequestDto plantRequestDto) {
        PlantResponseDto plantResponseDto = plantService.updatePlant(id, plantRequestDto);
        return ResponseEntity.ok(plantResponseDto);
    }

    @GetMapping("get-plant/{id}")
    public ResponseEntity<PlantResponseDto> getPlantById(@PathVariable Long id) {
        PlantResponseDto plantResponseDto = plantService.getPlantById(id);
        return ResponseEntity.ok(plantResponseDto);
    }

    @GetMapping("get-all-plants")
    public ResponseEntity<Page<PlantResponseDto>> getAllPlants(
            @PageableDefault(size = 10, sort = "plantName", page = 0) Pageable pageable,
            @RequestParam(required = false) String search) {
        Page<PlantResponseDto> plantResponseDtos = plantService.getPlants(pageable, search);
        return ResponseEntity.ok(plantResponseDtos);
    }

    @DeleteMapping("delete-plant/{id}")
    public ResponseEntity<Void> deletePlant(@PathVariable Long id) {
        plantService.deletePlant(id);
        return ResponseEntity.noContent().build();
    }
}
