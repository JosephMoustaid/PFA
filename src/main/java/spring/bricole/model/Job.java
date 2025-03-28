package spring.bricole.model;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import spring.bricole.blueprints.IReviewable;
import spring.bricole.common.ApplicationState;
import spring.bricole.common.JobCategory;
import spring.bricole.common.JobStatus;

import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "job")
public class Job implements IReviewable {
    // == fields ==
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String title;
    private String description;

    @Enumerated(EnumType.STRING)
    private JobCategory category;

    @Enumerated(EnumType.STRING)
    private JobStatus status;

    private String location;
    private float salary;


    // In Job.java
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employer_id")
    private Employer employer;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, String> media = new HashMap<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<String> missions = new ArrayList<>();

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @ElementCollection
    @CollectionTable(name = "job_applicants", joinColumns = @JoinColumn(name = "job_id"))
    @MapKeyColumn(name = "employee_id") // Store employee ID instead of Employee entity
    @Column(name = "application_state")
    @Enumerated(EnumType.STRING)
    private Map<Integer, ApplicationState> applicants = new HashMap<>();


    // == Reviews ==
    @OneToMany(mappedBy = "job")
    private Set<Review> reviews = new HashSet<>();


    // == Constructor ==
    public Job(String title, String description, JobCategory category, JobStatus status, String location, float salary, Employer employer, LocalDateTime createdAt) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.status = status;
        this.location = location;
        this.salary = salary;
        this.createdAt = createdAt;
    }

    // == Methods ==
    public void addApplicant(int employeeId) {
        applicants.put(employeeId, ApplicationState.PENDING);
    }

    public void acceptCandidate(int employeeId) {
        applicants.put(employeeId, ApplicationState.ACCEPTED);
    }

    public void rejectCandidate(int employeeId) {
        applicants.put(employeeId, ApplicationState.REJECTED);
    }

    public void addMedia(String key, String value) {
        media.put(key, value);
    }

    public void addMission(String mission) {
        missions.add(mission);
    }

    public void removeMission(String mission) {
        missions.remove(mission);
    }

    @Override
    public Review addReview(User user, String content, int rating) throws InvalidPropertiesFormatException {
        Review review = new Review();
        review.setReviewerName(user.getFirstname() + " " + user.getLastname());
        review.setContent(content);
        if (rating > 5 || rating < 1)
            throw new InvalidPropertiesFormatException("The rating should be between 1 and 5");
        review.setRating(rating);
        return null;
    }

    @Override
    public Set<Review> getReviews() {
        return this.reviews;
    }

    @Override
    public double getAverageRating() {
        double avg = 0.0;
        if(reviews.isEmpty() )
            return avg;
        for(Review rev : reviews){
            avg += rev.getRating();
        }
        return avg / reviews.size() ;
    }

    @Override
    public int getReviewCount() {
        if(reviews.isEmpty())
            return 0;
        return reviews.size();
    }
}
