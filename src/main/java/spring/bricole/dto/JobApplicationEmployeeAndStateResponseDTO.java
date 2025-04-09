package spring.bricole.dto;

import spring.bricole.common.ApplicationState;

public record JobApplicationEmployeeAndStateResponseDTO(
        EmployeeResponseDTO employee ,
        ApplicationState applicationState
) {
}
