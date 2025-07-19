package com.arijit.job_management_api.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.arijit.job_management_api.model.JobPost;
import com.arijit.job_management_api.repository.JobRepository;

@Service
public class JobService {

    @Autowired
    private JobRepository jobRepository;

    public JobPost getJob(int id) {
        return jobRepository.findById(id).orElse(new JobPost());
    }

    public List<JobPost> getAllJobs() {
        return jobRepository.findAll();
    }

    public void addJob(JobPost job) {
        jobRepository.save(job);
    }

    public void updateJob(JobPost job) {
        jobRepository.save(job);
    }

    public void deleteJob(int id) {
        jobRepository.deleteById(id);
    }

    public List<JobPost> search(String keyword) {
        return jobRepository.searchJob(keyword);
    }

    public void load() {
        List<JobPost> jobs = new ArrayList<>(List.of(
                new JobPost(1, "Software Engineer", "Exciting opportunity for a skilled software engineer.", 3, List.of("Java", "Spring", "SQL")),
                new JobPost(2, "Data Scientist", "Join our data science team and work on cutting-edge projects.", 5, List.of("Python", "Machine Learning", "TensorFlow")),
                new JobPost(3, "Frontend Developer", "Create amazing user interfaces with our talented frontend team.", 2, List.of("JavaScript", "React", "CSS")),
                new JobPost(4, "Network Engineer", "Design and maintain our robust network infrastructure.", 4, List.of("Cisco", "Routing", "Firewalls")),
                new JobPost(5, "UX Designer", "Shape the user experience with your creative design skills.", 3, List.of("UI/UX Design", "Adobe XD", "Prototyping"))
        ));

        jobRepository.saveAll(jobs);
    }
}
