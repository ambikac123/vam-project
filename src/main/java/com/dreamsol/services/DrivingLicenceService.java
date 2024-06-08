package com.dreamsol.services;

import com.dreamsol.dtos.requestDtos.DrivingLicenceReqDto;
import com.dreamsol.dtos.responseDtos.DrivingLicenceResDto;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public interface DrivingLicenceService {

    public ResponseEntity<?> addLicence(DrivingLicenceReqDto licenceReqDto, MultipartFile file, String path);

    ResponseEntity<?> deleteLicence(Long licenceId);

    ResponseEntity<?> updateLicence(DrivingLicenceReqDto drivingLicenceReqDto, Long licenceId, MultipartFile file,String path);

    ResponseEntity<?> fetchById(Long licenceId);

    ResponseEntity<Page<DrivingLicenceResDto>> fetchAllDrivers(
            String status,
            Long unitId,
            int page,
            int size,
            String sortBy,
            String sortDirection);

    ResponseEntity<Resource> getFile(String fileName, String uploadDir) throws IOException;

    ResponseEntity<?> downloadDriverDataAsExcel();

    ResponseEntity<?> downloadExcelSample() throws IOException;

    ResponseEntity<?> uploadExcelFile(MultipartFile file,Class<?> currentClass);

    ResponseEntity<?> saveBulkData(List<DrivingLicenceReqDto> drivingLicenceReqDtoList);
}
