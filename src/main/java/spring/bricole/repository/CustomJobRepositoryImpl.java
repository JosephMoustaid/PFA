package spring.bricole.repository;


import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomJobRepositoryImpl implements CustomJobRepository {
    private final EntityManager entityManager;

    @Override
    public List<Object[]> findApplicationsByJobId(int jobId) {
        String sql = """
            SELECT e.*, ja.application_state 
            FROM job_applicants ja
            JOIN employee e ON ja.employee_id = e.id
            WHERE ja.job_id = :jobId
            """;
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("jobId", jobId);
        return query.getResultList();
    }

    @Override
    public List<Object[]> findApplicationsByEmployeeId(int employeeId) {
        String sql = """
            SELECT j.*, ja.application_state 
            FROM job_applicants ja
            JOIN job j ON ja.job_id = j.id
            WHERE ja.employee_id = :employeeId
            """;
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("employeeId", employeeId);
        return query.getResultList();
    }
}