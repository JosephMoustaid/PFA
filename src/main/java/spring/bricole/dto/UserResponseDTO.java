package spring.bricole.dto;

import spring.bricole.common.AccountStatus;
import spring.bricole.common.Gender;

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

    // == Getters ==
    public String getEmail() { return email; }
    public String getFirstname() { return firstname; }
    public String getLastname() { return lastname; }
    public int getPhoneNumberPrefix() { return phoneNumberPrefix; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getAddress() { return address; }
    public Gender getGender() { return gender; }
    public String getProfilePicture() { return profilePicture; }
    public AccountStatus getStatus() { return status; }

    // == Setters (Only for mutable fields!) ==
    public void setFirstname(String firstname) { this.firstname = firstname; }
    public void setLastname(String lastname) { this.lastname = lastname; }
    public void setPhoneNumberPrefix(int prefix) { this.phoneNumberPrefix = prefix; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setAddress(String address) { this.address = address; }
    public void setGender(Gender gender) { this.gender = gender; }
    public void setProfilePicture(String profilePicture) { this.profilePicture = profilePicture; }
    public void setEmail(String email ){ this.email = email; } // Optional setter for email if needed
    // Note: No setters for id/email/status (immutable)!
}