package spring.bricole.dto;

import spring.bricole.common.ApplicationState;

public record ApplicantDTO(
        double matchingRank,
        EmployeeResponseDTO employeeDTO,
        ApplicationState applicationState
) {
}
