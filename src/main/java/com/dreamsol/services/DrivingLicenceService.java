package com.dreamsol.services;

import com.dreamsol.dtos.requestDtos.DrivingLicenceReqDto;
import com.dreamsol.dtos.responseDtos.DrivingLicenceResDto;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DrivingLicenceService {

    public ResponseEntity<?> addLicence(DrivingLicenceReqDto licenceReqDto, MultipartFile file, String path);

    ResponseEntity<?> deleteLicence(Long licenceId);

    ResponseEntity<?> updateLicence(DrivingLicenceReqDto drivingLicenceReqDto, Long licenceId);

    ResponseEntity<?> fetchById(Long licenceId);

    ResponseEntity<Page<DrivingLicenceResDto>> fetchAllDrivers(String search, int page, int size, String sortBy);
}
