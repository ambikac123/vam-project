package com.dreamsol.services.impl;

import com.dreamsol.dtos.requestDtos.VehicleEntryReqDto;
import com.dreamsol.dtos.responseDtos.ApiResponse;
import com.dreamsol.dtos.responseDtos.ExcelValidateDataResponseDto;
import com.dreamsol.dtos.responseDtos.VehicleEntryResDto;
import com.dreamsol.entites.*;
import com.dreamsol.exceptions.ResourceNotFoundException;
import com.dreamsol.repositories.*;
import com.dreamsol.securities.JwtUtil;
import com.dreamsol.services.VehicleEntryService;
import com.dreamsol.utility.DtoUtilities;
import com.dreamsol.utility.ExcelUtility;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VehicleEntryServiceImpl implements VehicleEntryService {

    private final VehicleEntryRepository vehicleEntryRepository;

    private final DrivingLicenceRepo drivingLicenceRepo;

    private final VehicleLicenceRepo vehicleLicenceRepo;

    private final PlantRepository plantRepository;

    private final PurposeRepository purposeRepository;

    private final DtoUtilities dtoUtilities;

    private final ExcelUtility excelUtility;

    private final JwtUtil jwtUtil;

    private static final Logger logger = LoggerFactory.getLogger(VehicleEntryServiceImpl.class);

    @Override
    public ResponseEntity<?> addEntry(VehicleEntryReqDto vehicleEntryReqDto) {
        String username = jwtUtil.getCurrentLoginUser();
        if (username == null) {
            logger.info("Unauthenticated user!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthenticated user!");
        }

        try {
            Optional<DrivingLicence> optionalDrivingLicence = drivingLicenceRepo.findByDriverMobile(vehicleEntryReqDto.getDriverMobile());
            DrivingLicence drivingLicence = optionalDrivingLicence.orElseThrow(() -> new NotFoundException("Driver not found with this Mobile Number,Please add the driver first"));

            Optional<VehicleLicence> optionalVehicleLicence = vehicleLicenceRepo.findByVehicleNumber(vehicleEntryReqDto.getVehicleNumber());
            VehicleLicence vehicleLicence = optionalVehicleLicence.orElseThrow(() -> new NotFoundException("Vehicle not found with this vehicle number,Please add the vehicle First"));

            Optional<Plant> optionalPlant = plantRepository.findByPlantNameContainingIgnoreCase(vehicleEntryReqDto.getPlantName());
            Plant plant = optionalPlant.orElseThrow(() -> new NotFoundException("Plant not found with this name,Please add the Plant first"));

            Optional<Purpose> optionalPurpose = purposeRepository.findByPurposeForContainingIgnoreCase(vehicleEntryReqDto.getPurposeFor());
            Purpose purpose = optionalPurpose.orElseThrow(() -> new NotFoundException("Purpose not found with this name,Please add the purpose first"));

            VehicleEntry vehicleEntry = dtoUtilities.vehicleEntryDtoToVehicleEntry(vehicleEntryReqDto, drivingLicence, vehicleLicence, plant, purpose);
            vehicleEntry.setCreatedBy(username);

            VehicleEntry savedVehicleEntry = vehicleEntryRepository.save(vehicleEntry);
            logger.info("Vehicle Entry saved successfully with ID: " + savedVehicleEntry.getId());

            VehicleEntryResDto vehicleEntryResDto = dtoUtilities.vehicleEntryToDto(savedVehicleEntry);
            return ResponseEntity.status(HttpStatus.CREATED).body(vehicleEntryResDto);
        } catch (NotFoundException e) {
            logger.error("NotFoundException: " + e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(), false));
        } catch (Exception e) {
            logger.error("Exception occurred while adding vehicle entry: " + e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse("Failed to add Vehicle Entry.", false));
        }
    }


    @Override
    public ResponseEntity<?> deleteEntry(Long entryId) {
        VehicleEntry vehicleEntry = vehicleEntryRepository.findById(entryId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle Entry", "Id", entryId));

        if (!vehicleEntry.isStatus()) {
            throw new ResourceNotFoundException("Vehicle Entry", "Id", entryId);
        }

        vehicleEntry.setStatus(false);

        vehicleEntryRepository.save(vehicleEntry);

        return ResponseEntity.ok(new ApiResponse("Entry deleted successfully!", true));
    }

    @Override
    public ResponseEntity<?> updateEntry(VehicleEntryReqDto vehicleEntryReqDto, Long entryId) {

            VehicleEntry vehicleEntry = vehicleEntryRepository.findById(entryId)
                    .orElseThrow(() -> new ResourceNotFoundException("Vehicle Entry", "Id", entryId));

        String username = jwtUtil.getCurrentLoginUser();
        if(username == null) {
            logger.info("Unauthenticated user!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthenticated user!");
        }

        try {
            Optional<DrivingLicence> optionalDrivingLicence = drivingLicenceRepo.findByDriverMobile(vehicleEntryReqDto.getDriverMobile());
            DrivingLicence drivingLicence = optionalDrivingLicence.orElseThrow(() -> new NotFoundException("Driver not found with this Mobile Number"));

            Optional<VehicleLicence> optionalVehicleLicence = vehicleLicenceRepo.findByVehicleNumber(vehicleEntryReqDto.getVehicleNumber());
            VehicleLicence vehicleLicence = optionalVehicleLicence.orElseThrow(() -> new NotFoundException("Vehicle not found with this vehicle number"));

            Optional<Plant> optionalPlant = plantRepository.findByPlantNameContainingIgnoreCase(vehicleEntryReqDto.getPlantName());
            Plant plant = optionalPlant.orElseThrow(() -> new NotFoundException("Plant not found with this name"));

            Optional<Purpose> optionalPurpose = purposeRepository.findByPurposeForContainingIgnoreCase(vehicleEntryReqDto.getPurposeFor());
            Purpose purpose = optionalPurpose.orElseThrow(() -> new NotFoundException("Purpose not found with this name"));

            BeanUtils.copyProperties(vehicleEntryReqDto, vehicleEntry, "id");

            vehicleEntry.setUpdatedBy(username);
            vehicleEntry.setDrivingLicence(drivingLicence);
            vehicleEntry.setVehicleLicence(vehicleLicence);
            vehicleEntry.setPlant(plant);
            vehicleEntry.setPurpose(purpose);

            VehicleEntry updatedVehicleEntry = vehicleEntryRepository.save(vehicleEntry);


            VehicleEntryResDto vehicleEntryResDto = dtoUtilities.vehicleEntryToDto(updatedVehicleEntry);

            return ResponseEntity.ok(vehicleEntryResDto);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(), false));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse("Failed to update Entry.", false));
        }
    }


    @Override
    public ResponseEntity<?> fetchById(Long entryId) {
        VehicleEntry vehicleEntry = vehicleEntryRepository.findById(entryId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle Entry", "Id", entryId));
       return ResponseEntity.ok(dtoUtilities.vehicleEntryToDto(vehicleEntry));
    }

    @Override
    public ResponseEntity<Page<VehicleEntryResDto>> fetchAllEntries(
            Long unitId,
            String status,
            int page,
            int size,
            String sortBy) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<VehicleEntry> vehicleEntries;

        if (status != null) {
            try {
                boolean bool = Boolean.parseBoolean(status);
                if (bool && unitId != null) {
                    vehicleEntries = vehicleEntryRepository.findByStatusAndUnitId(bool, unitId, pageable);
                } else {
                    vehicleEntries = vehicleEntryRepository.findByStatus(bool, pageable);
                }
            } catch (Exception e) {
                vehicleEntries = vehicleEntryRepository.findAll(pageable);
            }
        } else if (unitId != null) {
            vehicleEntries = vehicleEntryRepository.findByUnitId(unitId, pageable);
        } else {
            vehicleEntries = vehicleEntryRepository.findAll(pageable);
        }

        Page<VehicleEntryResDto> vehicleEntryResDtoPage = vehicleEntries.map(dtoUtilities::vehicleEntryToDto);

        return ResponseEntity.ok(vehicleEntryResDtoPage);
    }


    @Override
    public ResponseEntity<?> downloadEntryDataAsExcel() {
        try {
            List<VehicleEntry> vehicleEntryList = vehicleEntryRepository.findAll();
            if (vehicleEntryList.isEmpty())
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No users available!");
            List<VehicleEntryResDto> vehicleEntryResDtoList = vehicleEntryList.stream().map(dtoUtilities::vehicleEntryToDto)
                    .collect(Collectors.toList());
            String fileName = "entry_excel_data.xlsx";
            String sheetName=fileName.substring(0,fileName.indexOf('.'));
            Resource resource = excelUtility.downloadDataAsExcel(vehicleEntryResDtoList, sheetName);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName)
                    .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error!" + e);
        }
    }

    @Override
    public ResponseEntity<?> downloadExcelSample() throws IOException {
        String fileName = "entry_excel_sample.xlsx";
        String sheetName=fileName.substring(0,fileName.indexOf('.'));
        Resource resource = excelUtility.downloadExcelSample(VehicleEntryReqDto.class,sheetName);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,"attachment;filename="+fileName)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(resource);
    }

    @Override
    public ResponseEntity<?> validateExcelData(MultipartFile file) {
        try {
            if (excelUtility.isExcelFile(file)) {
                ExcelValidateDataResponseDto validateDataResponse = excelUtility.validateExcelData(file, VehicleEntryReqDto.class);
                return ResponseEntity.status(HttpStatus.OK).body(validateDataResponse);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Incorrect uploaded file type!");
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
