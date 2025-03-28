package spring.bricole.model;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import spring.bricole.blueprints.IReviewable;
import spring.bricole.common.Availability;
import spring.bricole.common.Gender;
import spring.bricole.common.JobCategory;
import spring.bricole.common.Skill;

import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "employee")
@PrimaryKeyJoinColumn(name = "user_id") // Changed from "id" to "user_id"
public class Employee extends User implements IReviewable {

    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, Availability> availabilityDaysOfWeek = new HashMap<>(
            Map.of("Monday", Availability.FULLTIME, "Tuesday", Availability.FULLTIME, "Wednesday", Availability.FULLTIME,
                    "Thursday", Availability.FULLTIME, "Friday", Availability.FULLTIME, "Saturday", Availability.FULLTIME,
                    "Sunday", Availability.FULLTIME)
    );

    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private List<Skill> skills = new ArrayList<>();

    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private List<JobCategory> jobPreferences = new ArrayList<>();


    // == Reviews ==
    @OneToMany(mappedBy = "reviewedEmployee", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Review> reviews = new HashSet<>();

    public Employee(String firstname, String lastname, String email, String password,
                    int phoneNumberPrefix, String phoneNumber, String address, Gender gender,
                    String profilePicture) {
        super(firstname, lastname, email, password, phoneNumberPrefix, phoneNumber, address, gender, profilePicture);
    }

    public void addSkill(Skill skill) {
        if (!skills.contains(skill)) {
            skills.add(skill);
        }
    }

    public void removeSkill(Skill skill) {
        skills.remove(skill);
    }

    public void addJobPreference(JobCategory jobCategory) {
        if (!jobPreferences.contains(jobCategory)) {
            jobPreferences.add(jobCategory);
        }
    }

    public void removeJobPreference(JobCategory jobCategory) {
        jobPreferences.remove(jobCategory);
    }

    public void updateAvailability(String day, Availability availability) {
        availabilityDaysOfWeek.put(day, availability);
    }

    public void applyForJob(Job job) {
        job.addApplicant(this.getId());
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
