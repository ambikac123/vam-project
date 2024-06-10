package com.dreamsol.controllers;

import com.dreamsol.dtos.requestDtos.DrivingLicenceReqDto;
import com.dreamsol.dtos.requestDtos.UserTypeRequestDto;
import com.dreamsol.dtos.requestDtos.VehicleLicenceReqDto;
import com.dreamsol.dtos.responseDtos.DrivingLicenceResDto;
import com.dreamsol.services.DrivingLicenceService;
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
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/driving-licence")
public class DrivingLicenceController {

    private final DrivingLicenceService drivingLicenceService;

    @Value("${project.FileUpload}")
    private String uploadDir;

    @PostMapping(path = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createLicence(@Valid  @RequestPart DrivingLicenceReqDto licenceReqDto,
                                           @RequestParam("file") MultipartFile file) {
        return drivingLicenceService.addLicence(licenceReqDto, file, uploadDir);
    }

    @DeleteMapping("/delete/{licenceId}")
    public ResponseEntity<?> deleteLicence(@PathVariable Long licenceId) {
        return drivingLicenceService.deleteLicence(licenceId);
    }

    @PutMapping(path = "/update/{licenceId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateLicence(@RequestPart DrivingLicenceReqDto drivingLicenceReqDto, @PathVariable Long licenceId,
                                           @RequestParam("file") MultipartFile file) {
        return drivingLicenceService.updateLicence(drivingLicenceReqDto, licenceId,file,uploadDir);
    }

    @GetMapping("/get/{licenceId}")
    public ResponseEntity<?> getLicenceById(@PathVariable Long licenceId) {
        return drivingLicenceService.fetchById(licenceId);
    }

    @GetMapping("/get-all")
    public ResponseEntity<Page<DrivingLicenceResDto>> fetchAll(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long unitId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        return drivingLicenceService.fetchAllDrivers(status, unitId, page, size, sortBy, sortDirection);
    }

    @GetMapping(path = "/download/{fileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) throws IOException {
        return drivingLicenceService.getFile(fileName, uploadDir);
    }

    @GetMapping(value = "/download-excel-data", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<?> downloadExcelData()
    {
        return drivingLicenceService.downloadDriverDataAsExcel();
    }

    @GetMapping(value = "/download-excel-sample",produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<?> downloadExcelSample() throws IOException {
        return drivingLicenceService.downloadExcelSample();
    }

    @PostMapping(value = "/upload-excel-data", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadExcelData(@RequestParam("file") MultipartFile file)
    {
        return drivingLicenceService.uploadExcelFile(file,DrivingLicenceReqDto.class);
    }

    @PostMapping("/save-bulk-data")
    public ResponseEntity<?> saveBulkData(@RequestBody @Valid List<DrivingLicenceReqDto> drivingLicenceReqDtoList)
    {
        return drivingLicenceService.saveBulkData(drivingLicenceReqDtoList);
    }


    @GetMapping("/mobile/{driverMobile}")
    public ResponseEntity<?> getDriverByMobile(@PathVariable Long driverMobile) {
        return drivingLicenceService.findByDriverMobile(driverMobile);
    }

}
