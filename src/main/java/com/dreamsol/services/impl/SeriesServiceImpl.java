package com.dreamsol.services.impl;

import com.dreamsol.dtos.requestDtos.SeriesRequestDto;
import com.dreamsol.dtos.responseDtos.SeriesResponseDto;
import com.dreamsol.entites.Series;
import com.dreamsol.repositories.SeriesRepository;
import com.dreamsol.services.SeriesService;
import com.dreamsol.exceptions.ResourceNotFoundException;
import com.dreamsol.utility.DtoUtilities;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SeriesServiceImpl implements SeriesService {

    private final SeriesRepository seriesRepository;
    int num = 0;

    @Override
    public SeriesResponseDto createSeries(SeriesRequestDto seriesRequestDto) {
        Series series = DtoUtilities.seriesRequestDtoToSeries(seriesRequestDto);
        Optional<List<Series>> dbSeries = seriesRepository
                .findBySeriesForIgnoreCaseAndPrefixIgnoreCase(seriesRequestDto.getSeriesFor(), series.getPrefix());
        if (dbSeries.isPresent()) {
            dbSeries.get().stream()
                    .map(numSeries -> num = numSeries.getNumberSeries() > num ? numSeries.getNumberSeries() : num);
            series.setNumberSeries(num);
            Series savedSeries = seriesRepository.save(series);
            return DtoUtilities.seriesToSeriesResponseDto(savedSeries);
        } else {
            series.setPrefix(series.getSeriesFor().substring(0, 3).toUpperCase());
            Series savedSeries = seriesRepository.save(series);
            return DtoUtilities.seriesToSeriesResponseDto(savedSeries);
        }
    }

    @Override
    public SeriesResponseDto updateSeries(Long id, SeriesRequestDto seriesRequestDto) {
        Series series = seriesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Series", "Id", id));
        Optional<List<Series>> dbSeries = seriesRepository
                .findBySeriesForIgnoreCaseAndPrefixIgnoreCase(seriesRequestDto.getSeriesFor(), series.getPrefix());
        if (dbSeries.isPresent()) {
            dbSeries.get().stream()
                    .map(numSeries -> num = numSeries.getNumberSeries() > num ? numSeries.getNumberSeries() : num);
            series.setNumberSeries(num);
            Series savedSeries = seriesRepository.save(series);
            return DtoUtilities.seriesToSeriesResponseDto(savedSeries);
        } else {
            series.setPrefix(series.getSeriesFor().substring(0, 3).toUpperCase());
            Series savedSeries = seriesRepository.save(series);
            return DtoUtilities.seriesToSeriesResponseDto(savedSeries);
        }
    }

    @Override
    public SeriesResponseDto getSeriesById(Long id) {
        Series series = seriesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Series", "Id", id));
        return DtoUtilities.seriesToSeriesResponseDto(series);
    }

    @Override
    public Page<SeriesResponseDto> getSeries(Pageable pageable, String search) {
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
                // Parsing failed its ok
            }
            // Search using parsed values
            return seriesRepository
                    .findBySeriesForContainingIgnoreCaseOrPrefixContainingIgnoreCaseOrSubPrefixContainingIgnoreCaseOrCreatedByContainingIgnoreCaseOrUpdatedByContainingIgnoreCaseOrStatusOrCreatedAtOrUpdatedAt(
                            search, search, search, search, search, parsedStatus, parsedDateTime, parsedDateTime,
                            pageable)
                    .map(DtoUtilities::seriesToSeriesResponseDto);
        } else {
            return seriesRepository.findAll(pageable).map(DtoUtilities::seriesToSeriesResponseDto);
        }
    }

    @Override
    public void deleteSeries(Long id) {
        Series series = seriesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Series", "Id", id));
        if (!series.isStatus()) {
            throw new ResourceNotFoundException("Series", "Id", id);
        } else {
            series.setStatus(false);
            series.setUpdatedAt(LocalDateTime.now());
            seriesRepository.save(series);
        }
    }
}
