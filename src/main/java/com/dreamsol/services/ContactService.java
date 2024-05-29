package com.dreamsol.services;

import com.dreamsol.dtos.requestDtos.ContactRequestDto;
import com.dreamsol.dtos.responseDtos.ContactResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ContactService {
    ContactResponseDto createContact(ContactRequestDto contactRequestDto);

    ContactResponseDto updateContact(Long id, ContactRequestDto contactRequestDto);

    ContactResponseDto getContactById(Long id);

    Page<ContactResponseDto> getContacts(Pageable pageable, String search);

    void deleteContact(Long id);
}
