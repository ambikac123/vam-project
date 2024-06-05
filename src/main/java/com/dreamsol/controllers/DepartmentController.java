package com.dreamsol.controllers;

import java.io.IOException;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.dreamsol.dtos.requestDtos.DepartmentRequestDto;
import com.dreamsol.dtos.responseDtos.DepartmentResponseDto;
import com.dreamsol.services.DepartmentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @PostMapping("/create-department")
    public ResponseEntity<DepartmentResponseDto> createDepartment(
            @Valid @RequestBody DepartmentRequestDto departmentRequestDto) {
        DepartmentResponseDto departmentResponseDto = departmentService.createDepartment(departmentRequestDto);
        return ResponseEntity.ok(departmentResponseDto);
    }

    @PutMapping("/update-department/{id}")
    public ResponseEntity<DepartmentResponseDto> updateDepartment(@PathVariable Long id,
            @Valid @RequestBody DepartmentRequestDto departmentRequestDto) {
        DepartmentResponseDto departmentResponseDto = departmentService.updateDepartment(id, departmentRequestDto);
        return ResponseEntity.ok(departmentResponseDto);
    }

    @GetMapping("/get-department/{id}")
    public ResponseEntity<DepartmentResponseDto> getDepartmentById(@PathVariable Long id) {
        DepartmentResponseDto departmentResponseDto = departmentService.getDepartmentById(id);
        return ResponseEntity.ok(departmentResponseDto);
    }

    @GetMapping("/get-all-departments")
    public ResponseEntity<Page<DepartmentResponseDto>> getAllDepartments(
            @PageableDefault(size = 10, sort = "departmentName", page = 0) Pageable pageable,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long unitId,
            @RequestParam(required = false) String departmentName) {
        Page<DepartmentResponseDto> departmentResponseDtos = departmentService.getDepartments(pageable, status, unitId,
                departmentName);
        return ResponseEntity.ok(departmentResponseDtos);
    }

    @DeleteMapping("/delete-department/{id}")
    public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/download-excel-data", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<?> downloadExcelData() {
        return departmentService.downloadDepartmentDataAsExcel();
    }

    @GetMapping(value = "/download-excel-sample", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<?> downloadExcelSample() throws IOException {
        return departmentService.downloadDepartmentExcelSample();
    }
}
