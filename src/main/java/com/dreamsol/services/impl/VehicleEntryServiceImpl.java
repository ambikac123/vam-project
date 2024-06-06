package com.dreamsol.services.impl;

import com.dreamsol.dtos.requestDtos.VehicleEntryReqDto;
import com.dreamsol.dtos.responseDtos.ApiResponse;
import com.dreamsol.dtos.responseDtos.VehicleEntryResDto;
import com.dreamsol.entites.*;
import com.dreamsol.exceptions.ResourceNotFoundException;
import com.dreamsol.repositories.*;
import com.dreamsol.services.VehicleEntryService;
import com.dreamsol.utility.DtoUtilities;
import com.dreamsol.utility.ExcelUtility;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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

    @Override
    public ResponseEntity<?> addEntry(VehicleEntryReqDto vehicleEntryReqDto) {
        try {
            Optional<DrivingLicence> optionalDrivingLicence = drivingLicenceRepo.findByDriverMobile(vehicleEntryReqDto.getDriverMobile());
            DrivingLicence drivingLicence = optionalDrivingLicence.orElseThrow(() -> new NotFoundException("Driver not found with this Mobile Number,Please add the driver first"));

            Optional<VehicleLicence> optionalVehicleLicence = vehicleLicenceRepo.findByVehicleNumber(vehicleEntryReqDto.getVehicleNumber());
            VehicleLicence vehicleLicence = optionalVehicleLicence.orElseThrow(() -> new NotFoundException("Vehicle not found with this vehicle number,Please add the vehicle First"));

            Optional<Plant> optionalPlant = plantRepository.findByPlantNameContainingIgnoreCase(vehicleEntryReqDto.getPlantName());
            Plant plant = optionalPlant.orElseThrow(() -> new NotFoundException("Plant not found with this name,Please add the Plant first"));

            Optional<Purpose> optionalPurpose = purposeRepository.findByPurposeForContainingIgnoreCase(vehicleEntryReqDto.getPurposeFor());
            Purpose purpose = optionalPurpose.orElseThrow(() -> new NotFoundException("Purpose not found with this name,Please add the purpose first"));

            VehicleEntry vehicleEntry = dtoUtilities.vehicleEntryDtoToVehicleEntry(vehicleEntryReqDto,drivingLicence,vehicleLicence,plant,purpose);

            VehicleEntry savedVehicleEntry=vehicleEntryRepository.save(vehicleEntry);

            VehicleEntryResDto vehicleEntryResDto=dtoUtilities.vehicleEntryToDto(savedVehicleEntry);

            return ResponseEntity.status(HttpStatus.CREATED).body(vehicleEntryResDto);
        } catch (NotFoundException e) {

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(), false));
        } catch (Exception e) {

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

//    public ResponseEntity<Page<VehicleEntryResDto>> fetchAllEntries(
//            int page,
//            int size,
//            String sortBy) {
//
//        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
//
//
//        Page<VehicleEntry> vehicleEntries = vehicleEntryRepository.findAll( pageable);
//        Page<VehicleEntryResDto> vehicleEntryResDtoPage = vehicleEntries.map(dtoUtilities::vehicleEntryToDto);
//
//        return ResponseEntity.ok(vehicleEntryResDtoPage);
//    }

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
}
