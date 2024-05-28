package com.dreamsol.services.impl;

import com.dreamsol.dtos.requestDtos.PurposeRequestDto;
import com.dreamsol.dtos.responseDtos.PurposeResponseDto;
import com.dreamsol.entites.Purpose;
import com.dreamsol.exceptions.ResourceNotFoundException;
import com.dreamsol.repositories.PurposeRepository;
import com.dreamsol.services.PurposeService;
import com.dreamsol.utility.DtoUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class PurposeServiceImpl implements PurposeService {

    @Autowired
    private PurposeRepository purposeRepository;

    @Override
    public PurposeResponseDto createPurpose(PurposeRequestDto purposeRequestDto) {
        Purpose purpose = DtoUtilities.purposeRequestDtoToPurpose(purposeRequestDto);
        Purpose savedPurpose = purposeRepository.save(purpose);
        return DtoUtilities.purposeToPurposeResponseDto(savedPurpose);
    }

    @Override
    public PurposeResponseDto updatePurpose(Long id, PurposeRequestDto purposeRequestDto) {
        Purpose existingPurpose = purposeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purpose not found"));
        Purpose updatedPurpose = DtoUtilities.purposeRequestDtoToPurpose(existingPurpose, purposeRequestDto);
        updatedPurpose = purposeRepository.save(updatedPurpose);
        return DtoUtilities.purposeToPurposeResponseDto(updatedPurpose);
    }

    @Override
    public PurposeResponseDto getPurposeById(Long id) {
        Purpose purpose = purposeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purpose not found"));
        return DtoUtilities.purposeToPurposeResponseDto(purpose);
    }

    @Override
    public Page<PurposeResponseDto> getPurposes(Pageable pageable, String search) {
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
                // Parsing failed, continue
            }

            // Search using parsed values
            return purposeRepository
                    .findByPurposeForContainingIgnoreCaseOrPurposeBriefContainingIgnoreCaseOrCreatedByContainingIgnoreCaseOrUpdatedByContainingIgnoreCaseOrStatusOrCreatedAtOrUpdatedAt(
                            search, search, search, search, parsedStatus, parsedDateTime, parsedDateTime, pageable)
                    .map(DtoUtilities::purposeToPurposeResponseDto);
        } else {
            return purposeRepository.findAll(pageable).map(DtoUtilities::purposeToPurposeResponseDto);
        }
    }

    @Override
    public void deletePurpose(Long id) {
        Purpose purpose = purposeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purpose not found"));
        purpose.setStatus(false);
        purposeRepository.save(purpose);
    }
}
