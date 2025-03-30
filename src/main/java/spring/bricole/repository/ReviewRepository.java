package spring.bricole.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.bricole.model.Review;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
    // Data retrieval methods:

    // get all reviews of an employee by id
    List<Review> findByReviewedEmployeeId(int employeeId);
}
