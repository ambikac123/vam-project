package com.dreamsol.services;

import com.dreamsol.dtos.requestDtos.DepartmentRequestDto;
import com.dreamsol.dtos.responseDtos.DepartmentResponseDto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface DepartmentService {
    DepartmentResponseDto createDepartment(DepartmentRequestDto departmentRequestDto);

    DepartmentResponseDto updateDepartment(Long id, DepartmentRequestDto departmentRequestDto);

    DepartmentResponseDto getDepartmentById(Long id);

    Page<DepartmentResponseDto> getDepartments(Pageable pageable, String status, Long unitId, String departmentName);

    void deleteDepartment(Long id);

    ResponseEntity<?> downloadDepartmentDataAsExcel();

    ResponseEntity<?> downloadDepartmentExcelSample() throws java.io.IOException;
}
