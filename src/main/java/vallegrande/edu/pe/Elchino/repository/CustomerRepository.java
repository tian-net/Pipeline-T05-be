package vallegrande.edu.pe.Elchino.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import vallegrande.edu.pe.Elchino.model.Customer;
import reactor.core.publisher.Flux;

@Repository
public interface CustomerRepository extends ReactiveMongoRepository<Customer, String> {
    
    // Método para listar por estado (activo o eliminado) - Devuelve un FLUX
    Flux<Customer> findByIsDeleted(Boolean isDeleted);
}