package com.dreamsol.controllers;

import com.dreamsol.dtos.requestDtos.DrivingLicenceReqDto;
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

    @PutMapping("/update/{licenceId}")
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
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        return drivingLicenceService.fetchAllDrivers(search, page, size, sortBy);
    }


    @GetMapping(path = "/download/{fileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) throws IOException {
        return drivingLicenceService.getFile(fileName, uploadDir);
    }
}
