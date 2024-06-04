package com.dreamsol.services;

import com.dreamsol.dtos.responseDtos.ExcelValidateDataResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

<<<<<<< Updated upstream
public interface CommonService {
    ResponseEntity<?> create(Object data);

    default ResponseEntity<?> create(Object data, MultipartFile file) {
        return ResponseEntity.status(HttpStatus.OK).body("Creating new entity");
    }

    ResponseEntity<?> update(Object data, Long id);

    ResponseEntity<?> delete(Long id);

    ResponseEntity<?> get(Long id);

=======
public interface CommonService<T,ID>
{
    ResponseEntity<?> create(T data);
   /* default ResponseEntity<?> create(Object data,MultipartFile file)
    {
        return ResponseEntity.status(HttpStatus.OK).body("Creating new entity");
    }*/
    ResponseEntity<?> update(T data, ID id);
    ResponseEntity<?> delete(ID id);
    ResponseEntity<?> get(ID id);
>>>>>>> Stashed changes
    ResponseEntity<?> getAll();

    ResponseEntity<?> downloadDataAsExcel();

    ResponseEntity<?> downloadExcelSample();
<<<<<<< Updated upstream

    ResponseEntity<?> validateExcelData(MultipartFile file);

    ResponseEntity<?> saveBulkData(List<Object> userList);
=======
    ResponseEntity<?> uploadExcelFile(MultipartFile file,Class<?> currentClass);
    ExcelValidateDataResponseDto validateDataFromDB(ExcelValidateDataResponseDto validateDataResponse);
    boolean isExistInDB(Object keyword);
    ResponseEntity<?> saveBulkData(List<T> list);
>>>>>>> Stashed changes
}
