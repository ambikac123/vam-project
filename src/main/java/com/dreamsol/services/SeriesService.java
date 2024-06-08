package com.dreamsol.services;

import com.dreamsol.dtos.requestDtos.SeriesRequestDto;
import com.dreamsol.dtos.responseDtos.SeriesResponseDto;
import org.springframework.http.ResponseEntity;

public interface SeriesService {
    ResponseEntity<SeriesResponseDto> createSeries(SeriesRequestDto seriesRequestDto);

    ResponseEntity<SeriesResponseDto> updateSeries(Long id, SeriesRequestDto seriesRequestDto);

    ResponseEntity<SeriesResponseDto> getSeriesById(Long id);

    ResponseEntity<?> getSeries(String purposeFor, int pageSize, int page, String sortBy, String SortDirection,
            String status, Long unitIdr);

    ResponseEntity<?> deleteSeries(Long id);

    ResponseEntity<?> downloadSeriesDataAsExcel();

    ResponseEntity<?> downloadSeriesExcelSample() throws java.io.IOException;

    ResponseEntity<?> getDropDown();

}