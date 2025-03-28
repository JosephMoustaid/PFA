package spring.bricole.blueprints;

import spring.bricole.model.Review;
import spring.bricole.model.User;

import java.util.InvalidPropertiesFormatException;
import java.util.Set;

public interface IReviewable {
    /**
     * Adds a review to the entity
     * @param review The review to add
     * @return The added review
     */
    Review addReview(User user, String content, int rating) throws InvalidPropertiesFormatException;

    /**
      * Gets all reviews for this entity
     * @return List of reviews
     */
    Set<Review> getReviews();

    /**
     * Gets the average rating
     * @return average rating (0 if no reviews)
     */
    double getAverageRating();

    /**
     * Gets the number of reviews
     * @return review count
     */
    int getReviewCount();
}
