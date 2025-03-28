package spring.bricole.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.bricole.model.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Integer > {
}
