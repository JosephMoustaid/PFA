package spring.bricole.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import spring.bricole.common.AccountStatus;
import spring.bricole.common.Gender;
import spring.bricole.service.Bcrypt;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Inheritance(strategy = InheritanceType.JOINED) // Add inheritance strategy
@Table(name = "\"user\"")  // Escape if "user" is a reserved keyword
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private int phoneNumberPrefix;
    private String phoneNumber;
    private String address;

    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String profilePicture;


    // Notifications received by this user (inverse side)
    @OneToMany(mappedBy = "receiver")
    @JsonIgnore // ← Break the cycle
    private List<Notification> receivedNotifications = new ArrayList<>();

    public User(String firstname, String lastname, String email, String password,
                int phoneNumberPrefix, String phoneNumber, String address,
                Gender gender, String profilePicture) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.setPassword(password);  // Use setter for validation/hashing
        this.phoneNumberPrefix = phoneNumberPrefix;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.gender = gender;
        this.profilePicture = profilePicture;
    }

    public void setPassword(String password) {
        if (password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }
        this.password = Bcrypt.hashPassword(password);
    }

    public void addNotification(Notification notification) {
        if (!receivedNotifications.contains(notification)) {
            receivedNotifications.add(notification);
        }
    }
}