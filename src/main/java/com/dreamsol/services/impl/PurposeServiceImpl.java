package com.dreamsol.services.impl;

import com.dreamsol.dtos.requestDtos.PurposeRequestDto;
import com.dreamsol.dtos.responseDtos.PurposeResponseDto;
import com.dreamsol.entites.Purpose;
import com.dreamsol.exceptions.ResourceNotFoundException;
import com.dreamsol.repositories.PurposeRepository;
import com.dreamsol.services.PurposeService;
import com.dreamsol.utility.DtoUtilities;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PurposeServiceImpl implements PurposeService {

    private final PurposeRepository purposeRepository;

    @Override
    public PurposeResponseDto createPurpose(PurposeRequestDto purposeRequestDto) {
        Purpose purpose = DtoUtilities.purposeRequestDtoToPurpose(purposeRequestDto);
        Purpose savedPurpose = purposeRepository.save(purpose);
        return DtoUtilities.purposeToPurposeResponseDto(savedPurpose);
    }

    @Override
    public PurposeResponseDto updatePurpose(Long id, PurposeRequestDto purposeRequestDto) {
        Purpose existingPurpose = purposeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purpose", "Id", id));
        Purpose updatedPurpose = DtoUtilities.purposeRequestDtoToPurpose(existingPurpose, purposeRequestDto);
        updatedPurpose = purposeRepository.save(updatedPurpose);
        return DtoUtilities.purposeToPurposeResponseDto(updatedPurpose);
    }

    @Override
    public PurposeResponseDto getPurposeById(Long id) {
        Purpose purpose = purposeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purpose", "Id", id));
        return DtoUtilities.purposeToPurposeResponseDto(purpose);
    }

    @Override
    public Page<PurposeResponseDto> getPurposes(Pageable pageable, String status, Long unitId, String purposeFor) {
        boolean bool = false;
        if (status != null) {
            try {
                bool = Boolean.parseBoolean(status);
            } catch (Exception e) {
                return purposeRepository.findByPurposeForAndUnitId(pageable, purposeFor, unitId)
                        .map(DtoUtilities::purposeToPurposeResponseDto);
            }
            return purposeRepository.findByPurposeForAndUnitIdAndStatus(pageable, purposeFor, unitId, bool)
                    .map(DtoUtilities::purposeToPurposeResponseDto);
        }

        return purposeRepository.findAll(pageable).map(DtoUtilities::purposeToPurposeResponseDto);

    }

    @Override
    public void deletePurpose(Long id) {
        Purpose purpose = purposeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purpose", "Id", id));
        purpose.setStatus(false);
        purposeRepository.save(purpose);
    }
}
