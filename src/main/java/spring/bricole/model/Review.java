package spring.bricole.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import spring.bricole.blueprints.IReviewable;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter @Setter
@Entity
@Table(name = "review")
public class Review {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "reviewer_name", nullable = false, length = 100)
    private String reviewerName;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    private int rating; // Typically 1-5 stars

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_employee_id", nullable = false)
    private Employee reviewedEmployee;

    @Column(name = "reviewer_id", nullable = false)
    private int reviewerId;



    // Constructors
    public Review() {
        this.createdAt = LocalDateTime.now();
    }

    public Review(String reviewerName, String content, int rating) {
        this();
        this.reviewerName = reviewerName;
        this.content = content;
        this.rating = rating;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Review review = (Review) o;
        return Objects.equals(id, review.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Review{" +
                "id=" + id +
                ", reviewerName='" + reviewerName + '\'' +
                ", rating=" + rating +
                ", createdAt=" + createdAt +
                '}';
    }
}