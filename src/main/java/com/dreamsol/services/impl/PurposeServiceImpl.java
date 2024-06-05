package com.dreamsol.services.impl;

import com.dreamsol.dtos.requestDtos.PurposeRequestDto;
import com.dreamsol.dtos.responseDtos.PurposeResponseDto;
import com.dreamsol.entites.Purpose;
import com.dreamsol.exceptions.ResourceNotFoundException;
import com.dreamsol.repositories.PurposeRepository;
import com.dreamsol.services.PurposeService;
import com.dreamsol.utility.DtoUtilities;
import com.dreamsol.utility.ExcelUtility;

import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PurposeServiceImpl implements PurposeService {

    private final PurposeRepository purposeRepository;
    private final ExcelUtility excelUtility;

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

    @Override
    public ResponseEntity<?> downloadPurposeDataAsExcel() {
        try {
            List<Purpose> purposeList = purposeRepository.findAll();
            if (purposeList.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No purposes available!");
            }

            List<PurposeResponseDto> purposeResDtoList = purposeList.stream()
                    .map(DtoUtilities::purposeToPurposeResponseDto)
                    .collect(Collectors.toList());

            String fileName = "purpose_excel_data.xlsx";
            String sheetName = fileName.substring(0, fileName.indexOf('.'));
            Resource resource = excelUtility.downloadDataAsExcel(purposeResDtoList, sheetName);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName)
                    .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error! " + e);
        }
    }

    @Override
    public ResponseEntity<?> downloadPurposeExcelSample() throws IOException {
        String fileName = "purpose_excel_sample.xlsx";
        String sheetName = fileName.substring(0, fileName.indexOf('.'));
        Resource resource = excelUtility.downloadExcelSample(PurposeRequestDto.class, sheetName);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(resource);
    }
}
