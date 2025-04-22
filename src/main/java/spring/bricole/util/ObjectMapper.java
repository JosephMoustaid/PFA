package spring.bricole.util;

import spring.bricole.common.Availability;
import spring.bricole.common.JobCategory;
import spring.bricole.common.Skill;
import spring.bricole.dto.EmployeeResponseDTO;
import spring.bricole.model.Employee;
import spring.bricole.model.Review;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ObjectMapper {
    /*
    Map Job to JobDTO
    Map Employee to EmployeeDTO
     */

    // Map Employee to EmployeeResponseDTO
    public static EmployeeResponseDTO mapEmployeeToEmployeeResponseDTO(Employee employee) {
        return new EmployeeResponseDTO(
                employee.getId(),
                employee.getFirstname(),
                employee.getLastname(),
                employee.getPhoneNumberPrefix(),
                employee.getPhoneNumber(),
                employee.getSkills(),
                employee.getProfilePicture(), // Ensure this method exists in Employee
                employee.getAvailabilityDaysOfWeek(),   // Ensure this method exists in Employee
                employee.getJobPreferences(),
                employee.getReviews()
        );
    }
}