package com.arijit.job_management_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.arijit.job_management_api.model.JobPost;

public interface JobRepository extends JpaRepository<JobPost, Integer> {

    @Query("SELECT j FROM JobPost j WHERE "
            + "LOWER(j.postProfile) LIKE LOWER(concat('%', :keyword, '%')) OR "
            + "LOWER(j.postDesc) LIKE LOWER(concat('%', :keyword, '%'))")
    List<JobPost> searchJob(String keyword);
}
