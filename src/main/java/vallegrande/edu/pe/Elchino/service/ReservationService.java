package vallegrande.edu.pe.Elchino.service;

import org.springframework.stereotype.Service;
import vallegrande.edu.pe.Elchino.model.Reservation;
import vallegrande.edu.pe.Elchino.repository.ReservationRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.LocalDateTime;

@Service
public class ReservationService {

    private final ReservationRepository repository;

    public ReservationService(ReservationRepository repository) {
        this.repository = repository;
    }

    public Flux<Reservation> findAllActive() {
        return repository.findByIsDeleted(false);
    }

    public Mono<Reservation> findById(String id) {
        return repository.findById(id);
    }

    //  Método Crear para Reservas
    public Mono<Reservation> create(Reservation reservation) {
        reservation.setId(null);
        reservation.setCreatedAt(LocalDateTime.now());
        
        reservation.setIsDeleted(false);
        if (reservation.getStatus() == null) reservation.setStatus("Pendiente");
        
        return repository.save(reservation);
    }

    // Método Update para Reservas
    public Mono<Reservation> update(String id, Reservation details) {
        return repository.findById(id)
                .flatMap(existing -> {
                    details.setId(existing.getId());
                    details.setCreatedAt(existing.getCreatedAt());
                    details.setUpdatedAt(LocalDateTime.now()); 
                    details.setIsDeleted(existing.getIsDeleted());
                    return repository.save(details);
                });
    }

    // Eliminar lógico
    public Mono<Reservation> deleteLogical(String id) {
        return repository.findById(id)
                .flatMap(res -> {
                    res.setIsDeleted(true);
                    res.setDeletedAt(LocalDateTime.now());
                    res.setRestoredAt(null);
                    return repository.save(res);
                });
    }

    // Restaurar lógico
    public Mono<Reservation> restore(String id) {
        return repository.findById(id)
                .flatMap(res -> {
                    res.setIsDeleted(false);
                    res.setRestoredAt(LocalDateTime.now());
                    res.setUpdatedAt(LocalDateTime.now());
                    return repository.save(res);
                });
    }
}