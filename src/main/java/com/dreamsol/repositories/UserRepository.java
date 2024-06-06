package com.dreamsol.repositories;

import com.dreamsol.entites.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    User findByEmail(String username);
    Optional<User> findByEmailOrMobile(String email, Long mobile);

    User findByIdAndStatusTrue(Long id);
}
