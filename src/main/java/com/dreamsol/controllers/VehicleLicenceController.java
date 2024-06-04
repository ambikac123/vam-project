package com.dreamsol.controllers;

import com.dreamsol.dtos.requestDtos.VehicleLicenceReqDto;
import com.dreamsol.dtos.responseDtos.VehicleLicenceResDto;
import com.dreamsol.services.VehicleLicenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/vehicle-licence")
public class VehicleLicenceController {

    private final VehicleLicenceService vehicleLicenceService;

    @Value("${project.FileUpload}")
    private String uploadDir;

    @PostMapping(path = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createLicence(@Valid @RequestPart VehicleLicenceReqDto vehicleLicenceReqDto,
                                           @RequestParam("pucFile") MultipartFile pucFile,
                                           @RequestParam("insuranceFile") MultipartFile insuranceFile,
                                           @RequestParam("registrationFile") MultipartFile registrationFile) {
        return vehicleLicenceService.addLicence(vehicleLicenceReqDto,pucFile,insuranceFile,registrationFile, uploadDir);
    }

    @DeleteMapping("/delete/{licenceId}")
    public ResponseEntity<?> deleteLicence(@PathVariable Long licenceId) {
        return vehicleLicenceService.deleteLicence(licenceId);
    }

    @PutMapping("/update/{licenceId}")
    public ResponseEntity<?> updateLicence(@Valid @RequestPart VehicleLicenceReqDto vehicleLicenceReqDto,
                                           @PathVariable Long licenceId,
                                           @RequestParam("pucFile") MultipartFile pucFile,
                                           @RequestParam("insuranceFile") MultipartFile insuranceFile,
                                           @RequestParam("registrationFile") MultipartFile registrationFile) {
        return vehicleLicenceService.updateLicence(vehicleLicenceReqDto, licenceId, pucFile,insuranceFile,registrationFile, uploadDir);
    }

    @GetMapping("/get/{licenceId}")
    public ResponseEntity<?> getLicenceById(@PathVariable Long licenceId) {
        return vehicleLicenceService.fetchById(licenceId);
    }

    @GetMapping("/get-all")
    public ResponseEntity<Page<VehicleLicenceResDto>> fetchAll(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        return vehicleLicenceService.fetchAllVehicles(status, page, size, sortBy);
    }

    @GetMapping(path = "/download/{fileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) throws IOException {
        return vehicleLicenceService.getFile(fileName, uploadDir);
    }

    @GetMapping(value = "/download-excel-data", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<?> downloadExcelData()
    {
        return vehicleLicenceService.downloadVehicleDataAsExcel();
    }

    @GetMapping(value = "/download-excel-sample",produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<?> downloadExcelSample() throws IOException {
        return vehicleLicenceService.downloadExcelSample();
    }

}
