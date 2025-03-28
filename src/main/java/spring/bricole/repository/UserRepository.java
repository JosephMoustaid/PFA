package spring.bricole.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.bricole.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {
}
