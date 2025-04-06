package spring.bricole.dto;

import jakarta.persistence.SequenceGenerators;
import lombok.Getter;
import lombok.Setter;
import spring.bricole.common.AccountStatus;
import spring.bricole.common.Gender;

@Getter @Setter
public class UserResponseDTO {
    private String email;             // Read-only
    private String firstname;
    private String lastname;
    private int phoneNumberPrefix;
    private String phoneNumber;
    private String address;
    private Gender gender;
    private String profilePicture;
    private AccountStatus status;     // Read-only (e.g., "ACTIVE")

    // == Constructors ==
    public UserResponseDTO() {}

    public UserResponseDTO(String email, String firstname, String lastname,
                           int phoneNumberPrefix, String phoneNumber, String address,
                           Gender gender, String profilePicture, AccountStatus status) {
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
        this.phoneNumberPrefix = phoneNumberPrefix;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.gender = gender;
        this.profilePicture = profilePicture;
        this.status = status;
    }
    // Note: No setters for id/email/status (immutable)!
}