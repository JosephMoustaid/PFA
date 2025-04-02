package spring.bricole.repository;

import java.util.List;

public interface CustomJobRepository {
    List<Object[]> findApplicationsByJobId(int jobId);
    List<Object[]> findApplicationsByEmployeeId(int employeeId);
}
