package spring.bricole.service;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import spring.bricole.common.ApplicationState;
import spring.bricole.model.Job;
import spring.bricole.repository.JobRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class JobService {

    private final JobRepository jobRepository;

    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    // Get all jobs
    public List<Job> getAllJobs() {
        return jobRepository.findAllByOrderByCreatedAtDesc();
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
        job.setApplicants(jobDetails.getApplicants());
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


    public Map<Integer, ApplicationState> getApplicationsByJobId(int jobId) {
        Job jobFound = jobRepository.findById(jobId).orElseThrow(() -> new RuntimeException("Job not found"));
        return jobFound.getApplicants();
    }

    public Map<Job, ApplicationState> getApplicationsByEmployeeId(int employeeId) {
        List<Job> jobs = jobRepository.findAll();

        Map<Job, ApplicationState> employeeApplications = new HashMap<>();

        for (Job job : jobs) {
            for (Map.Entry<Integer, ApplicationState> entry : job.getApplicants().entrySet()) {
                if (entry.getKey() == employeeId) {
                    employeeApplications.put(job, entry.getValue());
                }
            }
        }
        return employeeApplications;
    }

    public List<Job> findByTitle(String title) {
        return jobRepository.findByTitleOrderByCreatedAtDesc(title);
    }

    public List<Job> findByDescription(String description) {
        return jobRepository.findByDescriptionContaining(description);
    }
    public List<Job> findByCategory(String category) {
        return jobRepository.findByCategoryOrderByCategoryDesc(category);
    }
    public List<Job> findByLocation(String location) {
        return jobRepository.findByLocationContainingOrderByCreatedAtDesc(location);
    }
    public List<Job> findByJobStatus(String status) {
        return jobRepository.findAllByStatus(status);
    }
    public List<Job> findHighestPayingJobs() {
        return jobRepository.findTop50ByOrderBySalaryDesc();
    }

    // find most trending jobs
    public List<Job> findTrendingJobs() {
        return jobRepository
                .findAllByOrderByCreatedAtDesc()
                .stream()
                .sorted((job1, job2) -> Integer.compare(job2.getApplicants().size(), job1.getApplicants().size()))
                .collect(Collectors.toList());
    }

    // find jobs by employer
    public List<Job> findByEmployerId(int employerId) {
        return jobRepository.findAll()
                .stream()
                .filter(job -> job.getEmployer().getId() == employerId)
                .collect(Collectors.toList());
    }
}