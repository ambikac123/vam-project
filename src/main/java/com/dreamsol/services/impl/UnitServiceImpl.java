package com.dreamsol.services.impl;

import com.dreamsol.dtos.requestDtos.UnitRequestDto;
import com.dreamsol.dtos.responseDtos.UnitResponseDto;
import com.dreamsol.entites.Unit;
import com.dreamsol.exceptions.ResourceNotFoundException;
import com.dreamsol.repositories.UnitRepository;
import com.dreamsol.services.UnitService;
import com.dreamsol.utility.DtoUtilities;
import com.dreamsol.utility.ExcelUtility;

import lombok.RequiredArgsConstructor;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UnitServiceImpl implements UnitService {

    private final UnitRepository unitRepository;
    private final ExcelUtility excelUtility;

    @Override
    public UnitResponseDto createUnit(UnitRequestDto unitRequestDto) {
        Unit unit = DtoUtilities.unitRequestDtoToUnit(unitRequestDto);
        Optional<Unit> dbUnit = unitRepository.findByUnitNameIgnoreCaseOrUnitIp(unit.getUnitName(),
                unit.getUnitIp());
        if (dbUnit.isPresent()) {
            throw new RuntimeException("Unit with this details already exists UnitName: "
                    + unitRequestDto.getUnitName() + " ,UnitIp: " + unitRequestDto.getUnitIp());
        } else {
            Unit savedUnit = unitRepository.save(unit);
            return DtoUtilities.unitToUnitResponseDto(savedUnit);
        }
    }

    @Override
    public UnitResponseDto updateUnit(Long id, UnitRequestDto unitRequestDto) {
        Unit unit = unitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unit", "Id", id));
        Unit updatedUnit = DtoUtilities.unitRequestDtoToUnit(unit, unitRequestDto);
        updatedUnit = unitRepository.save(updatedUnit);
        return DtoUtilities.unitToUnitResponseDto(updatedUnit);
    }

    @Override
    public UnitResponseDto getUnitById(Long id) {
        Unit unit = unitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unit", "Id", id));
        return DtoUtilities.unitToUnitResponseDto(unit);
    }

    @Override
    public Page<UnitResponseDto> getUnits(Pageable pageable, String status) {
        boolean bool = false;
        if (status != null) {
            try {
                bool = Boolean.parseBoolean(status);
            } catch (Exception e) {
                return unitRepository
                        .findAll(pageable)
                        .map(DtoUtilities::unitToUnitResponseDto);
            }
            return unitRepository
                    .findByStatus(pageable, bool)
                    .map(DtoUtilities::unitToUnitResponseDto);
        }
        return unitRepository
                .findAll(pageable)
                .map(DtoUtilities::unitToUnitResponseDto);
    }

    @Override
    public void deleteUnit(Long id) {
        Unit unit = unitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unit", "Id", id));
        unit.setStatus(false);
        unit.setUpdatedAt(LocalDateTime.now());
        unitRepository.save(unit);
    }

    @Override
    public ResponseEntity<?> downloadDataAsExcel() {
        try {
            List<Unit> unitList = unitRepository.findAll();
            if (unitList.isEmpty())
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No units available!");
            List<UnitResponseDto> UnitResDtoList = unitList.stream().map(DtoUtilities::unitToUnitResponseDto)
                    .collect(Collectors.toList());
            String fileName = "unit_excel_data.xlsx";
            String sheetName = fileName.substring(0, fileName.indexOf('.'));
            Resource resource = excelUtility.downloadDataAsExcel(UnitResDtoList, sheetName);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName)
                    .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error!" + e);
        }
    }

    @Override
    public ResponseEntity<?> downloadExcelSample() throws IOException {
        String fileName = "unit_excel_sample.xlsx";
        String sheetName = fileName.substring(0, fileName.indexOf('.'));
        Resource resource = excelUtility.downloadExcelSample(UnitRequestDto.class, sheetName);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(resource);
    }
}
