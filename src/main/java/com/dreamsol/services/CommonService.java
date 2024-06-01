package com.dreamsol.services;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CommonService
{
    ResponseEntity<?> create(Object data);
    default ResponseEntity<?> create(Object data,MultipartFile file)
    {
        return ResponseEntity.status(HttpStatus.OK).body("Creating new entity");
    }
    ResponseEntity<?> update(Object data, Long id);
    ResponseEntity<?> delete(Long id);
    ResponseEntity<?> get(Long id);
    ResponseEntity<?> getAll();
    ResponseEntity<?> downloadDataAsExcel();
    ResponseEntity<?> downloadExcelSample();
    ResponseEntity<?> validateExcelData(MultipartFile file);
    ResponseEntity<?> saveBulkData(List<Object> userList);
}
