package com.dreamsol.services;

import com.dreamsol.dtos.requestDtos.DrivingLicenceReqDto;
import com.dreamsol.dtos.responseDtos.DrivingLicenceResDto;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;

public interface DrivingLicenceService {

    public ResponseEntity<?> addLicence(DrivingLicenceReqDto licenceReqDto, MultipartFile file, String path);

    ResponseEntity<?> deleteLicence(Long licenceId);

    ResponseEntity<?> updateLicence(DrivingLicenceReqDto drivingLicenceReqDto, Long licenceId, MultipartFile file,String path);

    ResponseEntity<?> fetchById(Long licenceId);

    ResponseEntity<Page<DrivingLicenceResDto>> fetchAllDrivers(
            String status,
            int page,
            int size,
            String sortBy);

    ResponseEntity<Resource> getFile(String fileName, String uploadDir) throws IOException;

    ResponseEntity<?> downloadDriverDataAsExcel();

    ResponseEntity<?> downloadExcelSample();

    ResponseEntity<?> validateExcelData(MultipartFile file);
}
