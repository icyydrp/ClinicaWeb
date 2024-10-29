package clinica_backend.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import clinica_backend.models.Review;
import clinica_backend.repositories.ReviewRepository;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    public Review saveReview(Review review) {
        return reviewRepository.save(review);
    }

    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }
}
