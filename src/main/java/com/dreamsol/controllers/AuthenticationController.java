package com.dreamsol.controllers;

import com.dreamsol.dtos.requestDtos.AuthRequestDto;
import com.dreamsol.dtos.requestDtos.UserRequestDto;
import com.dreamsol.services.AuthRequestService;
import com.dreamsol.services.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class AuthenticationController
{
    private final AuthRequestService authRequestService;
    private final UserService userService;

    @PostMapping("/authenticate-user")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequestDto authRequestDto)
    {
        return authRequestService.getToken(authRequestDto.getUsername(),authRequestDto.getPassword());
    }

    @PostMapping("/register-user")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRequestDto userRequestDto)
    {
        return userService.addUser(userRequestDto);
    }

    @PutMapping("/update-user/{id}")
    public ResponseEntity<?> updateUser(@Valid @RequestBody UserRequestDto userRequestDto,@PathVariable Long id)
    {
        return userService.updateUser(userRequestDto,id);
    }

    @DeleteMapping("/delete-user/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id)
    {
        return userService.deleteUser(id);
    }

    @GetMapping("/fetch-user/{id}")
    public ResponseEntity<?> fetchUser(@PathVariable Long id)
    {
        return userService.getUser(id);
    }

    @GetMapping("/fetch-all-user")
    public ResponseEntity<?> fetchAllUsers()
    {
        return userService.getUsers();
    }
    @GetMapping(value = "/download-excel", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<?> downloadExcel()
    {
        return userService.downloadUsersDataAsExcel();
    }
}
