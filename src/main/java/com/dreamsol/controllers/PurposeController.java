package com.dreamsol.controllers;

import java.io.IOException;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.dreamsol.dtos.requestDtos.PurposeRequestDto;
import com.dreamsol.dtos.responseDtos.PurposeResponseDto;
import com.dreamsol.services.PurposeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/purposes")
@RequiredArgsConstructor
public class PurposeController {

    @Autowired
    private final PurposeService purposeService;

    @PostMapping("/create-purpose")
    public ResponseEntity<PurposeResponseDto> createPurpose(
            @Valid @RequestBody PurposeRequestDto purposeRequestDto) {
        return purposeService.createPurpose(purposeRequestDto);
    }

    @PutMapping("/update-purpose/{id}")
    public ResponseEntity<PurposeResponseDto> updatePurpose(@PathVariable Long id,
            @Valid @RequestBody PurposeRequestDto purposeRequestDto) {
        return purposeService.updatePurpose(id, purposeRequestDto);
    }

    @GetMapping("/get-purpose/{id}")
    public ResponseEntity<PurposeResponseDto> getPurposeById(@PathVariable Long id) {
        return purposeService.getPurposeById(id);
    }

    @GetMapping("/get-all-purposes")
    public ResponseEntity<?> getAllPurposes(
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "ASC") String sortDirection,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long unitId,
            @RequestParam(required = false) String purposeFor) {
        return purposeService.getPurposes(purposeFor, pageSize, page, sortBy, sortDirection, status, unitId);
    }

    @DeleteMapping("/delete-purpose/{id}")
    public ResponseEntity<?> deletePurpose(@PathVariable Long id) {
        return purposeService.deletePurpose(id);
    }

    @GetMapping(value = "/download-excel-data", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<?> downloadExcelData() {
        return purposeService.downloadPurposeDataAsExcel();
    }

    @GetMapping(value = "/download-excel-sample", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<?> downloadExcelSample() throws IOException {
        return purposeService.downloadPurposeExcelSample();
    }

    @GetMapping("/drop-down")
    public ResponseEntity<?> getPurposeDropDown() {
        return purposeService.getDropDown();
    }
}
