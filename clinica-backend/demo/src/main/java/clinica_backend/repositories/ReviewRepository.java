package clinica_backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import clinica_backend.models.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
}
