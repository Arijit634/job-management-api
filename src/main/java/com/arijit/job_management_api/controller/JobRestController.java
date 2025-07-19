package com.arijit.job_management_api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.arijit.job_management_api.model.JobPost;
import com.arijit.job_management_api.service.JobService;

/**
 * REST Controller for job-related operations
 */
@RestController
public class JobRestController {

    @Autowired
    private JobService jobService;

    @GetMapping("/jobPost/{postId}")
    public JobPost getJob(@PathVariable int postId) {
        return jobService.getJob(postId);
    }

    @GetMapping("/allJobs")
    public List<JobPost> getAllJobs() {
        return jobService.getAllJobs();
    }

    @PostMapping("/jobPost")
    public JobPost addJob(@RequestBody JobPost jobPost) {
        jobService.addJob(jobPost);
        return jobService.getJob(jobPost.getPostId());
    }

    @PutMapping("/jobPost")
    public JobPost updateJob(@RequestBody JobPost jobPost) {
        jobService.updateJob(jobPost);
        return jobService.getJob(jobPost.getPostId());
    }

    @DeleteMapping("/jobPost/{postId}")
    public String deleteJob(@PathVariable int postId) {
        if (jobService.getJob(postId) != null) {
            jobService.deleteJob(postId);
            return "Deleted job with id: " + postId;
        }
        return "Job with id: " + postId + " doesn't exist!";
    }

    @GetMapping("/load")
    public String load() {
        jobService.load();
        return "Success";
    }

    @GetMapping("/jobPost/search")
    public List<JobPost> searchByKeyword(@RequestParam String keyword) {
        return jobService.search(keyword);
    }

}
