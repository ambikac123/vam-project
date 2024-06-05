package com.dreamsol.services;

import com.dreamsol.dtos.responseDtos.ExcelValidateDataResponseDto;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CommonService<T,ID>
{
    ResponseEntity<?> create(T data);
    ResponseEntity<?> update(T data, ID id);
    ResponseEntity<?> delete(ID id);
    ResponseEntity<?> get(ID id);
    ResponseEntity<?> getAll(Pageable pageable,String keyword);
    ResponseEntity<?> downloadDataAsExcel();
    ResponseEntity<?> downloadExcelSample();
    ResponseEntity<?> uploadExcelFile(MultipartFile file,Class<?> currentClass);
    ExcelValidateDataResponseDto validateDataFromDB(ExcelValidateDataResponseDto validateDataResponse);
    boolean isExistInDB(Object keyword);
    ResponseEntity<?> saveBulkData(List<T> list);
}
