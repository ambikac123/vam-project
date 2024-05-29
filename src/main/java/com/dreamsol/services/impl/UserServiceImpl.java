package com.dreamsol.services.impl;

import com.dreamsol.dtos.requestDtos.UserRequestDto;
import com.dreamsol.dtos.responseDtos.UserResponseDto;
import com.dreamsol.entites.User;
import com.dreamsol.repositories.UserRepository;
import com.dreamsol.services.UserService;
import com.dreamsol.utility.DtoUtilities;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService
{
    private final DtoUtilities dtoUtilities;
    private final UserRepository userRepository;
    @Override
    public ResponseEntity<?> addUser(UserRequestDto userRequestDto)
    {
        try
        {
            User user = userRepository.findByEmailOrMobile(userRequestDto.getEmail(),userRequestDto.getMobile());
            if(user!=null)
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("user already exist");
            user = dtoUtilities.userRequstDtoToUser(userRequestDto);
            user.setCreatedBy(userRequestDto.getName());
            user.setUpdatedBy(userRequestDto.getName());
            userRepository.save(user);
            return ResponseEntity.status(HttpStatus.CREATED).body("New user created successfully!");
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error!");
        }
    }

    @Override
    public ResponseEntity<?> updateUser(UserRequestDto userRequestDto, Long id)
    {
        try
        {
            User user = userRepository.findById(id).orElseThrow(()->new RuntimeException("user not found with id: " + id));
            if (!user.isStatus())
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("user not found with id: " + id);
            user = dtoUtilities.userRequstDtoToUser(userRequestDto);
            user.setId(id);
            user.setUpdatedBy(userRequestDto.getName());
            userRepository.save(user);
            return ResponseEntity.status(HttpStatus.OK).body("User updated successfully!");
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error!");
        }
    }

    @Override
    public ResponseEntity<?> deleteUser(Long id)
    {
        try
        {
            User user = userRepository.findById(id).orElseThrow(()-> new RuntimeException("user not found with id: "+id));
            user.setStatus(false);
            userRepository.save(user);
            return ResponseEntity.status(HttpStatus.OK).body("User deleted!");
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error!");
        }
    }

    @Override
    public ResponseEntity<?> getUser(Long id)
    {
        try {
            User user = userRepository.findByIdAndStatusTrue(id);
            if (user == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with id: " + id);
            UserResponseDto userResponseDto = dtoUtilities.userToUserResponseDto(user);
            return ResponseEntity.status(HttpStatus.FOUND).body(userResponseDto);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error!");
        }
    }

    @Override
    public ResponseEntity<?> getUsers()
    {
        try {
            List<User> userList = userRepository.findAll();
            if (userList.isEmpty())
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No users available!");
            List<UserResponseDto> userResponseDtoList = userList.stream()
                    .map(dtoUtilities::userToUserResponseDto).collect(Collectors.toList());
            return ResponseEntity.status(HttpStatus.OK).body(userResponseDtoList);
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error!");
        }
    }
}