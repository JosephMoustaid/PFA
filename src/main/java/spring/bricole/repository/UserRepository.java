package spring.bricole.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import spring.bricole.common.AccountStatus;
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

    // update User accout status
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.status = :status WHERE u.id = :id")
    int updateStatusById(@Param("id") int id, @Param("status") AccountStatus status);
    // get users by first name and last name
    List<User> findByFirstnameAndLastnameOrderByIdDesc(String firstname, String lastname);

    // get user male/female demographics
    @Query("SELECT COUNT(u) FROM User u WHERE u.gender = 'MALE'")
    int getMaleCount();

    @Query("SELECT COUNT(u) FROM User u WHERE u.gender = 'FEMALE'")
    int getFemaleCount();
}
