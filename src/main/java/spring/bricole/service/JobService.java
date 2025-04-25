package spring.bricole.service;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import spring.bricole.common.ApplicationState;
import spring.bricole.common.JobStatus;
import spring.bricole.model.Employer;
import spring.bricole.model.Job;
import spring.bricole.model.User;
import spring.bricole.repository.JobRepository;
import spring.bricole.util.Address;
import spring.bricole.util.JobFilter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class JobService {

    private final JobRepository jobRepository;

    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    private static final String JOB_MEDIA_IMAGES_DIR = "src/main/resources/static/images/jobmedias/";
    private static final Set<String> SUPPORTED_IMAGE_TYPES = Set.of("image/jpeg", "image/png", "image/webp");

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

    // change application status of an employee
    public void changeApplicationStatus(int jobId, int employeeId, ApplicationState status) {
        Job job = jobRepository.findById(jobId).orElseThrow(() -> new RuntimeException("Job not found"));
        Map<Integer, ApplicationState> applicants = job.getApplicants();
        if (applicants.containsKey(employeeId)) {
            applicants.put(employeeId, status);
            job.setApplicants(applicants);
            jobRepository.save(job);
        } else {
            throw new RuntimeException("Employee not found in the job applicants");
        }
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

    // change application status of an employee
    public List<Job> searchJobs(String title, JobStatus status, Address location,Boolean trending, Boolean sortBySalary , Boolean sortByMostRecent) {
        List<Job> jobs = getAllJobs();
        JobFilter jobFilter = new JobFilter(jobs);

        if (title != null && !title.isEmpty()) {
            jobFilter.filterByTitle(title);
        }

        if (status != null ) {
            jobFilter.filterByStatus(status);
        }
        if(location != null) {
            jobFilter.sortByAddress(location);
        }
        if( Boolean.TRUE.equals(sortBySalary) ){
            jobFilter.sortBySalary();
        }
        if(Boolean.TRUE.equals(sortByMostRecent) ){
            jobFilter.sortByDate();
        }
        return jobFilter.getFilteredJobs();
    }

    // storeJobMedia
    //  String mediaUrl = jobService.storeJobMedia(file); // implement this in your service
    public void storeJobMediaImages(Job job, Employer employer , MultipartFile[] files) {

        if (files != null) {
            for (MultipartFile file : files) {
                if (file.getSize() > 3 * 1024 * 1024) {
                    throw new IllegalArgumentException("Each image must be smaller than 3MB.");
                }

                if (!SUPPORTED_IMAGE_TYPES.contains(file.getContentType())) {
                    throw new IllegalArgumentException("Unsupported file type: " + file.getContentType());
                }
            }
        }
        int i = 1;
        for (MultipartFile file : files) {
            String extension = getExtensionFromMimeType(file);
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String newFilename = "image-"+ i + employer.getId() + "_" + employer.getFirstname() + "_" + employer.getLastname()
                    + "_" + timestamp + "." + extension;

            // Save the new file
            try {
                Path newPath = Paths.get(JOB_MEDIA_IMAGES_DIR + newFilename);
                Files.copy(file.getInputStream(), newPath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException("Failed to save new profile image", e);
            }

            // Add media profilePicture in DB
            job.addMedia("image"+i , newFilename);
            i++;
        }
    }
    private static final Map<String, String> MIME_TYPE_TO_EXTENSION = Map.of(
            "image/jpg", "jpg",
            "image/jpeg", "jpeg",
            "image/png", "png",
            "image/webp", "webp");

    private String getExtensionFromMimeType(MultipartFile file) {
        String mimeType = file.getContentType();
        if (mimeType == null || !MIME_TYPE_TO_EXTENSION.containsKey(mimeType)) {
            throw new IllegalArgumentException("Unsupported image type: " + mimeType);
        }
        return MIME_TYPE_TO_EXTENSION.get(mimeType);
    }
}