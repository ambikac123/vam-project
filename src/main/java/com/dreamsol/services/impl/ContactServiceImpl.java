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
import com.dreamsol.utility.ExcelUtility;

import lombok.RequiredArgsConstructor;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContactServiceImpl implements ContactService {

    private final ContactRepository contactRepository;

    private final DepartmentRepository departmentRepository;

    private final ExcelUtility excelUtility;

    @Override
    public ResponseEntity<ContactResponseDto> createContact(ContactRequestDto contactRequestDto) {
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
                .findByDepartmentNameIgnoreCaseAndDepartmentCodeIgnoreCase(
                        contactRequestDto.getDepartment().getDepartmentName(),
                        contactRequestDto.getDepartment().getDepartmentCode())
                .orElseThrow(() -> new RuntimeException("Choose a valid department"));

        Contact contact = DtoUtilities.contactRequestDtoToContact(contactRequestDto);
        contact.setDepartment(department);

        Contact savedContact = contactRepository.save(contact);
        return ResponseEntity.ok().body(DtoUtilities.contactToContactResponseDto(savedContact));
    }

    @Override
    public ResponseEntity<ContactResponseDto> updateContact(Long id, ContactRequestDto contactRequestDto) {
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
                .findByDepartmentCodeIgnoreCase(contactRequestDto.getDepartment().getDepartmentCode())
                .orElseThrow(() -> new RuntimeException("Choose a valid department"));

        // Update contact fields
        Contact updatedContact = DtoUtilities.contactRequestDtoToContact(existingContact, contactRequestDto);
        updatedContact.setDepartment(department);

        // Save the updated contact
        updatedContact = contactRepository.save(updatedContact);
        return ResponseEntity.ok().body(DtoUtilities.contactToContactResponseDto(updatedContact));
    }

    @Override
    public ResponseEntity<ContactResponseDto> getContactById(Long id) {
        Contact contact = contactRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contact", "Id", id));
        return ResponseEntity.ok().body(DtoUtilities.contactToContactResponseDto(contact));
    }

    @Override
    public ResponseEntity<?> getContacts(int pageSize, int page, String sortBy, String sortDirection, String status,
            Long unitId, Integer departmentId) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        PageRequest pageRequest = PageRequest.of(page, pageSize, Sort.by(direction, sortBy));

        Boolean statusBoolean = status != null ? Boolean.parseBoolean(status) : null;
        Page<Contact> contactsPage = contactRepository.findByFilters(statusBoolean, unitId, departmentId,
                pageRequest);

        Page<ContactResponseDto> contactResponseDtos = contactsPage.map(DtoUtilities::contactToContactResponseDto);
        return ResponseEntity.ok(contactResponseDtos);
    }

    @Override
    public ResponseEntity<?> deleteContact(Long id) {
        Contact contact = contactRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contact", "Id", id));
        if (contact.isStatus()) {
            contact.setStatus(false);
            contact.setUpdatedAt(LocalDateTime.now());
            contactRepository.save(contact);
            return ResponseEntity.ok().body("Contact has been deleted");
        } else {
            throw new ResourceNotFoundException("Contact", "Id", id);
        }
    }

    @Override
    public ResponseEntity<?> downloadContactDataAsExcel() {
        try {
            List<Contact> contactList = contactRepository.findAll();
            if (contactList.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No contacts available!");
            }

            List<ContactResponseDto> contactResDtoList = contactList.stream()
                    .map(DtoUtilities::contactToContactResponseDto)
                    .collect(Collectors.toList());

            String fileName = "contact_excel_data.xlsx";
            String sheetName = fileName.substring(0, fileName.indexOf('.'));
            Resource resource = excelUtility.downloadDataAsExcel(contactResDtoList, sheetName);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName)
                    .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error! " + e);
        }
    }

    @Override
    public ResponseEntity<?> downloadContactExcelSample() throws IOException {
        String fileName = "contact_excel_sample.xlsx";
        String sheetName = fileName.substring(0, fileName.indexOf('.'));
        Resource resource = excelUtility.downloadExcelSample(ContactRequestDto.class, sheetName);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(resource);
    }

    public Optional<Contact> getContact(String employeeId) {
        return contactRepository.findByEmployeeIdIgnoreCase(employeeId);
    }
}
