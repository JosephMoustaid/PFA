package spring.bricole.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import spring.bricole.service.Bcrypt;

@Getter @Setter
@NoArgsConstructor
@Entity
@Table(name = "admin")
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String email;
    private String password;

    public Admin(String email, String password) {
        this.email = email;
        this.password = Bcrypt.hashPassword(password);
    }
    public void setPassword(String password) {
        if(password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }
        this.password = Bcrypt.hashPassword(password);
    }
}
