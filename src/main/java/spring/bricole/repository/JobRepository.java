package spring.bricole.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.bricole.model.Job;

public interface JobRepository extends JpaRepository<Job, Integer> {
}
