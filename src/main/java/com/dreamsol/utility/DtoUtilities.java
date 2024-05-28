package com.dreamsol.utility;

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
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

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
        Set<Department> departments = unit.getDepartments();
        if (departments != null) {
            Set<DepartmentResponseDto> res = departments.stream()
                    .map(DtoUtilities::departmentToDepartmentResponseDtoForUnit)
                    .collect(Collectors.toSet());
            unitResponseDto.setDepartments(res);
        }
        return unitResponseDto;
    }

    public static UnitResponseDto unitToUnitResponseDtoForDep(Unit unit) {
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
        departmentResponseDto.setUnit(DtoUtilities.unitToUnitResponseDtoForDep(department.getUnit()));
        return departmentResponseDto;
    }

    public static DepartmentResponseDto departmentToDepartmentResponseDtoForUnit(Department department) {
        DepartmentResponseDto departmentResponseDto = new DepartmentResponseDto();
        BeanUtils.copyProperties(department, departmentResponseDto);
        return departmentResponseDto;
    }

    public static Contact contactRequestDtoToContact(ContactRequestDto contactRequestDto) {
        Contact contact = new Contact();
        BeanUtils.copyProperties(contactRequestDto, contact);
        contact.setUnit(DtoUtilities.unitRequestDtoToUnit(contactRequestDto.getUnit()));
        contact.setDepartment(DtoUtilities.departmentRequestDtoToDepartment(contactRequestDto.getDepartment()));
        contact.setCreatedAt(LocalDateTime.now());
        contact.setUpdatedAt(LocalDateTime.now());
        return contact;
    }

    public static Contact contactRequestDtoToContact(Contact contact, ContactRequestDto contactRequestDto) {
        BeanUtils.copyProperties(contactRequestDto, contact);
        contact.setUnit(DtoUtilities.unitRequestDtoToUnit(contactRequestDto.getUnit()));
        contact.setDepartment(DtoUtilities.departmentRequestDtoToDepartment(contactRequestDto.getDepartment()));
        contact.setUpdatedAt(LocalDateTime.now());
        return contact;
    }

    public static ContactResponseDto contactToContactResponseDto(Contact contact) {
        ContactResponseDto contactResponseDto = new ContactResponseDto();
        BeanUtils.copyProperties(contact, contactResponseDto);
        contactResponseDto.setUnit(DtoUtilities.unitToUnitResponseDtoForDep(contact.getUnit()));
        contactResponseDto
                .setDepartment(DtoUtilities.departmentToDepartmentResponseDtoForUnit(contact.getDepartment()));
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
