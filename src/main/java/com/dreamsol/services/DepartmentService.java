package com.dreamsol.services;

import com.dreamsol.dtos.requestDtos.DepartmentRequestDto;
import com.dreamsol.dtos.responseDtos.DepartmentResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DepartmentService {
    DepartmentResponseDto createDepartment(DepartmentRequestDto departmentRequestDto);

    DepartmentResponseDto updateDepartment(Long id, DepartmentRequestDto departmentRequestDto);

    DepartmentResponseDto getDepartmentById(Long id);

    Page<DepartmentResponseDto> getDepartments(Pageable pageable, String search);

    void deleteDepartment(Long id);
}
