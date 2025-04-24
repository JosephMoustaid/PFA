package spring.bricole.dto;

import spring.bricole.common.JobCategory;

import java.util.List;

public record JobPrefrencesDTO(
        List<JobCategory> jobPreferences
) {
}
