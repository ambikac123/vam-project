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

//    ResponseEntity<Page<VehicleEntryResDto>> fetchAllEntries(
//            String locationFrom,
//            String tripId,
//            String invoiceNo,
//            String materialDescription,
//            Long quantity,
//            Long numberOfBill,
//            String destinationTo,
//            int page,
//            int size,
//            String sortBy);

    ResponseEntity<?> downloadEntryDataAsExcel();

    ResponseEntity<?> downloadExcelSample() throws IOException;
}
