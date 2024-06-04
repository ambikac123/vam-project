package com.dreamsol.controllers;

import com.dreamsol.dtos.requestDtos.AuthRequestDto;
import com.dreamsol.dtos.requestDtos.UserRequestDto;
import com.dreamsol.services.AuthRequestService;
import com.dreamsol.services.CommonService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
<<<<<<< Updated upstream
//@CrossOrigin(origins = "http://192.168.1.8:8080/**")
@RequiredArgsConstructor
=======
>>>>>>> Stashed changes
@SecurityRequirement(name = "bearerAuth")
public class AuthenticationController {
    private final AuthRequestService authRequestService;
<<<<<<< Updated upstream
    private final CommonService commonService;

=======

    private final CommonService<UserRequestDto,Long> commonService;
    @Autowired
    public AuthenticationController(@Qualifier("userService") CommonService<UserRequestDto,Long> commonService, AuthRequestService authRequestService) {
        this.commonService = commonService;
        this.authRequestService = authRequestService;
    }
>>>>>>> Stashed changes
    @PostMapping("/authenticate-user")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequestDto authRequestDto) {
        return authRequestService.getToken(authRequestDto.getUsername(), authRequestDto.getPassword());
    }

    @PostMapping("/register-user")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRequestDto userRequestDto) {
        return commonService.create(userRequestDto);
    }

    @GetMapping("/regenerate-token")
    public ResponseEntity<?> regenerateToken(@RequestParam String refreshToken)
    {
        return authRequestService.createTokenByRefreshToken(refreshToken);
    }
    @PutMapping("/update-user/{id}")
    public ResponseEntity<?> updateUser(@Valid @RequestBody UserRequestDto userRequestDto, @PathVariable Long id) {
        return commonService.update(userRequestDto, id);
    }

    @DeleteMapping("/delete-user/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        return commonService.delete(id);
    }

    @GetMapping("/get-user/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id) {
        return commonService.get(id);
    }

    @GetMapping("/get-users")
    public ResponseEntity<?> getAllUsers() {
        return commonService.getAll();
    }

    @GetMapping(value = "/download-excel-data", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<?> downloadExcelData() {
        return commonService.downloadDataAsExcel();
    }

    @GetMapping(value = "/download-excel-sample", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<?> downloadExcelSample() {
        return commonService.downloadExcelSample();
    }

    @PostMapping(value = "/upload-excel-data", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
<<<<<<< Updated upstream
    public ResponseEntity<?> uploadExcelData(@RequestParam("file") MultipartFile file) {
        return commonService.validateExcelData(file);
=======
    public ResponseEntity<?> uploadExcelData(@RequestParam("file") MultipartFile file)
    {
        return commonService.uploadExcelFile(file,UserRequestDto.class);
>>>>>>> Stashed changes
    }

    @PostMapping("/save-bulk-data")
<<<<<<< Updated upstream
    public ResponseEntity<?> saveBulkData(@RequestBody @Valid List<UserRequestDto> userList) {
        System.out.println(Collections.singletonList(userList));
        return commonService.saveBulkData(Collections.singletonList(userList));
=======
    public ResponseEntity<?> saveBulkData(@RequestBody @Valid List<UserRequestDto> userList)
    {
        return commonService.saveBulkData(userList);
>>>>>>> Stashed changes
    }
}
