package spring.bricole.dto;

import spring.bricole.common.Gender;

public record EmployerRegisterRequest(
        String firstname,
        String lastname,
        int phoneNumberPrefix,
        String phoneNumber,
        String address,
        Gender gender,
        String email,
        String password
) {
}

/*
    // In Employer.java
    @OneToMany(mappedBy = "employer", fetch = FetchType.LAZY)
    private List<Job> jobOffers = new ArrayList<>();

    // Add helper method for bidirectional sync
    public void postJob(Job job) {
        jobOffers.add(job);
        job.setEmployer(this);
    }


*/
