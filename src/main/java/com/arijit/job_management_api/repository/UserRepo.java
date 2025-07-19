package com.arijit.job_management_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.arijit.job_management_api.model.User;

public interface UserRepo extends JpaRepository<User, Integer> {

    User findByUsername(String username);
}
