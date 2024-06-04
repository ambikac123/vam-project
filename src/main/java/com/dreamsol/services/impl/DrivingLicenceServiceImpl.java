package com.dreamsol.services.impl;

import com.dreamsol.dtos.requestDtos.DrivingLicenceReqDto;
import com.dreamsol.dtos.responseDtos.ApiResponse;
import com.dreamsol.dtos.responseDtos.DrivingLicenceResDto;
import com.dreamsol.dtos.responseDtos.ExcelValidateDataResponseDto;
import com.dreamsol.entites.DrivingLicence;
import com.dreamsol.entites.DrivingLicenceAttachment;
import com.dreamsol.exceptions.ResourceNotFoundException;
import com.dreamsol.repositories.DrivingLicenceRepo;
import com.dreamsol.repositories.DrivingLicenceAttachmentRepo;
import com.dreamsol.services.DrivingLicenceService;
import com.dreamsol.utility.DtoUtilities;
import com.dreamsol.utility.ExcelUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DrivingLicenceServiceImpl implements DrivingLicenceService {
    private final DrivingLicenceRepo drivingLicenceRepo;

    private final DtoUtilities dtoUtilities;

    private final FileService fileService;

    private final DrivingLicenceAttachmentRepo licenceAttachmentRepo;

    private final ExcelUtility excelUtility;

    @Override
    public ResponseEntity<?> addLicence(DrivingLicenceReqDto drivingLicenceReqDto, MultipartFile file, String path) {
        Optional<DrivingLicence> existingLicence = drivingLicenceRepo.findByLicence(drivingLicenceReqDto.getLicence());
        Optional<DrivingLicence> existingMobile = drivingLicenceRepo.findByDriverMobile(drivingLicenceReqDto.getDriverMobile());

        if (existingLicence.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("Licence already exists!", false));
        } else if (existingMobile.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("Mobile number already exists!", false));
        } else {
            DrivingLicence drivingLicence = dtoUtilities.licenceDtoToLicence(drivingLicenceReqDto);
            drivingLicence.setFile(uploadFile(file, path));
            DrivingLicence savedDriving = drivingLicenceRepo.save(drivingLicence);
            return ResponseEntity.status(HttpStatus.CREATED).body(dtoUtilities.licenceToLicenceDto(savedDriving));
        }
    }

    @Override
    public ResponseEntity<?> deleteLicence(Long licenceId) {
        DrivingLicence drivingLicence = drivingLicenceRepo.findById(licenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Driving Licence", "Id", licenceId));

        if (!drivingLicence.isStatus()) {
            throw new ResourceNotFoundException("Driving Licence", "Id", licenceId);
        }

        drivingLicence.setStatus(false);

        drivingLicenceRepo.save(drivingLicence);

        return ResponseEntity.ok(new ApiResponse("Licence deleted successfully!", true));
    }

    public ResponseEntity<?> updateLicence(DrivingLicenceReqDto drivingLicenceReqDto, Long licenceId, MultipartFile file, String path) {
        DrivingLicence drivingLicence = drivingLicenceRepo.findById(licenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Driving Licence", "Id", licenceId));

        BeanUtils.copyProperties(drivingLicenceReqDto, drivingLicence, "id", "licence");
        DrivingLicenceAttachment existingAttachment = drivingLicence.getFile();

        if (existingAttachment != null) {
            uploadFile(file, path, existingAttachment, drivingLicenceReqDto);
        } else {
            DrivingLicenceAttachment newAttachment = uploadFile(file, path);
            drivingLicence.setFile(newAttachment);
        }
        DrivingLicence updatedDriving = drivingLicenceRepo.save(drivingLicence);

        return ResponseEntity.ok(dtoUtilities.licenceToLicenceDto(updatedDriving));
    }

    @Override
    public ResponseEntity<?> fetchById(Long licenceId) {
        DrivingLicence drivingLicence = drivingLicenceRepo.findById(licenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Driving Licence", "Id", licenceId));
        return ResponseEntity.ok(dtoUtilities.licenceToLicenceDto(drivingLicence));
    }


    public ResponseEntity<Page<DrivingLicenceResDto>> fetchAllDrivers(
            String status,
            int page,
            int size,
            String sortBy) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));

        Page<DrivingLicence> drivingLicences;
        boolean bool=false;
        if (status != null) {
            try {
                bool = Boolean.parseBoolean(status);
            } catch (Exception e) {
                drivingLicences = drivingLicenceRepo.findAll(pageable);
            }
            drivingLicences = drivingLicenceRepo.findByStatus(bool, pageable);
        } else {
            drivingLicences = drivingLicenceRepo.findAll(pageable);
        }

        Page<DrivingLicenceResDto> drivingLicenceResDtoPage = drivingLicences.map(dtoUtilities::licenceToLicenceDto);

        return ResponseEntity.ok(drivingLicenceResDtoPage);
    }


    @Override
    public ResponseEntity<Resource> getFile(String fileName, String uploadDir) throws IOException {
        DrivingLicenceAttachment licenceAttachment = licenceAttachmentRepo.findByGeneratedFileName(fileName).orElseThrow(() -> {
            throw new ResourceNotFoundException();
        });
        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType(licenceAttachment.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + licenceAttachment.getOriginalFileName() + "\"")
                .body(new ByteArrayResource(fileService.getFile(uploadDir, licenceAttachment.getGeneratedFileName())));
    }

    @Override
    public ResponseEntity<?> downloadDriverDataAsExcel() {
        try {
            List<DrivingLicence> drivingLicenceList = drivingLicenceRepo.findAll();
            if (drivingLicenceList.isEmpty())
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No users available!");
            List<DrivingLicenceResDto> drivingLicenceResDtoList = drivingLicenceList.stream().map(dtoUtilities::licenceToLicenceDto)
                    .collect(Collectors.toList());
            String fileName = "driver_excel_data.xlsx";
            String sheetName = fileName.substring(0, fileName.indexOf('.'));
            Resource resource = excelUtility.downloadDataAsExcel(drivingLicenceResDtoList, sheetName);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName)
                    .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error!" + e);
        }
    }

    @Override
    public ResponseEntity<?> downloadExcelSample() {
        String fileName = "driver_excel_sample.xlsx";
        String sheetName = fileName.substring(0, fileName.indexOf('.'));
        Resource resource = excelUtility.downloadExcelSample(DrivingLicenceReqDto.class, sheetName);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(resource);
    }

    @Override
    public ResponseEntity<?> validateExcelData(MultipartFile file) {
        try {
            if (excelUtility.isExcelFile(file)) {
                ExcelValidateDataResponseDto validateDataResponse = excelUtility.validateExcelData(file, DrivingLicenceReqDto.class);
                return ResponseEntity.status(HttpStatus.OK).body(validateDataResponse);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Incorrect uploaded file type!");
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private DrivingLicenceAttachment uploadFile(MultipartFile file, String path) {
        try {
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            if (fileName.contains("..")) {
                throw new Exception("Invalid file Name");
            }
            DrivingLicenceAttachment drivingLicenceAttachment = new DrivingLicenceAttachment();
            drivingLicenceAttachment.setOriginalFileName(fileName);
            drivingLicenceAttachment.setGeneratedFileName(fileService.fileSave(file, path));
            drivingLicenceAttachment.setFileType(file.getContentType());
            drivingLicenceAttachment.setCreatedAt(LocalDateTime.now());
            return drivingLicenceAttachment;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return new DrivingLicenceAttachment();
    }

    private void uploadFile(MultipartFile file, String path, DrivingLicenceAttachment existingAttachment, DrivingLicenceReqDto drivingLicenceReqDto) {
        try {
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            if (fileName.contains("..")) {
                throw new Exception("Invalid file Name");
            }

            String generatedFileName = fileService.fileSave(file, path);
            if (generatedFileName == null) {
                throw new Exception("Failed to save file");
            }

            if (existingAttachment == null) {
                existingAttachment = new DrivingLicenceAttachment();
            }

            existingAttachment.setOriginalFileName(fileName);
            existingAttachment.setGeneratedFileName(generatedFileName);
            existingAttachment.setFileType(file.getContentType());
            existingAttachment.setUpdatedAt(LocalDateTime.now());
            existingAttachment.setUpdatedBy(drivingLicenceReqDto.getDriverName());
            //existingAttachment.setCreatedAt(drivingLicenceReqDto.get);

        } catch (Exception exception) {
            exception.printStackTrace();
            new DrivingLicenceAttachment();
        }
    }
}
