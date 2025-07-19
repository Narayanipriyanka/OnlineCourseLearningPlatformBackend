package com.Basics.onlineCoursePlatform.repository;

import com.Basics.onlineCoursePlatform.entity.User;
import com.Basics.onlineCoursePlatform.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findByRole(Role role);

}
