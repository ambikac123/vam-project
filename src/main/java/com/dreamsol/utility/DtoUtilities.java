package com.dreamsol.utility;

import com.dreamsol.dtos.requestDtos.DrivingLicenceReqDto;
import com.dreamsol.dtos.requestDtos.SeriesRequestDto;
import com.dreamsol.dtos.requestDtos.UserRequestDto;
import com.dreamsol.dtos.requestDtos.UserTypeRequestDto;
import com.dreamsol.dtos.requestDtos.VehicleLicenceReqDto;
import com.dreamsol.dtos.responseDtos.DrivingLicenceResDto;
import com.dreamsol.dtos.responseDtos.SeriesResponseDto;
import com.dreamsol.dtos.responseDtos.UserResponseDto;
import com.dreamsol.dtos.responseDtos.UserTypeResponseDto;
import com.dreamsol.dtos.responseDtos.VehicleLicenceResDto;
import com.dreamsol.entites.DrivingLicence;
import com.dreamsol.dtos.requestDtos.DepartmentRequestDto;
import com.dreamsol.dtos.requestDtos.PlantRequestDto;
import com.dreamsol.dtos.requestDtos.PurposeRequestDto;
import com.dreamsol.dtos.requestDtos.UnitRequestDto;
import com.dreamsol.dtos.responseDtos.DepartmentResponseDto;
import com.dreamsol.dtos.responseDtos.PlantResponseDto;
import com.dreamsol.dtos.responseDtos.PurposeResponseDto;
import com.dreamsol.dtos.responseDtos.UnitResponseDto;
import com.dreamsol.entites.Department;
import com.dreamsol.entites.Plant;
import com.dreamsol.entites.Purpose;
import com.dreamsol.entites.Series;
import com.dreamsol.entites.Unit;
import com.dreamsol.entites.User;
import com.dreamsol.entites.UserType;
import com.dreamsol.entites.VehicleLicence;
import com.dreamsol.dtos.requestDtos.*;
import com.dreamsol.dtos.responseDtos.*;
import com.dreamsol.entites.*;
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

    public UserType userTypeRequestDtoToUserType(UserTypeRequestDto userTypeRequestDto) {
        UserType userType = new UserType();
        BeanUtils.copyProperties(userTypeRequestDto, userType);
        return userType;
    }

    public UserTypeResponseDto userTypeToUserTypeResponseDto(UserType userType) {
        UserTypeResponseDto userTypeResponseDto = new UserTypeResponseDto();
        BeanUtils.copyProperties(userType, userTypeResponseDto);
        return userTypeResponseDto;
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
        VehicleLicence vehicleLicence = new VehicleLicence();
        BeanUtils.copyProperties(vehicleLicenceReqDto, vehicleLicence);
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
        // plant.setCreatedBy(util.getCurrentLoginUser());
        // plant.setUpdatedBy(util.getCurrentLoginUser());
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
        // unit.setUnitId(unit.getId());
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
        return department;
    }

    public static Department departmentRequestDtoToDepartment(Department department,
            DepartmentRequestDto departmentRequestDto) {
        BeanUtils.copyProperties(departmentRequestDto, department);
        department.setUpdatedAt(LocalDateTime.now());
        return department;
    }

    public static DepartmentResponseDto departmentToDepartmentResponseDto(Department department) {
        DepartmentResponseDto departmentResponseDto = new DepartmentResponseDto();
        BeanUtils.copyProperties(department, departmentResponseDto);
        return departmentResponseDto;
    }

    public static Purpose purposeRequestDtoToPurpose(PurposeRequestDto purposeRequestDto) {
        Purpose purpose = new Purpose();
        purpose.setCreatedAt(LocalDateTime.now());
        purpose.setUpdatedAt(LocalDateTime.now());
        BeanUtils.copyProperties(purposeRequestDto, purpose);
        return purpose;
    }

    public static Purpose purposeRequestDtoToPurpose(Purpose purpose, PurposeRequestDto purposeRequestDto) {
        BeanUtils.copyProperties(purposeRequestDto, purpose);
        purpose.setUpdatedAt(LocalDateTime.now());
        return purpose;
    }

    public static PurposeResponseDto purposeToPurposeResponseDto(Purpose purpose) {
        PurposeResponseDto purposeResponseDto = new PurposeResponseDto();
        BeanUtils.copyProperties(purpose, purposeResponseDto);
        return purposeResponseDto;
    }

    public static Series seriesRequestDtoToSeries(SeriesRequestDto seriesRequestDto) {
        Series series = new Series();
        BeanUtils.copyProperties(seriesRequestDto, series);
        series.setCreatedAt(LocalDateTime.now());
        series.setUpdatedAt(LocalDateTime.now());
        return series;
    }

    public static Series seriesRequestDtoToSeries(Series series, SeriesRequestDto seriesRequestDto) {
        BeanUtils.copyProperties(seriesRequestDto, series);
        series.setUpdatedAt(LocalDateTime.now());
        return series;
    }

    public static SeriesResponseDto seriesToSeriesResponseDto(Series series) {
        SeriesResponseDto seriesResponseDto = new SeriesResponseDto();
        BeanUtils.copyProperties(series, seriesResponseDto);
        return seriesResponseDto;
    }

    public VehicleEntry vehicleEntryDtoToVehicleEntry(VehicleEntryReqDto vehicleEntryReqDto,
            DrivingLicence drivingLicence, VehicleLicence vehicleLicence, Plant plant, Purpose purpose) {
        VehicleEntry vehicleEntry = new VehicleEntry();
        BeanUtils.copyProperties(vehicleEntryReqDto, vehicleEntry);
        vehicleEntry.setDrivingLicence(drivingLicence);
        vehicleEntry.setVehicleLicence(vehicleLicence);
        vehicleEntry.setPlant(plant);
        vehicleEntry.setPurpose(purpose);
        return vehicleEntry;
    }

    public VehicleEntryResDto vehicleEntryToDto(VehicleEntry savedVehicleEntry) {
        VehicleEntryResDto vehicleEntryResDto = new VehicleEntryResDto();
        BeanUtils.copyProperties(savedVehicleEntry, vehicleEntryResDto);
        vehicleEntryResDto.setVehicleNumber(savedVehicleEntry.getVehicleLicence().getVehicleNumber());
        vehicleEntryResDto.setVehicleOwner(savedVehicleEntry.getVehicleLicence().getVehicleOwner());
        vehicleEntryResDto.setVehicleType(savedVehicleEntry.getVehicleLicence().getVehicleType());
        vehicleEntryResDto.setDriverName(savedVehicleEntry.getDrivingLicence().getDriverName());
        vehicleEntryResDto.setDriverMobileNumber(savedVehicleEntry.getDrivingLicence().getDriverMobile());
        vehicleEntryResDto.setPlantTo(savedVehicleEntry.getPlant().getPlantName());
        vehicleEntryResDto.setVisitPurpose(savedVehicleEntry.getPurpose().getPurposeFor());
        return vehicleEntryResDto;
    }
}
