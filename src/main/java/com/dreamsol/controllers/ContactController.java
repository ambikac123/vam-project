package com.dreamsol.controllers;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
        ContactResponseDto contactResponseDto = contactService.createContact(contactRequestDto);
        return ResponseEntity.ok(contactResponseDto);
    }

    @PutMapping("/update-contact/{id}")
    public ResponseEntity<ContactResponseDto> updateContact(@PathVariable Long id,
            @Valid @RequestBody ContactRequestDto contactRequestDto) {
        ContactResponseDto contactResponseDto = contactService.updateContact(id, contactRequestDto);
        return ResponseEntity.ok(contactResponseDto);
    }

    @GetMapping("/get-contact/{id}")
    public ResponseEntity<ContactResponseDto> getContactById(@PathVariable Long id) {
        ContactResponseDto contactResponseDto = contactService.getContactById(id);
        return ResponseEntity.ok(contactResponseDto);
    }

    @GetMapping("/get-all-contacts")
    public ResponseEntity<Page<ContactResponseDto>> getAllContacts(
            @PageableDefault(size = 10, sort = "contactName", page = 0) Pageable pageable,
            @RequestParam(required = false) String search) {
        Page<ContactResponseDto> contactResponseDtos = contactService.getContacts(pageable, search);
        return ResponseEntity.ok(contactResponseDtos);
    }

    @DeleteMapping("/delete-contact/{id}")
    public ResponseEntity<Void> deleteContact(@PathVariable Long id) {
        contactService.deleteContact(id);
        return ResponseEntity.noContent().build();
    }
}
