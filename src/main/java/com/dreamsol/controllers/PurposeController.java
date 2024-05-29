package com.dreamsol.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
        PurposeResponseDto purposeResponseDto = purposeService.createPurpose(purposeRequestDto);
        return ResponseEntity.ok(purposeResponseDto);
    }

    @PutMapping("/update-purpose/{id}")
    public ResponseEntity<PurposeResponseDto> updatePurpose(@PathVariable Long id,
            @Valid @RequestBody PurposeRequestDto purposeRequestDto) {
        PurposeResponseDto purposeResponseDto = purposeService.updatePurpose(id, purposeRequestDto);
        return ResponseEntity.ok(purposeResponseDto);
    }

    @GetMapping("/get-purpose/{id}")
    public ResponseEntity<PurposeResponseDto> getPurposeById(@PathVariable Long id) {
        PurposeResponseDto purposeResponseDto = purposeService.getPurposeById(id);
        return ResponseEntity.ok(purposeResponseDto);
    }

    @GetMapping("/get-all-purposes")
    public ResponseEntity<Page<PurposeResponseDto>> getAllPurposes(
            @PageableDefault(size = 10, sort = "purposeFor", page = 0) Pageable pageable,
            @RequestParam(required = false) String search) {
        Page<PurposeResponseDto> purposeResponseDtos = purposeService.getPurposes(pageable, search);
        return ResponseEntity.ok(purposeResponseDtos);
    }

    @DeleteMapping("/delete-purpose/{id}")
    public ResponseEntity<Void> deletePurpose(@PathVariable Long id) {
        purposeService.deletePurpose(id);
        return ResponseEntity.noContent().build();
    }
}