package com.dreamsol.services;

import com.dreamsol.dtos.requestDtos.UnitRequestDto;
import com.dreamsol.dtos.responseDtos.DropDownDto;
import com.dreamsol.dtos.responseDtos.UnitResponseDto;
import com.dreamsol.entites.Unit;
import com.dreamsol.exceptions.ResourceNotFoundException;
import com.dreamsol.repositories.UnitRepository;
import com.dreamsol.securities.JwtUtil;
import com.dreamsol.utility.DtoUtilities;
import com.dreamsol.utility.ExcelUtility;

import lombok.RequiredArgsConstructor;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
public class UnitService {

    private final UnitRepository unitRepository;
    private final ExcelUtility excelUtility;
    private final JwtUtil jwtUtil;

    public ResponseEntity<UnitResponseDto> createUnit(UnitRequestDto unitRequestDto) {
        Unit unit = DtoUtilities.unitRequestDtoToUnit(unitRequestDto);
        Optional<Unit> dbUnit = unitRepository.findByUnitNameIgnoreCaseOrUnitIp(unit.getUnitName(),
                unit.getUnitIp());
        if (dbUnit.isPresent()) {
            throw new RuntimeException("Unit with this details already exists UnitName: "
                    + unitRequestDto.getUnitName() + " ,UnitIp: " + unitRequestDto.getUnitIp());
        } else {
            unit.setCreatedBy(jwtUtil.getCurrentLoginUser());
            unit.setUpdatedBy(jwtUtil.getCurrentLoginUser());
            unit.setStatus(true);
            Unit savedUnit = unitRepository.save(unit);
            return ResponseEntity.ok().body(DtoUtilities.unitToUnitResponseDto(savedUnit));
        }
    }

    public ResponseEntity<UnitResponseDto> updateUnit(Long id, UnitRequestDto unitRequestDto) {
        Unit unit = unitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unit", "Id", id));
        Unit updatedUnit = DtoUtilities.unitRequestDtoToUnit(unit, unitRequestDto);
        updatedUnit.setUpdatedBy(jwtUtil.getCurrentLoginUser());
        updatedUnit = unitRepository.save(updatedUnit);
        return ResponseEntity.ok().body(DtoUtilities.unitToUnitResponseDto(updatedUnit));
    }

    public ResponseEntity<UnitResponseDto> getUnitById(Long id) {
        Unit unit = unitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unit", "Id", id));
        return ResponseEntity.ok().body(DtoUtilities.unitToUnitResponseDto(unit));
    }

    public ResponseEntity<Page<UnitResponseDto>> getUnits(int pageSize, int page, String sortBy, String sortDirection,
            String status) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        PageRequest pageRequest = PageRequest.of(page, pageSize, Sort.by(direction, sortBy));

        Boolean statusBoolean = status != null ? Boolean.parseBoolean(status) : null;

        Page<Unit> unitsPage = unitRepository.findByStatus(statusBoolean, pageRequest);

        Page<UnitResponseDto> unitResponseDtos = unitsPage.map(DtoUtilities::unitToUnitResponseDto);
        return ResponseEntity.ok(unitResponseDtos);
    }

    public ResponseEntity<?> deleteUnit(Long id) {
        Unit unit = unitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unit", "Id", id));
        if (unit.isStatus()) {
            unit.setStatus(false);
            unit.setUpdatedAt(LocalDateTime.now());
            unit.setUpdatedBy(jwtUtil.getCurrentLoginUser());
            unitRepository.save(unit);
            return ResponseEntity.ok("Unit Deleted Successfully");
        } else {
            throw new ResourceNotFoundException("Unit", "Id", id);
        }
    }

    public ResponseEntity<?> downloadDataAsExcel(String status) {
        Boolean statusBoolean = status != null ? Boolean.parseBoolean(status) : null;

        try {

            List<Unit> unitList = unitRepository.findByStatus(statusBoolean);
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

    public ResponseEntity<?> downloadExcelSample() throws IOException {
        String fileName = "unit_excel_sample.xlsx";
        String sheetName = fileName.substring(0, fileName.indexOf('.'));
        Resource resource = excelUtility.downloadExcelSample(UnitRequestDto.class, sheetName);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(resource);
    }

    public ResponseEntity<?> getDropDown() {
        List<Unit> units = unitRepository.findAll();
        return ResponseEntity.ok(units.stream().map(unit -> this.unitToDropDownRes(unit)).collect(Collectors.toList()));
    }

    private DropDownDto unitToDropDownRes(Unit unit) {
        DropDownDto dto = new DropDownDto();
        dto.setId(unit.getId());
        dto.setName(unit.getUnitName());
        return dto;
    }
}
