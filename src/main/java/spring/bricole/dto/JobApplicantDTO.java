package spring.bricole.dto;

import spring.bricole.common.ApplicationState;
import spring.bricole.model.Employee;
import spring.bricole.model.User;

public class JobApplicantDTO {

    private Employee employee;
    private ApplicationState applicationState;

    public JobApplicantDTO(Employee employee, ApplicationState applicationState) {
        this.employee = employee;
        this.applicationState = applicationState;
    }
    // Getters and setters
    public User getUser() {
        return employee;
    }
    public void setUser(Employee employee) {
        this.employee = employee;
    }
    public ApplicationState getApplicationState() {
        return applicationState;
    }
    public void setApplicationState(ApplicationState applicationState) {
        this.applicationState = applicationState;
    }
}
