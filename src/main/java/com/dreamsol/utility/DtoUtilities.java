package com.dreamsol.utility;

import com.dreamsol.dtos.requestDtos.DrivingLicenceReqDto;
import com.dreamsol.dtos.requestDtos.UserRequestDto;
import com.dreamsol.dtos.requestDtos.VehicleLicenceReqDto;
import com.dreamsol.dtos.responseDtos.DrivingLicenceResDto;
import com.dreamsol.dtos.responseDtos.UserResponseDto;
import com.dreamsol.dtos.responseDtos.VehicleLicenceResDto;
import com.dreamsol.entites.DrivingLicence;
import com.dreamsol.entites.User;
import com.dreamsol.entites.VehicleLicence;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


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
        if (drivingLicence.getFile() != null) {
            String fileUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/licence/download/")
                    .path(drivingLicence.getFile().getGeneratedFileName())
                    .toUriString();
            drivingLicenceResDto.setFileUrl(fileUrl);
        }
        return drivingLicenceResDto;
    }

    public VehicleLicence vehicleLicenceDtoToVehicleLicence(VehicleLicenceReqDto vehicleLicenceReqDto) {
        VehicleLicence vehicleLicence=new VehicleLicence();
        BeanUtils.copyProperties(vehicleLicenceReqDto,vehicleLicence);
        return vehicleLicence;
    }

    public VehicleLicenceResDto vehicleLicenceToVehicleLicenceDto(VehicleLicence vehicleLicence) {
        VehicleLicenceResDto vehicleLicenceDto = new VehicleLicenceResDto();
        BeanUtils.copyProperties(vehicleLicence, vehicleLicenceDto);

        if (vehicleLicence.getPucAttachment() != null) {
            String pucAttachmentUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/vehicle-licence/download/")
                    .path(vehicleLicence.getPucAttachment().getGeneratedFileName())
                    .toUriString();
            vehicleLicenceDto.setPucUrl(pucAttachmentUrl);
        }

        if (vehicleLicence.getInsuranceAttachment() != null) {
            String insuranceAttachmentUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/vehicle-licence/download/")
                    .path(vehicleLicence.getInsuranceAttachment().getGeneratedFileName())
                    .toUriString();
            vehicleLicenceDto.setInsuranceUrl(insuranceAttachmentUrl);
        }

        if (vehicleLicence.getRegistrationAttachment() != null) {
            String registrationAttachmentUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/vehicle-licence/download/")
                    .path(vehicleLicence.getRegistrationAttachment().getGeneratedFileName())
                    .toUriString();
            vehicleLicenceDto.setRegistrationUrl(registrationAttachmentUrl);
        }

        return vehicleLicenceDto;
    }
}
