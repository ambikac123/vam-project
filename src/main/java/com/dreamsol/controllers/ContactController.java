package com.dreamsol.controllers;

import java.io.IOException;

import javax.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.dreamsol.dtos.requestDtos.ContactRequestDto;
import com.dreamsol.dtos.responseDtos.ContactResponseDto;
import com.dreamsol.services.ContactService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/contacts")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    @PostMapping("/create-contact")
    public ResponseEntity<ContactResponseDto> createContact(
            @Valid @RequestBody ContactRequestDto contactRequestDto) {
        return contactService.createContact(contactRequestDto);
    }

    @PutMapping("/update-contact/{id}")
    public ResponseEntity<ContactResponseDto> updateContact(@PathVariable Long id,
            @Valid @RequestBody ContactRequestDto contactRequestDto) {
        return contactService.updateContact(id, contactRequestDto);
    }

    @GetMapping("/get-contact/{id}")
    public ResponseEntity<ContactResponseDto> getContactById(@PathVariable Long id) {
        return contactService.getContactById(id);
    }

    @GetMapping("/get-all-contacts")
    public ResponseEntity<?> getAllContacts(
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "ASC") String sortDirection,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long unitId,
            @RequestParam(required = false) Integer departmentId) {
        return contactService.getContacts(pageSize, page, sortBy, sortDirection, status, unitId, departmentId);
    }

    @DeleteMapping("/delete-contact/{id}")
    public ResponseEntity<Void> deleteContact(@PathVariable Long id) {
        contactService.deleteContact(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/download-excel-data", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<?> downloadExcelData() {
        return contactService.downloadContactDataAsExcel();
    }

    @GetMapping(value = "/download-excel-sample", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<?> downloadExcelSample() throws IOException {
        return contactService.downloadContactExcelSample();
    }
}
