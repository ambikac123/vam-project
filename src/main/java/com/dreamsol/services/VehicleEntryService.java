package com.dreamsol.services;

import com.dreamsol.dtos.requestDtos.VehicleEntryReqDto;
import com.dreamsol.dtos.responseDtos.VehicleEntryResDto;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.io.IOException;


public interface VehicleEntryService {

    ResponseEntity<?> addEntry(VehicleEntryReqDto vehicleEntryReqDto);

    ResponseEntity<?> deleteEntry(Long entryId);

    ResponseEntity<?> updateEntry(VehicleEntryReqDto vehicleEntryReqDto, Long entryId);

    ResponseEntity<?> fetchById(Long entryId);

    ResponseEntity<Page<VehicleEntryResDto>> fetchAllEntries(
            String status,
            Long unitId,
            Long plantId,
            Long purposeId,
            int page,
            int size,
            String sortBy,
            String sortDirection);

    ResponseEntity<?> downloadEntryDataAsExcel();

    ResponseEntity<?> downloadExcelSample() throws IOException;
}
