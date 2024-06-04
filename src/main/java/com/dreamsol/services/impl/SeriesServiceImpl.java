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
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SeriesServiceImpl implements SeriesService {

    private final SeriesRepository seriesRepository;
    private int num;

    @Override
    public SeriesResponseDto createSeries(SeriesRequestDto seriesRequestDto) {
        num = 0;
        Series series = DtoUtilities.seriesRequestDtoToSeries(seriesRequestDto);
        Optional<List<Series>> dbSeries = seriesRepository
                .findBySeriesForIgnoreCaseAndSubPrefixIgnoreCase(seriesRequestDto.getSeriesFor(),
                        seriesRequestDto.getSubPrefix());
        if (dbSeries.isPresent() && dbSeries.get().size() > 0) {
            dbSeries.get().stream()
                    .forEach(numSeries -> num = numSeries.getNumberSeries() > num ? numSeries.getNumberSeries() : num);
            series.setNumberSeries(num + 1);
            series.setPrefix(series.getSeriesFor().substring(0, 3).toUpperCase());
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
        num = 0;
        Series series = seriesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Series", "Id", id));
        Optional<List<Series>> dbSeries = seriesRepository
                .findBySeriesForIgnoreCaseAndSubPrefixIgnoreCase(seriesRequestDto.getSeriesFor(),
                        series.getSubPrefix());
        if (dbSeries.isPresent()) {
            dbSeries.get().stream()
                    .forEach(numSeries -> num = numSeries.getNumberSeries() > num ? numSeries.getNumberSeries() : num);
            series.setNumberSeries(num + 1);
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
    public Page<SeriesResponseDto> getSeries(Pageable pageable, String status, Long unitId, String seriesFor) {

        boolean bool = false;
        if (status != null) {
            try {
                bool = Boolean.parseBoolean(status);
            } catch (Exception e) {
                return seriesRepository.findBySeriesForAndUnitId(pageable, seriesFor, unitId)
                        .map(DtoUtilities::seriesToSeriesResponseDto);
            }
            return seriesRepository.findBySeriesForAndUnitIdAndStatus(pageable, seriesFor, unitId, bool)
                    .map(DtoUtilities::seriesToSeriesResponseDto);
        }

        return seriesRepository.findAll(pageable).map(DtoUtilities::seriesToSeriesResponseDto);

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
