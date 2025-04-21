package spring.bricole.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import spring.bricole.common.Gender;
import spring.bricole.common.JobStatus;

import java.util.ArrayList;
import java.util.List;


@Getter @Setter
@NoArgsConstructor
@Entity
@Table(name = "employer")
@PrimaryKeyJoinColumn(name = "user_id") // Changed from "id" to "user_id"
public class Employer extends User{


    // In Employer.java
    @OneToMany(mappedBy = "employer", fetch = FetchType.LAZY)
    @JsonManagedReference // Allows serialization
    private List<Job> jobOffers = new ArrayList<>();

    // Add helper method for bidirectional sync
    public void postJob(Job job) {
        jobOffers.add(job);
        job.setEmployer(this);
    }
    // == Constructor ==
    public Employer( String firstname,String lastname, String email, String password,
                    int phoneNumberPrefix, String phoneNumber, String address, Gender gender,
                    String profilePicture){
        super(firstname, lastname,email, password,phoneNumberPrefix, phoneNumber,address, gender, profilePicture);
    }

    void accpetCandidate(Job job, Employee employee){
        job.acceptCandidate(employee.getId());
    }
    void rejectCandidate(Job job, Employee employee){
        job.rejectCandidate(employee.getId());
    }
    void changeJobOfferStatus(Job job, JobStatus status){
        for(Job j: jobOffers){
            if(j.equals(job)){
                j.setStatus(status);
            }
        }
    }
}
