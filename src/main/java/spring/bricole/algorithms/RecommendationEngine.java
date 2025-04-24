package spring.bricole.algorithms;


import com.fasterxml.jackson.databind.JsonNode;
import spring.bricole.common.AccountStatus;
import spring.bricole.common.ApplicationState;
import spring.bricole.common.JobCategory;
import spring.bricole.dto.EmployeeResponseDTO;
import spring.bricole.dto.JobDTO;
import spring.bricole.model.*;
import spring.bricole.util.Address;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import spring.bricole.util.ObjectMapper;

public class RecommendationEngine {

    /*
     Recommend jobs to employee
     Recommend employees to employer
     Rank from 0% to 100% how much the employee fits the job
      */

    public static Map<JobDTO, Double> rankJobsForEmployee(Employee employee, List<Job> allJobs) throws IOException {
        Map<JobDTO, Double> rankedJobs = new java.util.HashMap<>();

        for (Job job : allJobs) {
            double rank = rankMatch(employee, job, allJobs);
            // round the rank to 2 decimal places
            rankedJobs.put(new JobDTO(job), rank * 100 );
        }
        // sort the map by rank
        rankedJobs = rankedJobs.entrySet()
                .stream()
                .sorted(Map.Entry.<JobDTO, Double>comparingByValue(Double::compare).reversed())
                .collect(java.util.stream.Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        java.util.LinkedHashMap::new
                ));

        return rankedJobs;
    }

    public static Map<EmployeeResponseDTO, Double> rankEmployeesForEmployerJob(List<Employee> allEmployees, Job jobOffer, List<Job> allJobs) throws IOException {
        Map<EmployeeResponseDTO, Double> rankedEmployees = new java.util.HashMap<>();

        for (Employee employee : allEmployees) {
            double rank = rankMatch(employee, jobOffer, allJobs);
            rankedEmployees.put(ObjectMapper.mapEmployeeToEmployeeResponseDTO(employee), rank * 100);
        }

        // Sort the map by rank
        rankedEmployees = rankedEmployees.entrySet()
                .stream()
                .sorted(Map.Entry.<EmployeeResponseDTO, Double>comparingByValue().reversed())
                .collect(java.util.stream.Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        java.util.LinkedHashMap::new
                ));

        return rankedEmployees;
    }

    public static double rankMatch(Employee employee, Job job, List<Job> allJobs) throws IOException {
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

        // Calculate the cosine similarity between the job description and employee's combined text
        String jobText = job.getCombinedText();
        String employeeText = employee.getCombinedText();

        double[] jobVec = getEmbedding(jobText);
        double[] employeeVec = getEmbedding(employeeText);

        double contentSimilarityScore = cosineSimilarity(jobVec, employeeVec);

        // Normalized scores (between 0 and 1)
        double avgRatingScore = avgRating / 5.0;
        double accountStatusScore = (accountStatus == AccountStatus.ACTIVE) ? 1.0 : 0.0;
        double categoryMatchScore = jobCategoryInJobPreferences ? 1.0 : 0.0;
        double salaryScore = Math.min(job.getSalary() / 1000.0, 1.0);
        double distanceScore = 1 - Math.min(distance / 100.0, 1.0);
        double employerLoyaltyScore = Math.min(numberOfJobTakenFromEmployer / 5.0, 1.0);
        double experienceScore = Math.min(numberOfPreviousJobsTaken / 20.0, 1.0);
        double prevRatingScore = Math.min(previousRatingFromEmployer / 5.0, 1.0);

        // some code however
        // Final weighted rank (weights normalized to sum = 1.0)
        /*
        The model name is : All-MiniLM-L6-v2
        This is a sentence-transformers model: It maps sentences & paragraphs to a 384 dimensional dense
        ector space and can be used for tasks like clustering or semantic search.
        We then use cosine to compare the similarity between the job description and employee's combined text.

         */
        matchingRank =
                0.20 * contentSimilarityScore + // we calculate the cosine similarity between the job info and employee's combined text using
                0.15 * avgRatingScore +
                0.15 * distanceScore +
                0.10 * prevRatingScore +
                0.10 * accountStatusScore +
                0.10 * experienceScore +
                0.05 * employerLoyaltyScore +
                0.05 * salaryScore +
                0.10 * categoryMatchScore;

        return  matchingRank;
    }

    // to calculate the distance between two addresses in km
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

    public static double[] getEmbedding(String text) throws IOException {
        try {
            URL url = new URL("http://127.0.0.1:5000/embed");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);
            con.setConnectTimeout(5000);  // 5 seconds connection timeout
            con.setReadTimeout(10000);    // 10 seconds read timeout

            // Properly escape JSON string
            String escapedText = text.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");

            String jsonInputString = "{\"text\":\"" + escapedText + "\"}";
            System.out.println("Sending JSON to Flask: " + jsonInputString);

            // Write the JSON payload
            try (OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Check response code
            int responseCode = con.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                // Read error response if available
                String errorResponse = "";
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(con.getErrorStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        response.append(line);
                    }
                    errorResponse = response.toString();
                }
                throw new IOException("HTTP error " + responseCode + ": " + errorResponse);
            }

            // Read successful response
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }

                // Manual JSON parsing (since we're not using a library)
                String json = response.toString();
                int start = json.indexOf("[") + 1;
                int end = json.lastIndexOf("]");
                if (start < 0 || end < 0) {
                    throw new IOException("Invalid embedding response format");
                }

                String[] parts = json.substring(start, end).split(",");
                double[] embedding = new double[parts.length];
                for (int i = 0; i < parts.length; i++) {
                    embedding[i] = Double.parseDouble(parts[i].trim());
                }

                return embedding;
            }
        } catch (IOException e) {
            throw new IOException("Error while getting embedding: " + e.getMessage(), e);
        }
    }

    // Calculate cosine similarity between two vectors
    public static double cosineSimilarity(double[] vec1, double[] vec2) {
        double dot = 0.0, norm1 = 0.0, norm2 = 0.0;
        for (int i = 0; i < vec1.length; i++) {
            dot += vec1[i] * vec2[i];
            norm1 += Math.pow(vec1[i], 2);
            norm2 += Math.pow(vec2[i], 2);
        }
        return dot / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }

}