package com.dreamsol.services.impl;

import com.dreamsol.dtos.requestDtos.DrivingLicenceReqDto;
import com.dreamsol.dtos.responseDtos.ApiResponse;
import com.dreamsol.dtos.responseDtos.DrivingLicenceResDto;
import com.dreamsol.entites.DrivingLicence;
import com.dreamsol.entites.DrivingLicenceAttachment;
import com.dreamsol.exceptions.ResourceNotFoundException;
import com.dreamsol.repositories.DrivingLicenceRepo;
import com.dreamsol.repositories.DrivingLicenceAttachmentRepo;
import com.dreamsol.services.DrivingLicenceService;
import com.dreamsol.utility.DtoUtilities;
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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DrivingLicenceServiceImpl implements DrivingLicenceService
{
    private final DrivingLicenceRepo drivingLicenceRepo;

    private final DtoUtilities dtoUtilities;

    private final FileService fileService;

    private final DrivingLicenceAttachmentRepo licenceAttachmentRepo;

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
            drivingLicenceRepo.save(drivingLicence);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse("New Driving Licence created successfully!", true));
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

    public ResponseEntity<?> updateLicence(DrivingLicenceReqDto drivingLicenceReqDto, Long licenceId, MultipartFile file,String path) {
        DrivingLicence drivingLicence = drivingLicenceRepo.findById(licenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Driving Licence", "Id", licenceId));

        BeanUtils.copyProperties(drivingLicenceReqDto, drivingLicence, "id", "licence");
        DrivingLicenceAttachment existingAttachment = drivingLicence.getFile();

        if (existingAttachment != null) {
            uploadFile(file, path, existingAttachment,drivingLicenceReqDto);
        } else {
            DrivingLicenceAttachment newAttachment = uploadFile(file, path);
            drivingLicence.setFile(newAttachment);
        }
        drivingLicenceRepo.save(drivingLicence);

        return ResponseEntity.ok(new ApiResponse("Licence updated successfully!", true));
    }

    @Override
    public ResponseEntity<?> fetchById(Long licenceId) {
        DrivingLicence drivingLicence = drivingLicenceRepo.findById(licenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Driving Licence", "Id", licenceId));
        return ResponseEntity.ok(dtoUtilities.licenceToLicenceDto(drivingLicence));
    }

    @Override
    public ResponseEntity<Page<DrivingLicenceResDto>> fetchAllDrivers(String search, int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<DrivingLicence> drivingLicences;

        if (search != null && !search.isEmpty()) {
            drivingLicences = drivingLicenceRepo.findByDriverNameContainingIgnoreCase(search, pageable);
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

//    public ResponseEntity<Resource> getFile(String fileName, String path) throws IOException {
//        LicenceAttachment licenceAttachment = licenceAttachmentRepo.findByGeneratedFileName(fileName).orElseThrow(() -> {
//            throw new ResourceNotFoundException();
//        });
//        return ResponseEntity
//                .ok()
//                .contentType(MediaType.parseMediaType(file.getFileType()))
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getOriginalFileName() + "\"")
//                .body(new ByteArrayResource(fileService.getFile(path, file.getGeneratedFileName())));
//    }
}
