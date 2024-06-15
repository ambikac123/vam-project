package com.dreamsol.services;

import com.dreamsol.dtos.requestDtos.PurposeRequestDto;
import com.dreamsol.dtos.responseDtos.DropDownDto;
import com.dreamsol.dtos.responseDtos.PurposeResponseDto;
import com.dreamsol.entites.Department;
import com.dreamsol.entites.Purpose;
import com.dreamsol.entites.User;
import com.dreamsol.exceptions.ResourceNotFoundException;
import com.dreamsol.repositories.DepartmentRepository;
import com.dreamsol.repositories.PurposeRepository;
import com.dreamsol.repositories.UnitRepository;
import com.dreamsol.repositories.UserRepository;
import com.dreamsol.securities.JwtUtil;
import com.dreamsol.utility.DtoUtilities;
import com.dreamsol.utility.ExcelUtility;

import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PurposeService {

    private final PurposeRepository purposeRepository;
    private final ExcelUtility excelUtility;
    private final JwtUtil jwtUtil;
    private final UnitRepository unitRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final DtoUtilities utility;

    public ResponseEntity<PurposeResponseDto> createPurpose(PurposeRequestDto purposeRequestDto) {
        // Check if the unit exists
        unitRepository.findById(purposeRequestDto.getUnitId())
                .orElseThrow(() -> new RuntimeException(
                        "Unit with this id does not exist : " + purposeRequestDto.getUnitId()));
        User user = null;
        Department department = null;
        LocalTime alTime = null;
        if (purposeRequestDto.isAlert()) {
            user = userRepository.findById(purposeRequestDto.getUserId()).orElseThrow(() -> new RuntimeException(
                    "User with this id does not exist : " + purposeRequestDto.getUserId()));

            department = departmentRepository.findById(purposeRequestDto.getUserId())
                    .orElseThrow(() -> new RuntimeException(
                            "Department with this id does not exist : " + purposeRequestDto.getDepartmentId()));

            try {
                alTime = LocalTime.parse(purposeRequestDto.getAlertTime());
            } catch (Exception e) {
                throw new RuntimeException("Select a Valid Time in this format HH:MM:SS");
            }
        }
        Purpose purpose = DtoUtilities.purposeRequestDtoToPurpose(purposeRequestDto);
        purpose.setCreatedBy(jwtUtil.getCurrentLoginUser());
        purpose.setUpdatedBy(jwtUtil.getCurrentLoginUser());
        purpose.setStatus(true);
        purpose.setUser(user);
        purpose.setDepartment(department);
        purpose.setAlertTime(alTime);
        Purpose savedPurpose = purposeRepository.save(purpose);
        PurposeResponseDto res = DtoUtilities.purposeToPurposeResponseDto(savedPurpose);
        if (savedPurpose.getUser() != null) {
            res.setUser(utility.userToUserResponseDto(savedPurpose.getUser()));
        }
        return ResponseEntity.ok().body(res);
    }

    public ResponseEntity<PurposeResponseDto> updatePurpose(Long id, PurposeRequestDto purposeRequestDto) {
        Purpose existingPurpose = purposeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purpose", "Id", id));
        // Check if the unit exists
        unitRepository.findById(purposeRequestDto.getUnitId())
                .orElseThrow(() -> new RuntimeException(
                        "Unit with this id does not exist : " + purposeRequestDto.getUnitId()));
        User user = null;
        Department department = null;
        LocalTime alTime = null;
        if (purposeRequestDto.isAlert()) {
            user = userRepository.findById(purposeRequestDto.getUserId()).orElseThrow(() -> new RuntimeException(
                    "User with this id does not exist : " + purposeRequestDto.getUserId()));

            department = departmentRepository.findById(purposeRequestDto.getUserId())
                    .orElseThrow(() -> new RuntimeException(
                            "Department with this id does not exist : " + purposeRequestDto.getDepartmentId()));

            try {
                alTime = LocalTime.parse(purposeRequestDto.getAlertTime());
            } catch (Exception e) {
                throw new RuntimeException("Select a Valid Time in this format HH:MM:SS");
            }

        }

        Purpose updatedPurpose = DtoUtilities.purposeRequestDtoToPurpose(existingPurpose, purposeRequestDto);
        updatedPurpose.setUpdatedBy(jwtUtil.getCurrentLoginUser());
        if (purposeRequestDto.isAlert()) {
            updatedPurpose.setUser(user);
            updatedPurpose.setDepartment(department);
            updatedPurpose.setAlertTime(alTime);
        }
        Purpose savedPurpose = purposeRepository.save(updatedPurpose);
        PurposeResponseDto res = DtoUtilities.purposeToPurposeResponseDto(updatedPurpose);
        if (savedPurpose.getUser() != null) {
            res.setUser(utility.userToUserResponseDto(savedPurpose.getUser()));
        }
        return ResponseEntity.ok().body(res);
    }

    public ResponseEntity<PurposeResponseDto> getPurposeById(Long id) {
        Purpose purpose = purposeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purpose", "Id", id));
        return ResponseEntity.ok().body(DtoUtilities.purposeToPurposeResponseDto(purpose));
    }

    public ResponseEntity<?> getPurposes(String purposeFor, int pageSize, int page, String sortBy, String sortDirection,
                                         String status,
                                         Long unitId) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("Asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        PageRequest pageRequest = PageRequest.of(page, pageSize, Sort.by(direction, sortBy));
        Boolean statusBoolean = status != null ? Boolean.parseBoolean(status) : null;

        Page<Purpose> purposePage = purposeRepository.findByStatusAndUnitIdAndPurposeNameIgnoreCase(statusBoolean,
                unitId,
                purposeFor, pageRequest);

        Page<PurposeResponseDto> purposeResponseDtos = purposePage.map(DtoUtilities::purposeToPurposeResponseDto);
        return ResponseEntity.ok(purposeResponseDtos);
    }

    public ResponseEntity<?> deletePurpose(Long id) {
        Purpose purpose = purposeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purpose", "Id", id));
        if (purpose.isStatus()) {
            purpose.setStatus(false);
            purpose.setUpdatedAt(LocalDateTime.now());
            purpose.setUpdatedBy(jwtUtil.getCurrentLoginUser());

            purposeRepository.save(purpose);
            return ResponseEntity.ok().body("Purpose has been deleted");
        } else {
            throw new ResourceNotFoundException("Purpose", "Id", id);
        }
    }

    public ResponseEntity<?> downloadPurposeDataAsExcel(String status, Long unitId, String purposeName) {
        try {
            Boolean statusBoolean = status != null ? Boolean.parseBoolean(status) : null;

            List<Purpose> purposeList = purposeRepository.findByStatusAndUnitIdAndPurposeNameIgnoreCase(statusBoolean,
                    unitId, purposeName);
            if (purposeList.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No purposes available!");
            }

            List<PurposeResponseDto> purposeResDtoList = purposeList.stream()
                    .map(DtoUtilities::purposeToPurposeResponseDto)
                    .collect(Collectors.toList());

            String fileName = "purpose_excel_data.xlsx";
            String sheetName = fileName.substring(0, fileName.indexOf('.'));
            Resource resource = excelUtility.downloadDataAsExcel(purposeResDtoList, sheetName);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName)
                    .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error! " + e);
        }
    }

    public ResponseEntity<?> downloadPurposeExcelSample() throws IOException {
        String fileName = "purpose_excel_sample.xlsx";
        String sheetName = fileName.substring(0, fileName.indexOf('.'));
        Resource resource = excelUtility.downloadExcelSample(PurposeRequestDto.class, sheetName);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(resource);
    }

    public ResponseEntity<?> getDropDown() {
        List<Purpose> purposes = purposeRepository.findAll();
        return ResponseEntity.ok(purposes.stream().map(this::purposeToDropDownRes).collect(Collectors.toList()));
    }

    private DropDownDto purposeToDropDownRes(Purpose purpose) {
        DropDownDto dto = new DropDownDto();
        dto.setId(purpose.getId());
        dto.setName(purpose.getPurposeFor());
        return dto;
    }
}