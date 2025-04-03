package spring.bricole.dto;


import spring.bricole.common.ApplicationState;
import spring.bricole.model.Job;

public class JobApplicationDTO {
    private Job job;
    private ApplicationState applicationState;

    public JobApplicationDTO(Job job, ApplicationState applicationState) {
        this.job = job;
        this.applicationState = applicationState;
    }

    // Getters and setters
    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public ApplicationState getApplicationState() {
        return applicationState;
    }

    public void setApplicationState(ApplicationState applicationState) {
        this.applicationState = applicationState;
    }
}