package com.dreamsol.services.impl;

import com.dreamsol.dtos.requestDtos.ContactRequestDto;
import com.dreamsol.dtos.responseDtos.ContactResponseDto;
import com.dreamsol.entites.Contact;
import com.dreamsol.entites.Department;
import com.dreamsol.exceptions.ResourceNotFoundException;
import com.dreamsol.repositories.ContactRepository;
import com.dreamsol.repositories.DepartmentRepository;
import com.dreamsol.services.ContactService;
import com.dreamsol.utility.DtoUtilities;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class ContactServiceImpl implements ContactService {

    private final ContactRepository contactRepository;

    private final DepartmentRepository departmentRepository;

    @Override
    public ContactResponseDto createContact(ContactRequestDto contactRequestDto) {
        if (contactRepository.existsByEmail(contactRequestDto.getEmail())) {
            throw new RuntimeException("Contact email already exists");
        }
        if (contactRepository.existsByMobileNumber(contactRequestDto.getMobileNumber())) {
            throw new RuntimeException("Contact mobile number already exists");
        }
        if (contactRepository.existsByEmployeeId(contactRequestDto.getEmployeeId())) {
            throw new RuntimeException("Employee Id already exists");
        }

        // Check if the department exists
        Department department = departmentRepository
                .findByDepartmentCode(contactRequestDto.getDepartment().getDepartmentCode())
                .orElseThrow(() -> new RuntimeException("Choose a valid department"));

        Contact contact = DtoUtilities.contactRequestDtoToContact(contactRequestDto);
        contact.setDepartment(department);

        Contact savedContact = contactRepository.save(contact);
        return DtoUtilities.contactToContactResponseDto(savedContact);
    }

    @Override
    public ContactResponseDto updateContact(Long id, ContactRequestDto contactRequestDto) {
        // Find the existing contact
        Contact existingContact = contactRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contact", "Id", id));

        // Check if the new email or mobile number already exists (excluding the current
        // contact)
        if (!existingContact.getEmail().equals(contactRequestDto.getEmail()) &&
                contactRepository.existsByEmail(contactRequestDto.getEmail())) {
            throw new RuntimeException("Contact email already exists");
        }
        if (existingContact.getMobileNumber() != contactRequestDto.getMobileNumber() &&
                contactRepository.existsByMobileNumber(contactRequestDto.getMobileNumber())) {
            throw new RuntimeException("Contact mobile number already exists");
        }

        // Check if the department exists
        Department department = departmentRepository
                .findByDepartmentCode(contactRequestDto.getDepartment().getDepartmentCode())
                .orElseThrow(() -> new RuntimeException("Choose a valid department"));

        // Update contact fields
        Contact updatedContact = DtoUtilities.contactRequestDtoToContact(existingContact, contactRequestDto);
        updatedContact.setDepartment(department);

        // Save the updated contact
        updatedContact = contactRepository.save(updatedContact);
        return DtoUtilities.contactToContactResponseDto(updatedContact);
    }

    @Override
    public ContactResponseDto getContactById(Long id) {
        Contact contact = contactRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contact", "Id", id));
        return DtoUtilities.contactToContactResponseDto(contact);
    }

    @Override
    public Page<ContactResponseDto> getContacts(Pageable pageable, String search) {
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
            return contactRepository
                    .findByEmployeeIdContainingIgnoreCaseOrMobileNumberOrEmailContainingIgnoreCaseOrContactNameContainingIgnoreCaseOrCommunicationNameContainingIgnoreCaseOrStatusOrCreatedAtOrUpdatedAt(
                            search, parsedStatus ? Long.parseLong(search) : -1, search, search, search, parsedStatus,
                            parsedDateTime, parsedDateTime, pageable)
                    .map(DtoUtilities::contactToContactResponseDto);
        } else {
            return contactRepository.findAll(pageable).map(DtoUtilities::contactToContactResponseDto);
        }
    }

    @Override
    public void deleteContact(Long id) {
        Contact contact = contactRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contact", "Id", id));
        contact.setStatus(false);
        contact.setUpdatedAt(LocalDateTime.now());
        contactRepository.save(contact);
    }
}
