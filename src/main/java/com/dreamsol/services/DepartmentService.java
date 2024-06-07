package com.dreamsol.services;

import com.dreamsol.dtos.requestDtos.DepartmentRequestDto;
import com.dreamsol.dtos.responseDtos.DepartmentResponseDto;

import org.springframework.http.ResponseEntity;

public interface DepartmentService {
    ResponseEntity<DepartmentResponseDto> createDepartment(DepartmentRequestDto departmentRequestDto);

    ResponseEntity<DepartmentResponseDto> updateDepartment(Long id, DepartmentRequestDto departmentRequestDto);

    ResponseEntity<DepartmentResponseDto> getDepartmentById(Long id);

    ResponseEntity<?> getDepartments(int pageSize, int page, String sortBy, String SortDirection, String status,
            Long unitId);

    ResponseEntity<?> deleteDepartment(Long id);

    ResponseEntity<?> downloadDepartmentDataAsExcel();

    ResponseEntity<?> downloadDepartmentExcelSample() throws java.io.IOException;
}
