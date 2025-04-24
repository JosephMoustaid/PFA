package spring.bricole.util;

import spring.bricole.algorithms.RecommendationEngine;
import spring.bricole.common.JobCategory;
import spring.bricole.common.JobStatus;
import spring.bricole.model.Job;

import java.util.ArrayList;
import java.util.List;

public class JobFilter {
    private List<Job> jobs = new ArrayList<Job>();

    public JobFilter(List<Job> jobs) {
        this.jobs = jobs;
    }

    public void filterByTitle(String title) {
        jobs.removeIf(job -> !job.getTitle().toLowerCase().contains(title.toLowerCase()));
    }


    public void filterByCategory(JobCategory category) {
        jobs.removeIf(job -> !job.getCategory().equals(category));
    }

    public void sortByAddress(Address address) {
        if (address == null) {
            throw new IllegalArgumentException("Address cannot be null");
        }

        jobs = jobs.stream()
                .sorted((job1, job2) -> {
                    double distance1 = RecommendationEngine.haversineDistance(address, job1.getAddressAsObject());
                    double distance2 = RecommendationEngine.haversineDistance(address, job2.getAddressAsObject());
                    return Double.compare(distance1, distance2);
                })
                .collect(java.util.stream.Collectors.toList());
    }

    public void sortBySalary() {
        jobs.sort((job1, job2) -> Float.compare(job2.getSalary(), job1.getSalary()));
    }
    public void filterByStatus(JobStatus status) {
        jobs.removeIf(job -> !job.getStatus().equals(status));
    }
    public void sortByDate() {
        jobs.sort((job1, job2) -> job2.getCreatedAt().compareTo(job1.getCreatedAt()));
    }
    public List<Job> getFilteredJobs() {
        return jobs;
    }
}
