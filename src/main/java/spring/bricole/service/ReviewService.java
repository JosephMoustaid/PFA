package spring.bricole.service;

import org.springframework.stereotype.Service;
import spring.bricole.common.EventType;
import spring.bricole.common.Role;
import spring.bricole.model.Review;
import spring.bricole.repository.ReviewRepository;

import java.util.Map;

@Service
public class ReviewService {


    private  ReviewRepository reviewRepository;

    private final EventLoggingService eventLoggingService;
    public ReviewService(ReviewRepository reviewRepository,
                         EventLoggingService eventLoggingService) {
        this.reviewRepository = reviewRepository;
        this.eventLoggingService = eventLoggingService;
    }

    // get total number of reviews
    public long getTotalReviews() {
        return reviewRepository.count();
    }
    public Review saveReview(Review review) {
        eventLoggingService.log(
                review.getReviewerId(),
                Role.USER,
                EventType.REVIEW_SUBMISSION,
                Map.of(
                        "reviewerId", review.getReviewerId(),
                        "revieweeId", review.getReviewedEmployee().getId(),
                        "reviewer", review.getReviewerName(),
                        "reviewee", review.getReviewedEmployee().getFirstname() + " " + review.getReviewedEmployee().getLastname(),
                        "review", review.getContent(),
                        "rating", review.getRating()
                )
        );

        return reviewRepository.save(review);
    }
}
