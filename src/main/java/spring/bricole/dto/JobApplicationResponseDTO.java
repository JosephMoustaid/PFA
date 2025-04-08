package spring.bricole.dto;


import spring.bricole.common.ApplicationState;

public record JobApplicationResponseDTO(
        EmployeeDTO employee,
        ApplicationState applicationState
) {
}