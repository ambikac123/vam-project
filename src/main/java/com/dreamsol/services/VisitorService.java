package com.dreamsol.services;

import org.springframework.stereotype.Service;

import com.dreamsol.dtos.requestDtos.VisitorRequestDto;
import com.dreamsol.dtos.responseDtos.VisitorResponseDto;
import com.dreamsol.entites.Department;
import com.dreamsol.entites.Purpose;
import com.dreamsol.entites.User;
import com.dreamsol.entites.Visitor;
import com.dreamsol.exceptions.ResourceNotFoundException;
import com.dreamsol.repositories.VisitorRepository;
import com.dreamsol.repositories.DepartmentRepository;
import com.dreamsol.repositories.PurposeRepository;
import com.dreamsol.repositories.UserRepository;
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

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VisitorService {

    private final VisitorRepository visitorRepository;
    private final DepartmentRepository departmentRepository;
    private final PurposeRepository purposeRepository;
    private final UserRepository userRepository;
    private final ExcelUtility excelUtility;
    private final JwtUtil jwtUtil;
    private final DtoUtilities utilities;

    public ResponseEntity<VisitorResponseDto> createVisitor(VisitorRequestDto visitorRequestDto) {
        Visitor visitor = DtoUtilities.visitorRequestDtoToVisitor(visitorRequestDto);
        User user = userRepository.findById(visitorRequestDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not Found with id : " + visitorRequestDto.getUserId()));
        Department department = departmentRepository.findById(visitorRequestDto.getDepartmentId()).orElseThrow(
                () -> new RuntimeException("Department not Found with id : " + visitorRequestDto.getDepartmentId()));
        Purpose purpose = purposeRepository.findById(visitorRequestDto.getPurposeId()).orElseThrow(
                () -> new RuntimeException("Purpose not Found with id : " + visitorRequestDto.getPurposeId()));
        visitor.setDepartment(department);
        visitor.setUser(user);
        visitor.setPurpose(purpose);
        visitor.setCreatedBy(jwtUtil.getCurrentLoginUser());
        visitor.setUpdatedBy(jwtUtil.getCurrentLoginUser());
        visitor.setStatus(true);
        // saving Visitor Entry into DB
        Visitor savedVisitor = visitorRepository.save(visitor);
        VisitorResponseDto res = DtoUtilities.visitorToVisitorResponseDto(savedVisitor);
        res.setUser(utilities.userToUserResponseDto(user));
        return ResponseEntity.ok().body(res);
    }

    public ResponseEntity<VisitorResponseDto> updateVisitor(Long id, VisitorRequestDto visitorRequestDto) {
        Visitor existingVisitor = visitorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Visitor", "Id", id));
        User user = userRepository.findById(visitorRequestDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not Found with id : " + visitorRequestDto.getUserId()));
        Department department = departmentRepository.findById(visitorRequestDto.getDepartmentId()).orElseThrow(
                () -> new RuntimeException("Department not Found with id : " + visitorRequestDto.getDepartmentId()));
        Purpose purpose = purposeRepository.findById(visitorRequestDto.getPurposeId()).orElseThrow(
                () -> new RuntimeException("Purpose not Found with id : " + visitorRequestDto.getPurposeId()));
        existingVisitor.setDepartment(department);
        existingVisitor.setUser(user);
        existingVisitor.setPurpose(purpose);
        Visitor updatedVisitor = DtoUtilities.visitorRequestDtoToVisitor(existingVisitor, visitorRequestDto);
        updatedVisitor.setUpdatedBy(jwtUtil.getCurrentLoginUser());
        // Saving Updated Visitor into DB
        updatedVisitor = visitorRepository.save(updatedVisitor);
        VisitorResponseDto res = DtoUtilities.visitorToVisitorResponseDto(updatedVisitor);
        res.setUser(utilities.userToUserResponseDto(user));
        return ResponseEntity.ok().body(res);
    }

    public ResponseEntity<VisitorResponseDto> getVisitorById(Long id) {
        Visitor visitor = visitorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Visitor", "Id", id));
        VisitorResponseDto res = DtoUtilities.visitorToVisitorResponseDto(visitor);
        res.setUser(utilities.userToUserResponseDto(visitor.getUser()));
        return ResponseEntity.ok().body(res);
    }

    public ResponseEntity<?> deleteVisitor(Long id) {
        Visitor visitor = visitorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Visitor", "Id", id));

        if (visitor.isStatus()) {
            visitor.setStatus(false);
            visitor.setUpdatedAt(LocalDateTime.now());
            visitor.setUpdatedBy(jwtUtil.getCurrentLoginUser());

            visitorRepository.save(visitor);
            return ResponseEntity.ok().body("Visitor has been deleted");
        } else {
            throw new ResourceNotFoundException("Visitor", "Id", id);
        }
    }

    public ResponseEntity<?> getVisitors(int pageSize, int page, String sortBy, String sortDirection, String status,
            Long unitId, Long employeeId, Long purposeId, Long departmentId) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("Asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        PageRequest pageRequest = PageRequest.of(page, pageSize, Sort.by(direction, sortBy));

        Boolean statusBoolean = status != null ? Boolean.parseBoolean(status) : null;

        Page<Visitor> visitorsPage = visitorRepository.findByEmployeeIdAndPurposeIdAndDepartmentIdAndUnitIdAndStatus(
                employeeId, purposeId, departmentId, unitId, statusBoolean, pageRequest);

        Page<VisitorResponseDto> visitorResponseDtos = visitorsPage
                .map((visitor) -> {
                    VisitorResponseDto dto = DtoUtilities.visitorToVisitorResponseDto(visitor);
                    dto.setUser(utilities.userToUserResponseDto(visitor.getUser()));
                    return dto;
                });
        return ResponseEntity.ok(visitorResponseDtos);
    }

    public ResponseEntity<?> downloadVisitorDataAsExcel() throws java.io.IOException {
        try {
            List<Visitor> visitorList = visitorRepository.findAll();
            if (visitorList.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No purposes available!");
            }

            List<VisitorResponseDto> visitorResDtoList = visitorList.stream()
                    .map((visitor) -> {
                        VisitorResponseDto dto = DtoUtilities.visitorToVisitorResponseDto(visitor);
                        dto.setUser(utilities.userToUserResponseDto(visitor.getUser()));
                        return dto;
                    }).toList();

            String fileName = "visitor_excel_data.xlsx";
            String sheetName = fileName.substring(0, fileName.indexOf('.'));
            Resource resource = excelUtility.downloadDataAsExcel(visitorResDtoList, sheetName);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName)
                    .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error! " + e);
        }
    }
}
