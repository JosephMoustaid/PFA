package spring.bricole.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.bricole.model.Review;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
}
