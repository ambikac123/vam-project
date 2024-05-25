package com.dreamsol.services;

import com.dreamsol.dtos.requestDtos.UserRequestDto;
import org.springframework.http.ResponseEntity;

public interface UserService
{
    ResponseEntity<?> addUser(UserRequestDto userRequestDto);
    ResponseEntity<?> updateUser(UserRequestDto userRequestDto, Long id);
    ResponseEntity<?> deleteUser(Long id);
    ResponseEntity<?> getUser(Long id);
    ResponseEntity<?> getUsers();
}
