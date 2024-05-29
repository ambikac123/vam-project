package com.dreamsol.utility;

import com.dreamsol.dtos.requestDtos.DrivingLicenceReqDto;
import com.dreamsol.dtos.requestDtos.UserRequestDto;
import com.dreamsol.dtos.requestDtos.VehicleLicenceReqDto;
import com.dreamsol.dtos.responseDtos.DrivingLicenceResDto;
import com.dreamsol.dtos.responseDtos.UserResponseDto;
import com.dreamsol.dtos.responseDtos.VehicleLicenceResDto;
import com.dreamsol.entites.DrivingLicence;
import com.dreamsol.dtos.requestDtos.ContactRequestDto;
import com.dreamsol.dtos.requestDtos.DepartmentRequestDto;
import com.dreamsol.dtos.requestDtos.PlantRequestDto;
import com.dreamsol.dtos.requestDtos.PurposeRequestDto;
import com.dreamsol.dtos.requestDtos.UnitRequestDto;
import com.dreamsol.dtos.requestDtos.UserRequestDto;
import com.dreamsol.dtos.responseDtos.ContactResponseDto;
import com.dreamsol.dtos.responseDtos.DepartmentResponseDto;
import com.dreamsol.dtos.responseDtos.PlantResponseDto;
import com.dreamsol.dtos.responseDtos.PurposeResponseDto;
import com.dreamsol.dtos.responseDtos.UnitResponseDto;
import com.dreamsol.dtos.responseDtos.UserResponseDto;
import com.dreamsol.entites.Contact;
import com.dreamsol.entites.Department;
import com.dreamsol.entites.Plant;
import com.dreamsol.entites.Purpose;
import com.dreamsol.entites.Unit;
import com.dreamsol.entites.User;
import com.dreamsol.entites.VehicleLicence;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


@Component
@RequiredArgsConstructor
public class DtoUtilities {
    private final PasswordEncoder passwordEncoder;


    public User userRequstDtoToUser(UserRequestDto userRequestDto) {
        User user = new User();
        BeanUtils.copyProperties(userRequestDto, user);
        user.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));
        return user;
    }

    public UserResponseDto userToUserResponseDto(User user) {
        UserResponseDto userResponseDto = new UserResponseDto();
        BeanUtils.copyProperties(user, userResponseDto);
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
    public static Plant plantRequestDtoToPlant(PlantRequestDto plantRequestDto) {
        Plant plant = new Plant();

        BeanUtils.copyProperties(plantRequestDto, plant);
        plant.setCreatedAt(LocalDateTime.now());
        plant.setUpdatedAt(LocalDateTime.now());

        return plant;
    }

    public static Plant plantRequestDtoToPlant(Plant plant, PlantRequestDto plantRequestDto) {
        BeanUtils.copyProperties(plantRequestDto, plant);
        plant.setUpdatedAt(LocalDateTime.now());

        return plant;
    }

    public static PlantResponseDto plantToPlantResponseDto(Plant plant) {
        PlantResponseDto plantResponseDto = new PlantResponseDto();
        BeanUtils.copyProperties(plant, plantResponseDto);

        return plantResponseDto;
    }

    public static Unit unitRequestDtoToUnit(UnitRequestDto unitRequestDto) {
        Unit unit = new Unit();

        BeanUtils.copyProperties(unitRequestDto, unit);
        unit.setCreatedAt(LocalDateTime.now());
        unit.setUpdatedAt(LocalDateTime.now());

        return unit;
    }

    public static Unit unitRequestDtoToUnit(Unit unit, UnitRequestDto unitRequestDto) {
        BeanUtils.copyProperties(unitRequestDto, unit);
        unit.setUpdatedAt(LocalDateTime.now());

        return unit;
    }

    public static UnitResponseDto unitToUnitResponseDto(Unit unit) {
        UnitResponseDto unitResponseDto = new UnitResponseDto();
        BeanUtils.copyProperties(unit, unitResponseDto);
        return unitResponseDto;
    }

    public static Department departmentRequestDtoToDepartment(DepartmentRequestDto departmentRequestDto) {
        Department department = new Department();

        BeanUtils.copyProperties(departmentRequestDto, department);
        department.setCreatedAt(LocalDateTime.now());
        department.setUpdatedAt(LocalDateTime.now());
        department.setUnit(DtoUtilities.unitRequestDtoToUnit(departmentRequestDto.getUnit()));
        return department;
    }

    public static Department departmentRequestDtoToDepartment(Department department,
            DepartmentRequestDto departmentRequestDto) {
        BeanUtils.copyProperties(departmentRequestDto, department);
        department.setUpdatedAt(LocalDateTime.now());
        department.setUnit(DtoUtilities.unitRequestDtoToUnit(departmentRequestDto.getUnit()));
        return department;
    }

    public static DepartmentResponseDto departmentToDepartmentResponseDto(Department department) {
        DepartmentResponseDto departmentResponseDto = new DepartmentResponseDto();
        BeanUtils.copyProperties(department, departmentResponseDto);
        departmentResponseDto.setUnit(DtoUtilities.unitToUnitResponseDto(department.getUnit()));
        return departmentResponseDto;
    }

    public static Contact contactRequestDtoToContact(ContactRequestDto contactRequestDto) {
        Contact contact = new Contact();
        BeanUtils.copyProperties(contactRequestDto, contact);
        contact.setDepartment(DtoUtilities.departmentRequestDtoToDepartment(contactRequestDto.getDepartment()));
        contact.setCreatedAt(LocalDateTime.now());
        contact.setUpdatedAt(LocalDateTime.now());
        return contact;
    }

    public static Contact contactRequestDtoToContact(Contact contact, ContactRequestDto contactRequestDto) {
        BeanUtils.copyProperties(contactRequestDto, contact);
        contact.setDepartment(DtoUtilities.departmentRequestDtoToDepartment(contactRequestDto.getDepartment()));
        contact.setUpdatedAt(LocalDateTime.now());
        return contact;
    }

    public static ContactResponseDto contactToContactResponseDto(Contact contact) {
        ContactResponseDto contactResponseDto = new ContactResponseDto();
        BeanUtils.copyProperties(contact, contactResponseDto);
        contactResponseDto
                .setDepartment(DtoUtilities.departmentToDepartmentResponseDto(contact.getDepartment()));
        return contactResponseDto;
    }

    public static Purpose purposeRequestDtoToPurpose(PurposeRequestDto purposeRequestDto) {
        Purpose purpose = new Purpose();
        BeanUtils.copyProperties(purposeRequestDto, purpose);
        return purpose;
    }

    public static Purpose purposeRequestDtoToPurpose(Purpose purpose, PurposeRequestDto purposeRequestDto) {
        BeanUtils.copyProperties(purposeRequestDto, purpose);
        return purpose;
    }

    public static PurposeResponseDto purposeToPurposeResponseDto(Purpose purpose) {
        PurposeResponseDto purposeResponseDto = new PurposeResponseDto();
        BeanUtils.copyProperties(purpose, purposeResponseDto);
        return purposeResponseDto;
    }
}