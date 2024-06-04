package com.dreamsol.repositories;

import com.dreamsol.entites.UserType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserTypeRepository extends JpaRepository<UserType,Long> {
    Optional<UserType> findByUserTypeNameOrUserTypeCodeAndStatusTrue(String userTypeName, String userTypeCode);

    Optional<UserType> findByIdAndStatusTrue(Long id);

    Optional<UserType> findByUserTypeCode(String userTypeCode);
}
