package spring.bricole.dto;

import spring.bricole.common.Availability;

public record AvailabilityDTO(
        Availability mondayAvailability,
        Availability tuesdayAvailability,
        Availability wednesdayAvailability,
        Availability thursdayAvailability,
        Availability fridayAvailability,
        Availability saturdayAvailability,
        Availability sundayAvailability
) {
}
