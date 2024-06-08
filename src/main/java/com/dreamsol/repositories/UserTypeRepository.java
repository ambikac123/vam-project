package com.dreamsol.repositories;

import com.dreamsol.entites.UserType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserTypeRepository extends JpaRepository<UserType,Long> {

    Optional<UserType> findByIdAndStatusTrue(Long id);

    Optional<UserType> findByUserTypeNameOrUserTypeCode(String userTypeName, String userTypeCode);

    Optional<UserType> findByUserTypeNameAndStatusTrue(String userTypeName);

    List<UserType> findAll(Specification<UserType> userTypeSpecification, Pageable pageable);

    @Query("SELECT u FROM UserType u WHERE " +
            "(:unitId IS NULL OR u.unitId = :unitId) AND " +
            "(:status IS NULL OR u.status = :status) AND " +
            "(:userTypeName IS NULL OR u.userTypeName = :userTypeName) AND " +
            "(:userTypeCode IS NULL OR u.userTypeCode = :userTypeCode) ")
    List<UserType> findByFilters(@Param("unitId") Long unitId,
                                @Param("status") Boolean status,
                                @Param("userTypeName") String userTypeName,
                                @Param("userTypeCode") String userTypeCode,Pageable pageable);
}
