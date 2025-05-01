package spring.bricole.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
import spring.bricole.util.Address;

import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "job")
public class Job {
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
    @JsonBackReference // Prevents infinite recursion
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

    @Transient
    public Integer getEmployerId() {
        return employer != null ? employer.getId() : null;
    }


    // == Reviews ==
    /*
    @OneToMany(mappedBy = "job")
    private Set<Review> reviews = new HashSet<>();
    */

    // == Constructor ==
    public Job(String title, String description, JobCategory category, JobStatus status, String location, float salary, LocalDateTime createdAt) {
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

    public Map<Integer, ApplicationState> getApplicants() {
        return applicants;
    }


    public Address getAddressAsObject() {
        double latitude = 0.0;
        double longitude = 0.0;
        if( location != null && !location.isEmpty() ) {
            String[] parts = location.split(",");
            if (parts.length == 2) {
                try {
                    latitude = Double.parseDouble(parts[0].trim());
                    longitude = Double.parseDouble(parts[1].trim());
                } catch (NumberFormatException e) {
                    // Handle parsing error
                    System.err.println("Error parsing address: " + e.getMessage());
                }
            }
            return new Address(latitude, longitude);
        }
        return new Address( );
    }

    public void setAddressAsObject( Address address) {
        this.location = address.getLatitude() + "," + address.getLongitude();
    }

    public String getCombinedText(){
        return "Job " +
                "\n Title : "+ title +
                "\n Description : "+ description +
                "\n Category : " + category +
                "\n Status : " + status +
                "\n missions : " + String.join(", " , this.missions);

    }
}
