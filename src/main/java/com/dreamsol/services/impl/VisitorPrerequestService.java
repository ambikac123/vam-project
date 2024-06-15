package com.dreamsol.services.impl;

import com.dreamsol.dtos.requestDtos.VisitorPrerequestDto;
import com.dreamsol.dtos.responseDtos.VisitorPrerequestResponseDto;
import com.dreamsol.entites.Purpose;
import com.dreamsol.entites.VisitorPrerequest;
import com.dreamsol.exceptions.ResourceNotFoundException;
import com.dreamsol.repositories.PurposeRepository;
import com.dreamsol.repositories.VisitorPrerequestRepository;
import com.dreamsol.utility.DtoUtilities;
import com.dreamsol.utility.ExcelUtility;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VisitorPrerequestService
{
    private final Logger logger = LoggerFactory.getLogger(VisitorPrerequestService.class);
    private final VisitorPrerequestRepository visitorRepository;
    private final PurposeRepository purposeRepository;
    private final DtoUtilities dtoUtilities;
    private final ExcelUtility excelUtility;

    public ResponseEntity<?> create(VisitorPrerequestDto visitorPrerequestDto) {
        try {
            VisitorPrerequest visitorPrerequest = dtoUtilities.visitorPrerequestDtoToVisitorPrerequest(visitorPrerequestDto);
            Optional<Purpose> purposeOptional = purposeRepository.findById(visitorPrerequestDto.getMeetingPurposeId());
            purposeOptional.ifPresent(visitorPrerequest::setMeetingPurpose);
            visitorPrerequest.setOtp(generateOTP());
            visitorPrerequest.setMeetingStatus(visitorPrerequestDto.getMeetingStatus());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            if(visitorPrerequestDto.getStartHours()==null) {
                visitorPrerequest.setStartHours(LocalTime.MIN);
            }else {
                LocalTime startHours = LocalTime.parse(visitorPrerequestDto.getStartHours(), formatter);
                visitorPrerequest.setStartHours(startHours);
            }
            if(visitorPrerequestDto.getEndHours()==null){
                visitorPrerequest.setEndHours(LocalTime.MAX);
            }else{
                LocalTime endHours = LocalTime.parse(visitorPrerequestDto.getEndHours(),formatter);
                visitorPrerequest.setEndHours(endHours);
            }
            visitorRepository.save(visitorPrerequest);
            logger.info("Pre-requested new visitor created successfully!");
            return ResponseEntity.status(HttpStatus.CREATED).body("Pre-requested new visitor created successfully!");
        }catch (Exception e){
            logger.error("Error occurred while creating new visitor, ",e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while creating new visitor:"+e.getMessage());
        }
    }
    public String generateOTP(){
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }
    public ResponseEntity<?> update(VisitorPrerequestDto visitorPrerequestDto, Long id)
    {
        try{
            VisitorPrerequest visitorPrerequest = visitorRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("pre-requested visitor","id",id));
            BeanUtils.copyProperties(visitorPrerequestDto,visitorPrerequest);
            Purpose purpose = purposeRepository.findById(visitorPrerequestDto.getMeetingPurposeId()).orElseThrow(()->new ResourceNotFoundException("purpose","purposeFor",visitorPrerequestDto.getMeetingPurposeId()));
            visitorPrerequest.setMeetingPurpose(purpose);
            visitorPrerequest.setMeetingStatus(visitorPrerequestDto.getMeetingStatus());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            if(visitorPrerequestDto.getStartHours()==null) {
                visitorPrerequest.setStartHours(LocalTime.MIN);
            }else {
                LocalTime startHours = LocalTime.parse(visitorPrerequestDto.getStartHours(), formatter);
                visitorPrerequest.setStartHours(startHours);
            }
            if(visitorPrerequestDto.getEndHours()==null){
                visitorPrerequest.setEndHours(LocalTime.MAX);
            }else{
                LocalTime endHours = LocalTime.parse(visitorPrerequestDto.getEndHours(),formatter);
                visitorPrerequest.setEndHours(endHours);
            }
            visitorRepository.save(visitorPrerequest);
            logger.info("Pre-requested visitor with id: "+id+" updated successfully!");
            return ResponseEntity.status(HttpStatus.OK).body("Pre-requested visitor with id: "+id+" updated successfully!");
        }catch(Exception e){
            logger.error("Error occurred while updating pre-requested visitor details, ",e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while updating pre-requested visitor details,"+e.getMessage());
        }
    }

    public ResponseEntity<?> delete(Long id)
    {
        try {
            VisitorPrerequest visitorPrerequest = visitorRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Pre-requested Visitor", "id", id));
            visitorPrerequest.setStatus(false);
            visitorRepository.save(visitorPrerequest);
            logger.info("Visitor prerequest with id: "+id+" deleted successfully!");
            return ResponseEntity.status(HttpStatus.OK).body("Visitor pre-request with id: "+id+" deleted successfully!");
        }catch (Exception e){
            logger.error("Error occurred while deleting visitor pre-request: "+e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while deleting visitor pre-request: "+e.getMessage());
        }
    }

    public ResponseEntity<?> get(Long id)
    {
        try {
            VisitorPrerequest visitorPrerequest = visitorRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Pre-requested Visitor", "id", id));
            logger.info("Pre-requested visitor found with id: " + id);
            return ResponseEntity.status(HttpStatus.FOUND).body(visitorPrerequest);
        }catch (Exception e){
            logger.error("Error occurred while fetching pre-requested visitor with id: "+id,e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while fetching pre-requested visitor with id: "+id+", "+e.getMessage());
        }
    }
    public ResponseEntity<?> getVisitorByMobile(Long mobile){
        try{
            Optional<VisitorPrerequest> visitorPrerequest = visitorRepository.findByMobile(mobile);
            if(visitorPrerequest.isPresent())
                return ResponseEntity.status(HttpStatus.OK).body(visitorPrerequest.get());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while fetching visitor details by mobile number");
        }
    }

    public ResponseEntity<?> getStatusCount(String meetingStatus,Long meetingPurposeId,LocalDate fromDate,LocalDate toDate){
        LocalDateTime from = fromDate.atStartOfDay();
        LocalDateTime to = toDate.atTime(LocalTime.MAX);
        List<VisitorPrerequest> prerequestList = visitorRepository.findByFilters(meetingStatus,meetingPurposeId,from,to);
        Map<String,Long> meetingStatusCount = new LinkedHashMap<>();
        long countPending = 0L;
        long countDone = 0L;
        long countRescheduled = 0L;
        long countCancelled = 0L;
        for(VisitorPrerequest visitorPrerequest : prerequestList){
            switch(visitorPrerequest.getMeetingStatus().toLowerCase()){
                case "pending": countPending++; break;
                case "done": countDone++; break;
                case "reschedule": countRescheduled++; break;
                case "cancel": countCancelled++;
            }
        }
        meetingStatusCount.put("Pending",countPending);
        meetingStatusCount.put("Done",countDone);
        meetingStatusCount.put("Rescheduled",countRescheduled);
        meetingStatusCount.put("Cancelled",countCancelled);
        return ResponseEntity.status(HttpStatus.OK).body(meetingStatusCount);
    }
    public ResponseEntity<?> getAll(Integer pageNumber, Integer pageSize, String sortBy, String sortDir, Long unitId, Boolean status, Long meetingPurposeId, String meetingStatus, String fromDate, String toDate) {
        try {
            if(LocalDate.parse(toDate).isBefore(LocalDate.parse(fromDate)) || LocalDate.parse(toDate).isAfter(LocalDate.now()))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("invalid date range!");
            Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
            Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
            LocalDateTime from = LocalDate.parse(fromDate).atStartOfDay();
            LocalDateTime to = LocalDate.parse(toDate).atTime(LocalTime.MAX);
            List<VisitorPrerequest> prerequestList = visitorRepository.findByFilters(unitId, status, meetingPurposeId, meetingStatus, from, to, pageable);
            logger.info("All visitors data fetched successfully!");
            return ResponseEntity.status(HttpStatus.OK).body(prerequestList);
        }catch (Exception e){
            logger.error("Error occurred while fetching all visitors: ",e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while fetching all visitors: "+e.getMessage());
        }
    }

    public ResponseEntity<?> downloadDataAsExcel(Long unitId, Boolean status, Long meetingPurposeId, String meetingStatus, String fromDate, String toDate)
    {
        try{
            if(LocalDate.parse(toDate).isBefore(LocalDate.parse(fromDate)) || LocalDate.parse(toDate).isAfter(LocalDate.now()))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("invalid date range!");
            LocalDateTime from = LocalDate.parse(fromDate).atStartOfDay();
            LocalDateTime to = LocalDate.parse(toDate).atTime(LocalTime.MAX);
            List<VisitorPrerequest> prerequestList = visitorRepository.findByFilters(unitId,status,meetingPurposeId,meetingStatus,from,to);
            if (prerequestList.isEmpty()) {
                logger.info("No visitors available!");
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No visitors available!");
            }
            List<VisitorPrerequestResponseDto> responseDtos = prerequestList.stream()
                    .map(dtoUtilities::visitorPrerequestToVisitorPrerequestResponseDto)
                    .collect(Collectors.toList());
            String fileName = "visitor_prerequest_excel_data.xlsx";
            String sheetName = fileName.substring(0,fileName.indexOf('.'));
            Resource resource = excelUtility.downloadDataAsExcel(responseDtos, sheetName);
            logger.info("pre-requested visitors data as excel file downloaded successfully!");
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName)
                    .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                    .body(resource);
        }catch (Exception e){
            logger.error("Error occurred while downloading data as excel file: ",e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while downloading data as excel file: "+e.getMessage());
        }
    }
}
