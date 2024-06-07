package com.dreamsol.controllers;

import java.io.IOException;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.dreamsol.dtos.requestDtos.UnitRequestDto;
import com.dreamsol.dtos.responseDtos.UnitResponseDto;
import com.dreamsol.services.UnitService;

@RestController
@RequestMapping("/api/units")
//@CrossOrigin(origins = "http://192.168.1.8:3000")
public class UnitController {

    @Autowired
    private UnitService unitService;

   // @CrossOrigin(origins = "http://192.168.1.8:3000")
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

   // @CrossOrigin(origins = "http://192.168.1.8:3000")
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

    @GetMapping(value = "/download-excel-data", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<?> downloadExcelData() {
        return unitService.downloadDataAsExcel();
    }

    @GetMapping(value = "/download-excel-sample", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<?> downloadExcelSample() throws IOException {
        return unitService.downloadExcelSample();
    }
}
