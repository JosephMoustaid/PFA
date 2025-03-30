package spring.bricole.dto;

import spring.bricole.common.Availability;
import spring.bricole.common.Gender;
import spring.bricole.common.Skill;
import spring.bricole.common.JobCategory;
import java.util.List;
import java.util.Map;

public record EmployeeRegisterRequest(
        String firstname,
        String lastname,
        int phoneNumberPrefix,
        String phoneNumber,
        String address,
        Gender gender,
        String profilePicture,
        String email,
        String password,
        List<Skill> skills,
        List<JobCategory> jobPreferences,
        Map<String, Availability> availability
) {
    // Default constructor with pre-filled availability
    public EmployeeRegisterRequest {
        if (availability == null) {
            availability = Map.of(
                    "Monday", Availability.FULLTIME,
                    "Tuesday", Availability.FULLTIME,
                    "Wednesday", Availability.FULLTIME,
                    "Thursday", Availability.FULLTIME,
                    "Friday", Availability.FULLTIME,
                    "Saturday", Availability.FULLTIME,
                    "Sunday", Availability.FULLTIME
            );
        }
    }
}


