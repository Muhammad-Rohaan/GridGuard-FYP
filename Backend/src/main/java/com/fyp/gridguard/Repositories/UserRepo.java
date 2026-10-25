package com.fyp.gridguard.Repositories;

import com.fyp.gridguard.Models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepo extends JpaRepository<Users, Long> {

    Users findByEmail(String email);

    @Query(value = "select full_name, roles from users where roles='ADMIN'", nativeQuery = true)
    List<String> findByAdminName();



}
