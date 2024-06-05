package com.dreamsol.services;

import com.dreamsol.dtos.requestDtos.VehicleEntryReqDto;
import com.dreamsol.dtos.responseDtos.VehicleEntryResDto;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


public interface VehicleEntryService {

    ResponseEntity<?> addEntry(VehicleEntryReqDto vehicleEntryReqDto);

    ResponseEntity<?> deleteEntry(Long entryId);

    ResponseEntity<?> updateEntry(VehicleEntryReqDto vehicleEntryReqDto, Long entryId);

    ResponseEntity<?> fetchById(Long entryId);

    ResponseEntity<Page<VehicleEntryResDto>> fetchAllEntries(
            Long unitId,
            String status,
            int page,
            int size,
            String sortBy);

    ResponseEntity<?> downloadEntryDataAsExcel();

    ResponseEntity<?> downloadExcelSample() throws IOException;

    ResponseEntity<?> validateExcelData(MultipartFile file);
}
