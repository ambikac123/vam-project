package com.dreamsol.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.dreamsol.dtos.requestDtos.UnitRequestDto;
import com.dreamsol.dtos.responseDtos.UnitResponseDto;
import com.dreamsol.services.UnitService;

@RestController
@RequestMapping("/api/units")

public class UnitController {

    @Autowired
    private UnitService unitService;

    @PostMapping("/create-unit")
    public ResponseEntity<UnitResponseDto> createUnit(
            @Valid @RequestBody UnitRequestDto unitRequestDto) {
        UnitResponseDto unitResponseDto = unitService.createUnit(unitRequestDto);
        return ResponseEntity.ok(unitResponseDto);
    }

    @PutMapping("/update-unit/{id}")
    public ResponseEntity<UnitResponseDto> updateUnit(@PathVariable Long id,
            @Valid @RequestBody UnitRequestDto unitRequestDto) {
        UnitResponseDto unitResponseDto = unitService.updateUnit(id, unitRequestDto);
        return ResponseEntity.ok(unitResponseDto);
    }

    @GetMapping("/get-unit/{id}")
    public ResponseEntity<UnitResponseDto> getUnitById(@PathVariable Long id) {
        UnitResponseDto unitResponseDto = unitService.getUnitById(id);
        return ResponseEntity.ok(unitResponseDto);
    }

    @GetMapping("/get-all-units")
    public ResponseEntity<Page<UnitResponseDto>> getAllUnits(
            @PageableDefault(size = 10, sort = "unitName", page = 0) Pageable pageable,
            @RequestParam(required = false) String status) {
        Page<UnitResponseDto> unitResponseDtos = unitService.getUnits(pageable, status);
        return ResponseEntity.ok(unitResponseDtos);
    }

    @DeleteMapping("/delete-unit/{id}")
    public ResponseEntity<Void> deleteUnit(@PathVariable Long id) {
        unitService.deleteUnit(id);
        return ResponseEntity.noContent().build();
    }
}
