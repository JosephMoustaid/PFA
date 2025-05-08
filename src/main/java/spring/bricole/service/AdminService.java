package spring.bricole.service;

import org.springframework.stereotype.Service;
import spring.bricole.model.*;

import spring.bricole.repository.*;

import java.util.List;
import spring.bricole.service.EventLoggingService;
@Service
public class AdminService {

    private final AdminRepository adminRepository ;
    private final EmployeeRepository employeeRepository;
    private final EmployerRepository employerRepository;
    private final JobRepository jobRepository;
    private final ConversationRepository conversationRepository;
    public final ReviewRepository reviewRepository;
    public final UserRepository userRepository;
    public final NotificationRepository notificationRepository;
    private final UserEventRepository userEventRepository;

    public AdminService(AdminRepository adminRepository,
                        EmployeeRepository employeeRepository,
                        EmployerRepository employerRepository,
                        JobRepository jobRepository,
                        ConversationRepository conversationRepository,
                        ReviewRepository reviewRepository,
                        UserRepository userRepository,
                        NotificationRepository notificationRepository,
                        UserEventRepository userEventRepository)  {

        this.adminRepository = adminRepository;
        this.employeeRepository = employeeRepository;
        this.employerRepository = employerRepository;
        this.jobRepository = jobRepository;
        this.conversationRepository = conversationRepository;
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
        this.userEventRepository = userEventRepository;
    }

    public List<Employee> getAllEmployees(){
        List<Employee> employees = employeeRepository.findAll();
        return employees;
    }
    public List<Employer> getAllEmployers(){
        List<Employer> employers = employerRepository.findAll();
        return employers;
    }
    public List<Job> getAllJobs(){
        List<Job> jobs = jobRepository.findAll();
        return jobs;
    }
    public List<UserEvent> getLast100UserEvents(){
        List<UserEvent> userEvents = userEventRepository.findTop100ByOrderByTimestampDesc();
        return userEvents;
    }
    //      User Growth Timeline (Line Chart)
    //      User demographics (Pie Chart)

    public int getMaleCount(){
       return userRepository.getMaleCount();
    }
    public int getFemaleCount(){
        return userRepository.getMaleCount();
    }
}

