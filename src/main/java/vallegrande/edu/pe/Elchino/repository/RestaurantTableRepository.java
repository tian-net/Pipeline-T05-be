package vallegrande.edu.pe.Elchino.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import vallegrande.edu.pe.Elchino.model.RestaurantTable;

@Repository
public interface RestaurantTableRepository extends ReactiveMongoRepository<RestaurantTable, String> {
    Flux<RestaurantTable> findByIsDeleted(Boolean isDeleted);
    Flux<RestaurantTable> findByIsDeletedFalseAndIsReservableTrue();
    Flux<RestaurantTable> findByIsDeletedFalseAndIsReservableTrueAndStatus(String status);
}
