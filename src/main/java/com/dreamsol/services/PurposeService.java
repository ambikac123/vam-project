package com.dreamsol.services;

import com.dreamsol.dtos.requestDtos.PurposeRequestDto;
import com.dreamsol.dtos.responseDtos.PurposeResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PurposeService {
    PurposeResponseDto createPurpose(PurposeRequestDto purposeRequestDto);

    PurposeResponseDto updatePurpose(Long id, PurposeRequestDto purposeRequestDto);

    PurposeResponseDto getPurposeById(Long id);

    Page<PurposeResponseDto> getPurposes(Pageable pageable, String search);

    void deletePurpose(Long id);
}
