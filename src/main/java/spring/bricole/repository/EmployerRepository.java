package spring.bricole.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.bricole.model.Employer;

import java.util.List;
import java.util.Optional;

public interface EmployerRepository extends JpaRepository<Employer, Integer> {

    // Data retrieval methods:

    // find employer by email
    Optional<Employer> findByEmail(String email);

    // find employer by id
    Optional<Employer> findById(int id);

    // find employers by firstname
    List<Employer> findByFirstname(String firstname);

    // find employers by lastname
    List<Employer> findByLastname(String firstname);

    // find employers by firstname and lastname
    List<Employer> findByFirstnameAndLastname(String firstname, String lastname);

    // find employers Like firstname and lastname
    List<Employer> findByFirstnameAndLastnameContaining(String firstname, String lastname);

}
