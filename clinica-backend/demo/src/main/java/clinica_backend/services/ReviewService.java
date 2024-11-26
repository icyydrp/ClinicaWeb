package clinica_backend.services;

import java.util.List;
import java.util.Optional;

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

    public void deleteReview(Long id) {
        if (reviewRepository.existsById(id)) {
            reviewRepository.deleteById(id);
        } else {
            throw new RuntimeException("Reseña con ID " + id + " no encontrada.");
        }
    }

    public Review updateReview(Long id, Review updatedReview) {
        Optional<Review> existingReview = reviewRepository.findById(id);
        if (existingReview.isPresent()) {
            Review review = existingReview.get();
            review.setNombrePaciente(updatedReview.getNombrePaciente());
            review.setCalificacion(updatedReview.getCalificacion());
            review.setComentario(updatedReview.getComentario());
            return reviewRepository.save(review);
        } else {
            throw new RuntimeException("Reseña con ID " + id + " no encontrada.");
        }
    }
}
