package com.dreamsol.controllers;

import com.dreamsol.dtos.requestDtos.UserRequestDto;
import com.dreamsol.dtos.requestDtos.UserTypeRequestDto;
import com.dreamsol.services.CommonService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/user-types")
@RequiredArgsConstructor
public class UserTypeController
{
    private final CommonService<UserTypeRequestDto,Long> userTypeService;
    @PostMapping("/create")
    public ResponseEntity<?> createUserType(@RequestBody @Valid UserTypeRequestDto userTypeRequestDto){
        return userTypeService.create(userTypeRequestDto);
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateUserType(@RequestBody @Valid UserTypeRequestDto userTypeRequestDto,@PathVariable Long id){
        return userTypeService.update(userTypeRequestDto,id);
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUserType(@PathVariable Long id){
        return userTypeService.delete(id);
    }
    @GetMapping("/get/{id}")
    public ResponseEntity<?> getUserType(@PathVariable Long id){
        return userTypeService.get(id);
    }
    @GetMapping("/get-all")
    public ResponseEntity<?> getAllUserType(
            @PageableDefault(size = 10, sort = "userTypeName", page = 0) Pageable pageable,
            @RequestParam(required = false) String search
    )
    {
        return userTypeService.getAll(pageable,search);
    }
    @GetMapping(value = "/download-excel-data", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<?> downloadExcelData() {
        return userTypeService.downloadDataAsExcel();
    }
    @GetMapping(value = "/download-excel-sample",produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<?> downloadExcelSample() {
        return userTypeService.downloadExcelSample();
    }
   @PostMapping(value = "/upload-excel-data", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadExcelData(@RequestParam("file") MultipartFile file)
    {
        return userTypeService.uploadExcelFile(file,UserTypeRequestDto.class);
    }
    @PostMapping("/save-bulk-data")
    public ResponseEntity<?> saveBulkData(@RequestBody @Valid List<UserTypeRequestDto> userTypeRequestDtoList)
    {
        return userTypeService.saveBulkData(userTypeRequestDtoList);
    }
}
