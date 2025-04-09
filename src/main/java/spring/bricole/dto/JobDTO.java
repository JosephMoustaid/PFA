package spring.bricole.dto;

import spring.bricole.common.ApplicationState;
import spring.bricole.common.JobCategory;
import spring.bricole.common.JobStatus;
import spring.bricole.model.Job;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class JobDTO {
    private int id;
    private String title;
    private String description;
    private JobCategory category;
    private JobStatus status;
    private String location;
    private float salary;
    private Map<String, String> media;
    private List<String> missions;
    private LocalDateTime createdAt;


    public JobDTO(Job job) {
        this.id = job.getId();
        setTitle(job.getTitle());
        setDescription(job.getDescription());
        setLocation(job.getLocation());
        setCategory(job.getCategory());
        setMissions(job.getMissions());
        setMedia(job.getMedia());
        setCreatedAt(job.getCreatedAt());
        setStatus(job.getStatus());
        setSalary(job.getSalary());
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public JobCategory getCategory() {
        return category;
    }

    public void setCategory(JobCategory category) {
        this.category = category;
    }

    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public float getSalary() {
        return salary;
    }

    public void setSalary(float salary) {
        this.salary = salary;
    }

    public Map<String, String> getMedia() {
        return media;
    }

    public void setMedia(Map<String, String> media) {
        this.media = media;
    }

    public List<String> getMissions() {
        return missions;
    }

    public void setMissions(List<String> missions) {
        this.missions = missions;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JobDTO jobDTO = (JobDTO) o;
        return id == jobDTO.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}