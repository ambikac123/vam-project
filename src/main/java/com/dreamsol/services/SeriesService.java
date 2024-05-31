package com.dreamsol.services;

import com.dreamsol.dtos.requestDtos.SeriesRequestDto;
import com.dreamsol.dtos.responseDtos.SeriesResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SeriesService {
    SeriesResponseDto createSeries(SeriesRequestDto seriesRequestDto);

    SeriesResponseDto updateSeries(Long id, SeriesRequestDto seriesRequestDto);

    SeriesResponseDto getSeriesById(Long id);

    Page<SeriesResponseDto> getSeries(Pageable pageable, String search);

    void deleteSeries(Long id);
}