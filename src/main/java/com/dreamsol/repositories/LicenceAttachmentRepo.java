package com.dreamsol.repositories;

import com.dreamsol.entites.LicenceAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LicenceAttachmentRepo extends JpaRepository<LicenceAttachment,Long> {

    public Optional<LicenceAttachment> findByGeneratedFileName(String fileName);
}
