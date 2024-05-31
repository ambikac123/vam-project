package com.dreamsol.services.impl;

import com.dreamsol.dtos.requestDtos.UnitRequestDto;
import com.dreamsol.dtos.responseDtos.UnitResponseDto;
import com.dreamsol.entites.Unit;
import com.dreamsol.exceptions.ResourceNotFoundException;
import com.dreamsol.repositories.UnitRepository;
import com.dreamsol.services.UnitService;
import com.dreamsol.utility.DtoUtilities;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UnitServiceImpl implements UnitService {

    private final UnitRepository unitRepository;

    @Override
    public UnitResponseDto createUnit(UnitRequestDto unitRequestDto) {
        Unit unit = DtoUtilities.unitRequestDtoToUnit(unitRequestDto);
        Optional<Unit> dbUnit = unitRepository.findByUnitNameIgnoreCaseOrUnitIp(unit.getUnitName(),
                unit.getUnitIp());
        if (dbUnit.isPresent()) {
            throw new RuntimeException("Unit with this details already exists UnitName: "
                    + unitRequestDto.getUnitName() + " ,UnitIp: " + unitRequestDto.getUnitIp());
        } else {
            Unit savedUnit = unitRepository.save(unit);
            return DtoUtilities.unitToUnitResponseDto(savedUnit);
        }
    }

    @Override
    public UnitResponseDto updateUnit(Long id, UnitRequestDto unitRequestDto) {
        Unit unit = unitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unit", "Id", id));
        Unit updatedUnit = DtoUtilities.unitRequestDtoToUnit(unit, unitRequestDto);
        updatedUnit = unitRepository.save(updatedUnit);
        return DtoUtilities.unitToUnitResponseDto(updatedUnit);
    }

    @Override
    public UnitResponseDto getUnitById(Long id) {
        Unit unit = unitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unit", "Id", id));
        return DtoUtilities.unitToUnitResponseDto(unit);
    }

    @Override
    public Page<UnitResponseDto> getUnits(Pageable pageable, String search) {
        LocalDateTime parsedDateTime = null;
        boolean parsedStatus = false;
        if (search != null) {
            // Attempt to parse LocalDateTime and boolean from search string
            try {
                parsedDateTime = LocalDateTime.parse(search, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            } catch (Exception ignored) {
                // Parsing failed, continue
            }

            try {
                parsedStatus = Boolean.parseBoolean(search);
            } catch (Exception ignored) {
                // Parsing failed, continue
            }

            // Search using parsed values
            return unitRepository
                    .findByUnitNameContainingIgnoreCaseOrUnitIpContainingIgnoreCaseOrUnitCityContainingIgnoreCaseOrPassAddressContainingIgnoreCaseOrPassDisclaimerContainingIgnoreCaseOrCreatedByContainingIgnoreCaseOrUpdatedByContainingIgnoreCaseOrStatusOrCreatedAtOrUpdatedAt(
                            search, search, search, search, search, search, search, parsedStatus, parsedDateTime,
                            parsedDateTime,
                            pageable)
                    .map(DtoUtilities::unitToUnitResponseDto);
        } else {
            return unitRepository.findAll(pageable).map(DtoUtilities::unitToUnitResponseDto);
        }
    }

    @Override
    public void deleteUnit(Long id) {
        Unit unit = unitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unit", "Id", id));
        unit.setStatus(false);
        unit.setUpdatedAt(LocalDateTime.now());
        unitRepository.save(unit);
    }
}
