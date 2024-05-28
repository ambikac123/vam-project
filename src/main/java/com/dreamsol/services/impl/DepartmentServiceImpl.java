package com.dreamsol.services.impl;

import com.dreamsol.dtos.requestDtos.DepartmentRequestDto;
import com.dreamsol.dtos.responseDtos.DepartmentResponseDto;
import com.dreamsol.entites.Department;
import com.dreamsol.entites.Unit;
import com.dreamsol.exceptions.ResourceNotFoundException;
import com.dreamsol.repositories.DepartmentRepository;
import com.dreamsol.repositories.UnitRepository;
import com.dreamsol.services.DepartmentService;
import com.dreamsol.utility.DtoUtilities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private UnitRepository unitRepository;

    @Override
    public DepartmentResponseDto createDepartment(DepartmentRequestDto departmentRequestDto) {
        // Check if department code already exists
        Optional<Department> existingDepartment = departmentRepository
                .findByDepartmentCode(departmentRequestDto.getDepartmentCode());
        if (existingDepartment.isPresent()) {
            throw new RuntimeException("Department code already exists");
        }

        // Check if the unit exists
        Optional<Unit> unit = unitRepository.findByUnitName(departmentRequestDto.getUnit().getUnitName());
        if (!unit.isPresent()) {
            throw new RuntimeException("Choose a valid Unit");
        }

        Department department = DtoUtilities.departmentRequestDtoToDepartment(departmentRequestDto);
        department.setUnit(unit.get());

        Department savedDepartment = departmentRepository.save(department);
        return DtoUtilities.departmentToDepartmentResponseDto(savedDepartment);
    }

    @Override
    public DepartmentResponseDto updateDepartment(Long id, DepartmentRequestDto departmentRequestDto) {
        // Find the existing department
        Department existingDepartment = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));

        // Check if the new department code already exists (excluding the current
        // department)
        Optional<Department> departmentWithSameCode = departmentRepository
                .findByDepartmentCode(departmentRequestDto.getDepartmentCode());
        if (departmentWithSameCode.isPresent() && !departmentWithSameCode.get().getId().equals(id)) {
            throw new RuntimeException("Department code already exists");
        }

        // Check if the unit exists
        Optional<Unit> unit = unitRepository.findByUnitName(departmentRequestDto.getUnit().getUnitName());
        if (!unit.isPresent()) {
            throw new RuntimeException("Choose a valid unit");
        }

        // Update department fields
        Department updatedDepartment = DtoUtilities.departmentRequestDtoToDepartment(existingDepartment,
                departmentRequestDto);
        updatedDepartment.setUnit(unit.get());

        // Save the updated department
        updatedDepartment = departmentRepository.save(updatedDepartment);
        return DtoUtilities.departmentToDepartmentResponseDto(updatedDepartment);
    }

    @Override
    public DepartmentResponseDto getDepartmentById(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
        return DtoUtilities.departmentToDepartmentResponseDto(department);
    }

    @Override
    public Page<DepartmentResponseDto> getDepartments(Pageable pageable, String search) {
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
            return departmentRepository
                    .findByDepartmentNameContainingIgnoreCaseOrDepartmentCodeContainingIgnoreCaseOrCreatedByContainingIgnoreCaseOrUpdatedByContainingIgnoreCaseOrStatusOrCreatedAtOrUpdatedAt(
                            search, search, search, search, parsedStatus, parsedDateTime, parsedDateTime, pageable)
                    .map(DtoUtilities::departmentToDepartmentResponseDto);
        } else {
            return departmentRepository.findAll(pageable).map(DtoUtilities::departmentToDepartmentResponseDto);
        }
    }

    @Override
    public void deleteDepartment(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
        department.setStatus(false);
        department.setUpdatedAt(LocalDateTime.now());
        departmentRepository.save(department);
    }
}
