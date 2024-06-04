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
        Optional<Unit> unit = unitRepository.findById(departmentRequestDto.getUnitId());
        if (unit.isPresent()) {

            Department savedDepartment = departmentRepository
                    .save(DtoUtilities.departmentRequestDtoToDepartment(departmentRequestDto));
            return DtoUtilities.departmentToDepartmentResponseDto(savedDepartment);
        } else {
            throw new RuntimeException("Organization with this id does not exist");
        }
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
        Optional<Unit> unit = unitRepository.findById(departmentRequestDto.getUnitId());
        if (!unit.isPresent()) {
            throw new RuntimeException("Organization with this id does not exist");
        }
        // Update department fields
        Department updatedDepartment = DtoUtilities.departmentRequestDtoToDepartment(existingDepartment,
                departmentRequestDto);

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
    public Page<DepartmentResponseDto> getDepartments(Pageable pageable, String status, Long unitId,
            String departmentName) {
        boolean bool = false;
        if (status != null) {
            try {
                bool = Boolean.parseBoolean(status);
            } catch (Exception e) {
                return departmentRepository.findByUnitIdAndDepartmentName(unitId, departmentName, pageable)
                        .map(DtoUtilities::departmentToDepartmentResponseDto);
            }
            return departmentRepository.findByStatusAndUnitIdAndDepartmentName(bool, unitId, departmentName, pageable)
                    .map(DtoUtilities::departmentToDepartmentResponseDto);
        }

        return departmentRepository.findAll(pageable).map(DtoUtilities::departmentToDepartmentResponseDto);

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
