package com.dreamsol.services;

import com.dreamsol.dtos.requestDtos.ContactRequestDto;
import com.dreamsol.dtos.responseDtos.ContactResponseDto;
import org.springframework.http.ResponseEntity;

public interface ContactService {
    ResponseEntity<ContactResponseDto> createContact(ContactRequestDto contactRequestDto);

    ResponseEntity<ContactResponseDto> updateContact(Long id, ContactRequestDto contactRequestDto);

    ResponseEntity<ContactResponseDto> getContactById(Long id);

    ResponseEntity<?> getContacts(int pageSize, int page, String sortBy, String sortDirection, String status,
            Long unitId, Long departmentId);

    ResponseEntity<?> deleteContact(Long id);

    ResponseEntity<?> downloadContactDataAsExcel();

    ResponseEntity<?> downloadContactExcelSample() throws java.io.IOException;

    ResponseEntity<?> getDropDown();

}
