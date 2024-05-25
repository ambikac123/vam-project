package com.dreamsol.repositories;

import com.dreamsol.entites.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
    User findByEmail(String username);
    User findByEmailOrMobile(String email, Long mobile);

    User findByIdAndStatusTrue(Long id);
}
