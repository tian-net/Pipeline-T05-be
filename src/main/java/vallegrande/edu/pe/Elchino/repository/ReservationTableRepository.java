package vallegrande.edu.pe.Elchino.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import vallegrande.edu.pe.Elchino.model.ReservationTable;

@Repository
public interface ReservationTableRepository extends ReactiveMongoRepository<ReservationTable, String> {
    Flux<ReservationTable> findByIsDeleted(Boolean isDeleted);
    Flux<ReservationTable> findByReservationIdAndIsDeleted(String reservationId, Boolean isDeleted);
    Flux<ReservationTable> findByTableIdAndIsDeleted(String tableId, Boolean isDeleted);
}
