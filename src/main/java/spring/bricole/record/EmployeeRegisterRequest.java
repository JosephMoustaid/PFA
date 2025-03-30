package spring.bricole.record;

import spring.bricole.common.Gender;

public record EmployeeRegisterRequest(
        String firstname,
        String lastname,
        String phoneNumberPrefix,
        String phoneNumber,
        String address,
        Gender gender,
        String profilePicture,
        String email,
        String password
) {
}
