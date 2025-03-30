package spring.bricole.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import spring.bricole.model.Employee;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    Optional<Employee> findByEmail(String email);
    Optional<Employee> findById(int id);
    List<Employee> findByFirstname(String firstname);
    List<Employee> findByLastname(String lastname);

    @Query(value = "SELECT * FROM employee e WHERE e.skills @> :skill::jsonb", nativeQuery = true)
    List<Employee> findBySkill(@Param("skill") String skill);

    List<Employee> findByAddress(String address);

    @Query("SELECT e FROM Employee e WHERE e.address LIKE %:address%")
    List<Employee> findByAddressContaining(@Param("address") String address);
}