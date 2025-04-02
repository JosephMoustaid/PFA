package spring.bricole.service;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import spring.bricole.model.Job;
import spring.bricole.repository.JobRepository;

import java.util.List;
import java.util.Map;

@Service
public class JobService {

    private final JobRepository jobRepository;

    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    // Get all jobs
    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    // Get job by ID
    public Job getJobById(Integer id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));
    }

    // Create new job
    public Job createJob(Job job) {
        // Add any business logic here (validation, etc.)
        return jobRepository.save(job);
    }

    // Update job
    public Job updateJob(Integer id, Job jobDetails) {
        Job job = getJobById(id);
        job.setTitle(jobDetails.getTitle());
        job.setDescription(jobDetails.getDescription());
        job.setCategory(jobDetails.getCategory());
        job.setStatus(jobDetails.getStatus());
        job.setLocation(jobDetails.getLocation());
        job.setSalary(jobDetails.getSalary());
        job.setCreatedAt(jobDetails.getCreatedAt());
        return jobRepository.save(job);
    }

    // Delete job by ID
    public boolean deleteJobById(Integer id) {
        if(jobRepository.existsById(id)) {
            jobRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    // delete job
    public boolean deleteJobById(int id) {
        if(jobRepository.existsById(id)){
            jobRepository.deleteById(id);
            return true;
        }
        return false;
    }


    public List<Object[]> getApplicationsByJobId(int jobId) {
        return jobRepository.findApplicationsByJobId(jobId);
    }

    public List<Object[]> getApplicationsByEmployeeId(int employeeId) {
        return jobRepository.findApplicationsByEmployeeId(employeeId);
    }
}