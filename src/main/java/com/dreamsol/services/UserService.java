package com.dreamsol.services;

import com.dreamsol.dtos.requestDtos.UserRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface UserService
{
    ResponseEntity<?> addUser(UserRequestDto userRequestDto);
    ResponseEntity<?> updateUser(UserRequestDto userRequestDto, Long id);
    ResponseEntity<?> deleteUser(Long id);
    ResponseEntity<?> getUser(Long id);
    ResponseEntity<?> getUsers();
    ResponseEntity<?> downloadUsersDataAsExcel();

    ResponseEntity<?> downloadExcelSample();
    ResponseEntity<?> validateExcelData(MultipartFile file);
}
