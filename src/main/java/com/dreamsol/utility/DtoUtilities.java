package com.dreamsol.utility;

import com.dreamsol.dtos.requestDtos.DrivingLicenceReqDto;
import com.dreamsol.dtos.requestDtos.UserRequestDto;
import com.dreamsol.dtos.responseDtos.DrivingLicenceResDto;
import com.dreamsol.dtos.responseDtos.UserResponseDto;
import com.dreamsol.entites.DrivingLicence;
import com.dreamsol.entites.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class DtoUtilities
{
    private final PasswordEncoder passwordEncoder;

    public User userRequstDtoToUser(UserRequestDto userRequestDto)
    {
        User user = new User();
        BeanUtils.copyProperties(userRequestDto,user);
        user.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));
        return user;
    }
    public UserResponseDto userToUserResponseDto(User user)
    {
        UserResponseDto userResponseDto = new UserResponseDto();
        BeanUtils.copyProperties(user,userResponseDto);
        return userResponseDto;
    }

    public DrivingLicence licenceDtoToLicence(DrivingLicenceReqDto drivingLicenceReqDto) {
        DrivingLicence drivingLicence = new DrivingLicence();
        BeanUtils.copyProperties(drivingLicenceReqDto, drivingLicence);
        return drivingLicence;
    }

    public DrivingLicenceResDto licenceToLicenceDto(DrivingLicence drivingLicence) {
        DrivingLicenceResDto drivingLicenceResDto = new DrivingLicenceResDto();
        BeanUtils.copyProperties(drivingLicence, drivingLicenceResDto);
        return drivingLicenceResDto;
    }
}
