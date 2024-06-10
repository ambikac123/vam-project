package com.dreamsol.services;

import com.dreamsol.dtos.requestDtos.PurposeRequestDto;
import com.dreamsol.dtos.responseDtos.PurposeResponseDto;
import org.springframework.http.ResponseEntity;

public interface PurposeService {
    ResponseEntity<PurposeResponseDto> createPurpose(PurposeRequestDto purposeRequestDto);

    ResponseEntity<PurposeResponseDto> updatePurpose(Long id, PurposeRequestDto purposeRequestDto);

    ResponseEntity<PurposeResponseDto> getPurposeById(Long id);

    ResponseEntity<?> getPurposes(String purposeFor, int pageSize, int page, String sortBy, String SortDirection,
            String status, Long unitId);

    ResponseEntity<?> deletePurpose(Long id);

    ResponseEntity<?> downloadPurposeDataAsExcel();

    ResponseEntity<?> downloadPurposeExcelSample() throws java.io.IOException;

    ResponseEntity<?> getDropDown();

}
