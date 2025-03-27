package spring.bricole.service;

import spring.bricole.model.Job;
import spring.bricole.common.JobCategory;

import java.util.List;
import java.util.stream.Collectors;

public class Filter {
    List<Job> filterByCity(List<Job> jobs, String city) {
        return jobs.stream()
                .filter(job -> job.getLocation().contains(city))
                .collect(Collectors.toList());
    }
    List<Job>  filterByName( List<Job> jobs,  String query){
        return jobs.stream()
                .filter(job -> job.getTitle().contains(query))
                .collect(Collectors.toList());
    }
    List<Job> filterByRecent( List<Job> jobs){
        return jobs.stream()
                .sorted((j1,j2) -> j1.getCreatedAt().compareTo(j2.getCreatedAt()))
                .collect(Collectors.toList());
    }
    List<Job> filterByOldest( List<Job> jobs){
        return jobs.stream()
                .sorted((j1,j2) -> j2.getCreatedAt().compareTo(j1.getCreatedAt()))
                .collect(Collectors.toList());
    }
    List<Job>  filterByCategory( List<Job> jobs, JobCategory category){
        return jobs.stream()
                .filter(job -> job.getCategory().equals(category))
                .collect(Collectors.toList());

    }
    List<Job> filterByTrending( List<Job> jobs){
        return jobs.stream()
                .sorted((j1,j2) -> j1.getApplicants().size() - j2.getApplicants().size())
                .collect(Collectors.toList());
    }
}

