package spring.bricole.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.bricole.model.Employee;
import spring.bricole.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    // Data retrieval methods:

    // get user by id
    Optional<User> findById(int id);

    // get user by email
    Optional<User> findByEmail(String email);

    // get users by first name and last name
    List<User> findByFirstnameAndLastnameOrderByIdDesc(String firstname, String lastname);
}
