package vallegrande.edu.pe.Elchino.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import vallegrande.edu.pe.Elchino.model.Reservation;
import reactor.core.publisher.Flux;

@Repository
public interface ReservationRepository extends ReactiveMongoRepository<Reservation, String> {
    Flux<Reservation> findByIsDeleted(Boolean isDeleted);
}