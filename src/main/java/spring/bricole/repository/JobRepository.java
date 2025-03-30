package spring.bricole.repository;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import spring.bricole.model.Employee;
import spring.bricole.model.Job;

import java.util.List;
import java.util.Optional;

public interface JobRepository extends JpaRepository<Job, Integer> {
    // Data retrieval methods:

    // find job by id
    Optional<Job> findById(int id);

    // Partial match with LIKE (equivalent to '%location%')

    @Query("SELECT j FROM Job j WHERE j.location LIKE %:location%")
    List<Job> findByLocationContainingOrderByCreatedAtDesc(@Param("location") String location);

    // find jobs by category
    List<Job> findByCategoryOrderByCategoryDesc(String category);

    // find jobs by title
    List<Job> findByTitleOrderByCreatedAtDesc(String title);

    // find jobs by description
    List<Job> findByDescriptionContaining(String description);

    // find jobs by location
    List<Job> findByLocationLikeOrderByCreatedAtDesc(String location);
    /*
    @Query(value = """
    SELECT j FROM Job j 
    WHERE EXISTS (
        SELECT 1 FROM jsonb_array_elements_text(j.missions) AS m 
        WHERE LOWER(m) LIKE LOWER(CONCAT('%', :mission, '%'))
    )
    ORDER BY j.createdAt DESC
    """)
    List<Job> findByMissionsContainingIgnoreCaseOrderByCreatedAtDesc(@Param("mission") String mission);
    */

    // find most recent jobs
    List<Job> findAllByOrderByCreatedAtDesc();

    // find trending jobs
    // Lets leave this to the service layer

    // find jobs by status
    List<Job> findAllByStatus(String status);

    // find highest paying jobs
    List<Job> findTop50ByOrderBySalaryDesc();

}
