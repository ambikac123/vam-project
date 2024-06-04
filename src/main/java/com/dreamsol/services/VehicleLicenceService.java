package com.dreamsol.services;

import com.dreamsol.dtos.requestDtos.VehicleLicenceReqDto;
import com.dreamsol.dtos.responseDtos.VehicleLicenceResDto;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;

public interface VehicleLicenceService {

    ResponseEntity<?> addLicence(VehicleLicenceReqDto vehicleLicenceReqDto,
                                 MultipartFile pucFile,
                                 MultipartFile insuranceFile,
                                 MultipartFile registrationFile,
                                 String path);

    ResponseEntity<?> deleteLicence(Long licenceId);

    public ResponseEntity<?> updateLicence(VehicleLicenceReqDto vehicleLicenceReqDto, Long licenceId,
                                           MultipartFile pucFile, MultipartFile insuranceFile, MultipartFile registrationFile,
                                           String path);

    ResponseEntity<?> fetchById(Long licenceId);

    ResponseEntity<Page<VehicleLicenceResDto>> fetchAllVehicles(
            String status,
            int page,
            int size,
            String sortBy);

    ResponseEntity<Resource> getFile(String fileName, String uploadDir) throws IOException;

    ResponseEntity<?> downloadVehicleDataAsExcel();

    ResponseEntity<?> downloadExcelSample();
}
