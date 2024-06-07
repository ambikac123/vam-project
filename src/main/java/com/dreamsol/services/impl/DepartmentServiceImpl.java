package com.dreamsol.services.impl;

import com.dreamsol.dtos.requestDtos.DepartmentRequestDto;
import com.dreamsol.dtos.responseDtos.DepartmentResponseDto;
import com.dreamsol.entites.Department;
import com.dreamsol.exceptions.ResourceNotFoundException;
import com.dreamsol.repositories.DepartmentRepository;
import com.dreamsol.repositories.UnitRepository;
import com.dreamsol.services.DepartmentService;
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
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final UnitRepository unitRepository;
    private final ExcelUtility excelUtility;

    @Override
    public ResponseEntity<DepartmentResponseDto> createDepartment(DepartmentRequestDto departmentRequestDto) {
        // Check if department already exists
        Optional<Department> dbDepartment = departmentRepository
                .findByDepartmentCodeIgnoreCase(departmentRequestDto.getDepartmentCode());
        if (dbDepartment.isPresent()) {
            throw new RuntimeException(
                    "Department Already exists with  Code : " + departmentRequestDto.getDepartmentCode());
        }
        // Check if the unit exists
        unitRepository.findById(departmentRequestDto.getUnitId())
                .orElseThrow(() -> new RuntimeException("Organization with this id does not exist"));

        Department savedDepartment = departmentRepository
                .save(DtoUtilities.departmentRequestDtoToDepartment(departmentRequestDto));
        return ResponseEntity.ok().body(DtoUtilities.departmentToDepartmentResponseDto(savedDepartment));

    }

    @Override
    public ResponseEntity<DepartmentResponseDto> updateDepartment(Long id, DepartmentRequestDto departmentRequestDto) {
        // Find the existing department
        Department existingDepartment = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department", "Id", id));

        Optional<Department> departmentWithSameCode = departmentRepository
                .findByDepartmentCodeIgnoreCase(departmentRequestDto.getDepartmentCode());
        if (departmentWithSameCode.isPresent() && !departmentWithSameCode.get().getId().equals(id)) {
            throw new RuntimeException("Department code already exists");
        }

        // Check if the unit exists
        unitRepository.findById(departmentRequestDto.getUnitId())
                .orElseThrow(() -> new RuntimeException("Organization with this id does not exist"));
        // Update department fields
        Department updatedDepartment = DtoUtilities.departmentRequestDtoToDepartment(existingDepartment,
                departmentRequestDto);

        // Save the updated department
        updatedDepartment = departmentRepository.save(updatedDepartment);
        return ResponseEntity.ok().body(DtoUtilities.departmentToDepartmentResponseDto(updatedDepartment));
    }

    @Override
    public ResponseEntity<DepartmentResponseDto> getDepartmentById(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department", "Id", id));
        return ResponseEntity.ok().body(DtoUtilities.departmentToDepartmentResponseDto(department));
    }

    @Override
    public ResponseEntity<?> deleteDepartment(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department", "Id", id));

        if (department.isStatus()) {
            department.setStatus(false);
            department.setUpdatedAt(LocalDateTime.now());
            departmentRepository.save(department);
            return ResponseEntity.ok().body("Department has been deleted");
        } else {
            throw new ResourceNotFoundException("Department", "Id", id);

        }
    }

    @Override
    public ResponseEntity<?> getDepartments(int pageSize, int page, String sortBy, String sortDirection, String status,
            Long unitId) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("Asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        PageRequest pageRequest = PageRequest.of(page, pageSize, Sort.by(direction, sortBy));
        Boolean statusBoolean = status != null ? Boolean.parseBoolean(status) : null;

        Page<Department> departmentsPage = departmentRepository.findByStatusAndUnitId(statusBoolean, unitId,
                pageRequest);

        Page<DepartmentResponseDto> departmentResponseDtos = departmentsPage
                .map(DtoUtilities::departmentToDepartmentResponseDto);
        return ResponseEntity.ok(departmentResponseDtos);
    }

    @Override
    public ResponseEntity<?> downloadDepartmentDataAsExcel() {
        try {
            List<Department> departmentList = departmentRepository.findAll();
            if (departmentList.isEmpty())
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No departments available!");

            List<DepartmentResponseDto> departmentResDtoList = departmentList.stream()
                    .map(DtoUtilities::departmentToDepartmentResponseDto)
                    .collect(Collectors.toList());

            String fileName = "department_excel_data.xlsx";
            String sheetName = fileName.substring(0, fileName.indexOf('.'));
            Resource resource = excelUtility.downloadDataAsExcel(departmentResDtoList, sheetName);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName)
                    .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error! " + e);
        }
    }

    @Override
    public ResponseEntity<?> downloadDepartmentExcelSample() throws IOException {
        String fileName = "department_excel_sample.xlsx";
        String sheetName = fileName.substring(0, fileName.indexOf('.'));
        Resource resource = excelUtility.downloadExcelSample(DepartmentRequestDto.class, sheetName);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(resource);
    }

}
