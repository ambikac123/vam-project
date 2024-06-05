package com.dreamsol.controllers;

import com.dreamsol.dtos.requestDtos.VehicleEntryReqDto;
import com.dreamsol.dtos.responseDtos.VehicleEntryResDto;
import com.dreamsol.services.VehicleEntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;

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
            @RequestParam(required = false) Long unitId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        return vehicleEntryService.fetchAllEntries(unitId,status, page, size, sortBy);
    }

    @GetMapping(value = "/download-excel-data", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<?> downloadExcelData()
    {
        return vehicleEntryService.downloadEntryDataAsExcel();
    }

    @GetMapping(value = "/download-excel-sample",produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<?> downloadExcelSample() throws IOException {
        return vehicleEntryService.downloadExcelSample();
    }

    @PostMapping(value = "/upload-excel-data", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadExcelData(@RequestParam("file") MultipartFile file)
    {
        return vehicleEntryService.validateExcelData(file);
    }
}
