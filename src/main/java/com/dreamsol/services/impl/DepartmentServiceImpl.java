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

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    private final UnitRepository unitRepository;

    @Override
    public DepartmentResponseDto createDepartment(DepartmentRequestDto departmentRequestDto) {
        // Check if department already exists
        Optional<Department> dbDepartment = departmentRepository
                .findByDepartmentNameIgnoreCaseAndDepartmentCodeIgnoreCase(departmentRequestDto.getDepartmentName(),
                        departmentRequestDto.getDepartmentCode());
        if (dbDepartment.isPresent()) {
            throw new RuntimeException(
                    "Department Already exists with name : " + departmentRequestDto.getDepartmentName()
                            + " , and Code : " + departmentRequestDto.getDepartmentCode());
        }
        // Check if the unit exists
        Unit unit = unitRepository.findByUnitNameIgnoreCaseAndUnitIp(
                departmentRequestDto.getUnit().getUnitName(), departmentRequestDto.getUnit().getUnitIp())
                .orElseThrow(() -> new RuntimeException("Unit with this details already exists UnitName: "
                        + departmentRequestDto.getUnit().getUnitName() + " ,UnitIp: "
                        + departmentRequestDto.getUnit().getUnitIp()));

        Department department = DtoUtilities.departmentRequestDtoToDepartment(departmentRequestDto);
        department.setUnit(unit);

        Department savedDepartment = departmentRepository.save(department);
        return DtoUtilities.departmentToDepartmentResponseDto(savedDepartment);
    }

    @Override
    public DepartmentResponseDto updateDepartment(Long id, DepartmentRequestDto departmentRequestDto) {
        // Find the existing department
        Department existingDepartment = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department", "Id", id));

        // Check if the new department code already exists (excluding the current
        // department)
        Optional<Department> departmentWithSameCode = departmentRepository
                .findByDepartmentCode(departmentRequestDto.getDepartmentCode());
        if (departmentWithSameCode.isPresent() && !departmentWithSameCode.get().getId().equals(id)) {
            throw new RuntimeException("Department code already exists");
        }

        // Check if the unit exists
        Unit unit = unitRepository.findByUnitNameIgnoreCaseAndUnitIp(
                departmentRequestDto.getUnit().getUnitName(), departmentRequestDto.getUnit().getUnitIp())
                .orElseThrow(() -> new RuntimeException("Unit with this details already exists UnitName: "
                        + departmentRequestDto.getUnit().getUnitName() + " ,UnitIp: "
                        + departmentRequestDto.getUnit().getUnitIp()));

        // Update department fields
        Department updatedDepartment = DtoUtilities.departmentRequestDtoToDepartment(existingDepartment,
                departmentRequestDto);
        updatedDepartment.setUnit(unit);

        // Save the updated department
        updatedDepartment = departmentRepository.save(updatedDepartment);
        return DtoUtilities.departmentToDepartmentResponseDto(updatedDepartment);
    }

    @Override
    public DepartmentResponseDto getDepartmentById(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department", "Id", id));
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
                .orElseThrow(() -> new ResourceNotFoundException("Department", "Id", id));
        department.setStatus(false);
        department.setUpdatedAt(LocalDateTime.now());
        departmentRepository.save(department);
    }
}
