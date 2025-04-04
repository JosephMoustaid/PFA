package spring.bricole.dto;

import spring.bricole.common.Gender;

public class UserUpdateDTO {
    private String firstname;
    private String lastname;
    private int phoneNumberPrefix;
    private String phoneNumber;
    private String address;
    private Gender gender;
    private String profilePicture;

    // == Constructors ==
    public UserUpdateDTO() {}

    public UserUpdateDTO(String firstname, String lastname, int phoneNumberPrefix,
                         String phoneNumber, String address, Gender gender,
                         String profilePicture) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.phoneNumberPrefix = phoneNumberPrefix;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.gender = gender;
        this.profilePicture = profilePicture;
    }

    // == Getters and Setters ==
    public String getFirstname() { return firstname; }
    public void setFirstname(String firstname) { this.firstname = firstname; }

    public String getLastname() { return lastname; }
    public void setLastname(String lastname) { this.lastname = lastname; }

    public int getPhoneNumberPrefix() { return phoneNumberPrefix; }
    public void setPhoneNumberPrefix(int prefix) { this.phoneNumberPrefix = prefix; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Gender getGender() { return gender; }
    public void setGender(Gender gender) { this.gender = gender; }

    public String getProfilePicture() { return profilePicture; }
    public void setProfilePicture(String profilePicture) { this.profilePicture = profilePicture; }
}
