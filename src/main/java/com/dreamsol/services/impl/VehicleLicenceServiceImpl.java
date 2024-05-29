package com.dreamsol.services.impl;

import com.dreamsol.dtos.requestDtos.VehicleLicenceReqDto;
import com.dreamsol.dtos.responseDtos.ApiResponse;
import com.dreamsol.dtos.responseDtos.VehicleLicenceResDto;
import com.dreamsol.entites.VehicleLicence;
import com.dreamsol.entites.VehicleLicenceAttachment;
import com.dreamsol.exceptions.ResourceNotFoundException;
import com.dreamsol.repositories.VehicleLicenceAttachmentRepo;
import com.dreamsol.repositories.VehicleLicenceRepo;
import com.dreamsol.services.VehicleLicenceService;
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

@Service
@RequiredArgsConstructor
public class VehicleLicenceServiceImpl implements VehicleLicenceService {

    private final VehicleLicenceRepo vehicleLicenceRepo;

    private final DtoUtilities dtoUtilities;

    private final FileService fileService;

    private final VehicleLicenceAttachmentRepo vehicleLicenceAttachmentRepo;

    @Override
    public ResponseEntity<?> addLicence(VehicleLicenceReqDto vehicleLicenceReqDto,
                                        MultipartFile pucFile,
                                        MultipartFile insuranceFile,
                                        MultipartFile registrationFile,
                                        String path) {
        try {
            if (vehicleLicenceRepo.existsByVehicleNumber(vehicleLicenceReqDto.getVehicleNumber())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse("Vehicle number already exists.", false));
            }

            VehicleLicence vehicleLicence = dtoUtilities.vehicleLicenceDtoToVehicleLicence(vehicleLicenceReqDto);

            if (pucFile != null && !pucFile.isEmpty()) {
                VehicleLicenceAttachment pucAttachment = uploadFile(pucFile, path);
                vehicleLicence.setPucAttachment(pucAttachment);
            }

            if (insuranceFile != null && !insuranceFile.isEmpty()) {
                VehicleLicenceAttachment insuranceAttachment = uploadFile(insuranceFile, path);
                vehicleLicence.setInsuranceAttachment(insuranceAttachment);
            }

            if (registrationFile != null && !registrationFile.isEmpty()) {
                VehicleLicenceAttachment registrationAttachment = uploadFile(registrationFile, path);
                vehicleLicence.setRegistrationAttachment(registrationAttachment);
            }

            vehicleLicenceRepo.save(vehicleLicence);

            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse("New Vehicle Licence created successfully!", true));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse("Failed to add Vehicle Licence.", false));
        }
    }


    @Override
    public ResponseEntity<?> deleteLicence(Long licenceId) {
        VehicleLicence vehicleLicence = vehicleLicenceRepo.findById(licenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle Licence", "Id", licenceId));

        if (!vehicleLicence.isStatus()) {
            throw new ResourceNotFoundException("Vehicle Licence", "Id", licenceId);
        }

        vehicleLicence.setStatus(false);

        vehicleLicenceRepo.save(vehicleLicence);

        return ResponseEntity.ok(new ApiResponse("Licence deleted successfully!", true));
    }

    @Override
    public ResponseEntity<?> updateLicence(VehicleLicenceReqDto vehicleLicenceReqDto, Long licenceId,
                                           MultipartFile pucFile, MultipartFile insuranceFile, MultipartFile registrationFile,
                                           String path) {
        VehicleLicence vehicleLicence = vehicleLicenceRepo.findById(licenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle Licence", "Id", licenceId));

        BeanUtils.copyProperties(vehicleLicenceReqDto, vehicleLicence, "id", "vehicleNumber");

        try {
            if (pucFile != null && !pucFile.isEmpty()) {
                VehicleLicenceAttachment pucAttachment = vehicleLicence.getPucAttachment();
                if (pucAttachment != null) {
                    uploadFile(pucFile, path, pucAttachment, vehicleLicenceReqDto);
                } else {
                    pucAttachment = uploadFile(pucFile, path);
                    vehicleLicence.setPucAttachment(pucAttachment);
                }
            }

            if (insuranceFile != null && !insuranceFile.isEmpty()) {
                VehicleLicenceAttachment insuranceAttachment = vehicleLicence.getInsuranceAttachment();
                if (insuranceAttachment != null) {
                    uploadFile(insuranceFile, path, insuranceAttachment, vehicleLicenceReqDto);
                } else {
                    insuranceAttachment = uploadFile(insuranceFile, path);
                    vehicleLicence.setInsuranceAttachment(insuranceAttachment);
                }
            }

            if (registrationFile != null && !registrationFile.isEmpty()) {
                VehicleLicenceAttachment registrationAttachment = vehicleLicence.getRegistrationAttachment();
                if (registrationAttachment != null) {
                    uploadFile(registrationFile, path, registrationAttachment, vehicleLicenceReqDto);
                } else {
                    registrationAttachment = uploadFile(registrationFile, path);
                    vehicleLicence.setRegistrationAttachment(registrationAttachment);
                }
            }

            vehicleLicenceRepo.save(vehicleLicence);

            return ResponseEntity.ok(new ApiResponse("Licence updated successfully!", true));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse("Failed to update Licence.", false));
        }
    }

    @Override
    public ResponseEntity<?> fetchById(Long licenceId) {
        VehicleLicence vehicleLicence = vehicleLicenceRepo.findById(licenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle Licence", "Id", licenceId));
        return ResponseEntity.ok(dtoUtilities.vehicleLicenceToVehicleLicenceDto(vehicleLicence));
    }

    @Override
    public ResponseEntity<Page<VehicleLicenceResDto>> fetchAllVehicles(String search, int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<VehicleLicence> vehicleLicences;

        if (search != null && !search.isEmpty()) {
            vehicleLicences = vehicleLicenceRepo.findByVehicleOwnerContainingIgnoreCase(search, pageable);
        } else {
            vehicleLicences = vehicleLicenceRepo.findAll(pageable);
        }

        Page<VehicleLicenceResDto> vehicleLicenceResDtoPage = vehicleLicences.map(dtoUtilities::vehicleLicenceToVehicleLicenceDto);

        return ResponseEntity.ok(vehicleLicenceResDtoPage);
    }

    public ResponseEntity<Resource> getFile(String fileName, String uploadDir) {
        try {
            VehicleLicenceAttachment vehicleLicenceAttachment = vehicleLicenceAttachmentRepo.findByGeneratedFileName(fileName)
                    .orElseThrow(ResourceNotFoundException::new);

            Resource resource = new ByteArrayResource(fileService.getFile(uploadDir, vehicleLicenceAttachment.getGeneratedFileName()));

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(vehicleLicenceAttachment.getFileType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + vehicleLicenceAttachment.getOriginalFileName() + "\"")
                    .body(resource);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    private VehicleLicenceAttachment uploadFile(MultipartFile file, String path) {
        try {
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            if (fileName.contains("..")) {
                throw new Exception("Invalid file Name");
            }
            VehicleLicenceAttachment vehicleLicenceAttachment = new VehicleLicenceAttachment();
            vehicleLicenceAttachment.setOriginalFileName(fileName);
            vehicleLicenceAttachment.setGeneratedFileName(fileService.fileSave(file, path));
            vehicleLicenceAttachment.setFileType(file.getContentType());
            vehicleLicenceAttachment.setCreatedAt(LocalDateTime.now());
            return vehicleLicenceAttachment;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return new VehicleLicenceAttachment();
    }

    private void uploadFile(MultipartFile file, String path, VehicleLicenceAttachment existingAttachment, VehicleLicenceReqDto vehicleLicenceReqDto) {
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
                existingAttachment = new VehicleLicenceAttachment();
            }

            existingAttachment.setOriginalFileName(fileName);
            existingAttachment.setGeneratedFileName(generatedFileName);
            existingAttachment.setFileType(file.getContentType());
            existingAttachment.setUpdatedAt(LocalDateTime.now());
            existingAttachment.setUpdatedBy(vehicleLicenceReqDto.getVehicleOwner());
        } catch (Exception exception) {
            exception.printStackTrace();
            new VehicleLicenceAttachment();
        }
    }
}
