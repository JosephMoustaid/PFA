package spring.bricole.algorithms;


import spring.bricole.common.AccountStatus;
import spring.bricole.common.ApplicationState;
import spring.bricole.common.JobCategory;
import spring.bricole.model.*;
import spring.bricole.util.Address;

import java.util.List;
import java.util.Map;

public class RecommendationEngine {

    /*
     Recommend jobs to employee
     Recommend employees to employer
     Rank from 0% to 100% how much the employee fits the job
      */

    public static Map<Job, Double> rankJobsForEmployee(Employee employee, List<Job> allJobs) {
        Map<Job, Double> rankedJobs = new java.util.HashMap<>();

        for (Job job : allJobs) {
            double rank = rankMatch(employee, job, allJobs);
            rankedJobs.put(job, rank * 100);
        }
        // sort the map by rank
        rankedJobs = rankedJobs.entrySet()
                .stream()
                .sorted(Map.Entry.<Job, Double>comparingByValue().reversed())
                .collect(java.util.stream.Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue ,
                        (e1, e2) -> e1,
                        java.util.LinkedHashMap::new
                ));

        return rankedJobs;
    }

    public static Map<Employee, Double> rankEmployeesForEmployerJob( List<Employee> allEmployees , Job jobOffer , List<Job> allJobs) {
        Map<Employee, Double> rankedEmployees = new java.util.HashMap<>();

        for (Employee employee : allEmployees) {
            double rank = rankMatch(employee, jobOffer, allJobs);
            rankedEmployees.put(employee, rank * 100);
        }
        // sort the map by rank
        rankedEmployees = rankedEmployees.entrySet()
                .stream()
                .sorted(Map.Entry.<Employee, Double>comparingByValue().reversed())
                .collect(java.util.stream.Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue ,
                        (e1, e2) -> e1,
                        java.util.LinkedHashMap::new
                ));

        return rankedEmployees;
    }

    public static double rankMatch(Employee employee, Job job, List<Job> allJobs) {
        double matchingRank = 0.0;

        double avgRating = employee.getAverageRating();
        AccountStatus accountStatus = employee.getStatus();

        boolean jobCategoryInJobPreferences = false;
        for (JobCategory jobCategory : employee.getJobPreferences()) {
            if (jobCategory.equals(job.getCategory())) {
                jobCategoryInJobPreferences = true;
                break;
            }
        }

        double distance = haversineDistance(employee.getAddressAsObject(), job.getAddressAsObject());

        int numberOfJobTakenFromEmployer = 0;
        for (Job j : job.getEmployer().getJobOffers()) {
            for (Map.Entry<Integer, ApplicationState> entry : j.getApplicants().entrySet()) {
                if (entry.getKey() == employee.getId() && entry.getValue() == ApplicationState.ACCEPTED)
                    numberOfJobTakenFromEmployer++;
            }
        }

        int numberOfPreviousJobsTaken = 0;
        for (Job j : allJobs) {
            for (Map.Entry<Integer, ApplicationState> entry : j.getApplicants().entrySet()) {
                if (entry.getKey() == employee.getId() && entry.getValue() == ApplicationState.ACCEPTED)
                    numberOfPreviousJobsTaken++;
            }
        }

        double previousRatingFromEmployer = 3.0; // Neutral default
        for (Review review : employee.getReviews()) {
            if (review.getReviewerId() == job.getEmployer().getId()) {
                previousRatingFromEmployer = review.getRating();
                break;
            }
        }

        // Normalized scores (between 0 and 1)
        double avgRatingScore = avgRating / 5.0;
        double accountStatusScore = (accountStatus == AccountStatus.ACTIVE) ? 1.0 : 0.0;
        double categoryMatchScore = jobCategoryInJobPreferences ? 1.0 : 0.0;
        double salaryScore = Math.min(job.getSalary() / 1000.0, 1.0);
        double distanceScore = 1 - Math.min(distance / 100.0, 1.0);
        double employerLoyaltyScore = Math.min(numberOfJobTakenFromEmployer / 5.0, 1.0);
        double experienceScore = Math.min(numberOfPreviousJobsTaken / 20.0, 1.0);
        double prevRatingScore = Math.min(previousRatingFromEmployer / 5.0, 1.0);

        // Final weighted rank (weights normalized to sum = 1.0)
        matchingRank =
                0.20 * avgRatingScore +
                        0.20 * distanceScore +
                        0.10 * prevRatingScore +
                        0.10 * accountStatusScore +
                        0.10 * experienceScore +
                        0.10 * employerLoyaltyScore +
                        0.10 * salaryScore +
                        0.10 * categoryMatchScore;

        return matchingRank;
    }

    public static double haversineDistance(Address address1, Address address2) {
        double R = 6371; // Earth's radius in kilometers

        double lat1 = Math.toRadians(address1.getLatitude());
        double lon1 = Math.toRadians(address1.getLongitude());
        double lat2 = Math.toRadians(address2.getLatitude());
        double lon2 = Math.toRadians(address2.getLongitude());

        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;

        double a = Math.pow(Math.sin(dLat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin(dLon / 2), 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c; // in kilometers
    }
}