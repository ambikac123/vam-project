package com.dreamsol.services;

import com.dreamsol.dtos.requestDtos.PurposeRequestDto;
import com.dreamsol.dtos.responseDtos.PurposeResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface PurposeService {
    PurposeResponseDto createPurpose(PurposeRequestDto purposeRequestDto);

    PurposeResponseDto updatePurpose(Long id, PurposeRequestDto purposeRequestDto);

    PurposeResponseDto getPurposeById(Long id);

    Page<PurposeResponseDto> getPurposes(Pageable pageable, String status, Long unitId, String purposeFor);

    void deletePurpose(Long id);

    ResponseEntity<?> downloadPurposeDataAsExcel();

    ResponseEntity<?> downloadPurposeExcelSample() throws java.io.IOException;
}
