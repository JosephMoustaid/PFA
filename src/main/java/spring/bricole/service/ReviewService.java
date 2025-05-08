package spring.bricole.service;

import org.springframework.stereotype.Service;
import spring.bricole.model.Review;
import spring.bricole.repository.ReviewRepository;

@Service
public class ReviewService {

    private  ReviewRepository reviewRepository;
    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    // get total number of reviews
    public long getTotalReviews() {
        return reviewRepository.count();
    }
    public Review saveReview(Review review) {
        return reviewRepository.save(review);
    }
}
