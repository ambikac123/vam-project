package com.dreamsol.controllers;

import com.dreamsol.dtos.requestDtos.VehicleEntryReqDto;
import com.dreamsol.dtos.responseDtos.PurposeCountDto;
import com.dreamsol.dtos.responseDtos.VehicleEntryCountDto;
import com.dreamsol.dtos.responseDtos.VehicleEntryResDto;
import com.dreamsol.services.impl.VehicleEntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/vehicle-entry")
public class VehicleEntryController {

    private final VehicleEntryService vehicleEntryService;

    @PostMapping("/add")
    public ResponseEntity<?> createEntry(@Valid @RequestBody VehicleEntryReqDto vehicleEntryReqDto) {
        return vehicleEntryService.addEntry(vehicleEntryReqDto);
    }

    @DeleteMapping("/delete/{entryId}")
    public ResponseEntity<?> deleteEntry(@PathVariable Long entryId) {
        return vehicleEntryService.deleteEntry(entryId);
    }

    @PutMapping("/update/{entryId}")
    public ResponseEntity<?> updateEntry(@Valid @RequestBody VehicleEntryReqDto vehicleEntryReqDto,
                                         @PathVariable Long entryId) {
        return vehicleEntryService.updateEntry(vehicleEntryReqDto, entryId);
    }

    @GetMapping("/get/{entryId}")
    public ResponseEntity<?> getEntryById(@PathVariable Long entryId) {
        return vehicleEntryService.fetchById(entryId);
    }

    @GetMapping("/get-all")
    public ResponseEntity<Page<VehicleEntryResDto>> fetchAll(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long unitId,
            @RequestParam(required = false) Long plantId,
            @RequestParam(required = false) Long purposeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        return vehicleEntryService.fetchAllEntries(status,unitId,plantId,purposeId, page, size, sortBy,sortDirection);
    }
//
//    @GetMapping(value = "/download-excel-data", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
//    public ResponseEntity<?> downloadExcelData()
//    {
//        return vehicleEntryService.downloadEntryDataAsExcel();
//    }

    @GetMapping("/download-entry-data")
    public ResponseEntity<?> downloadEntryDataAsExcel(
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "unitId", required = false) Long unitId,
            @RequestParam(value = "plantId", required = false) Long plantId,
            @RequestParam(value = "purposeId", required = false) Long purposeId) {
        return vehicleEntryService.downloadEntryDataAsExcel(status, unitId, plantId, purposeId);
    }


    @GetMapping(value = "/download-excel-sample",produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<?> downloadExcelSample() throws IOException {
        return vehicleEntryService.downloadExcelSample();
    }

//    @GetMapping("/purposes/count")
//    public ResponseEntity<List<PurposeCountDto>> fetchPurposeCountsByDateRange(
//            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fromDate,
//            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate toDate) {
//
//        return vehicleEntryService.fetchPurposeCountsByDateRange(fromDate.atStartOfDay(), toDate.atTime(LocalTime.MAX));
//    }

    @GetMapping("/count")
    public ResponseEntity<VehicleEntryCountDto> getEntryCounts() {
       return vehicleEntryService.getEntryCounts();
    }

    @PostMapping("/entry/exit")
    public ResponseEntity<?> exitEntry(@RequestParam Long entryId) {
        return vehicleEntryService.exitEntry(entryId);
    }
}
