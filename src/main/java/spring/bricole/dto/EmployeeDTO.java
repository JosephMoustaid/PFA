package spring.bricole.dto;

import spring.bricole.common.Availability;
import spring.bricole.common.JobCategory;
import spring.bricole.common.Skill;
import spring.bricole.model.Review;

import java.util.List;
import java.util.Map;
import java.util.Set;

public record EmployeeDTO(
        String firstname,
        String lastname,
        int phoneNumberPrefix ,
        String phoneNumber,
        List<Skill> skills,
        String profilePictureUrl,
        Map<String, Availability> availabilityMap ,
        List<JobCategory> jobPreferences,
        Set<Review> reviews) {
}
