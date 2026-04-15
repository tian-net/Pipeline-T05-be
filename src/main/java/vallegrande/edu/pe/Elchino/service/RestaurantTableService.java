package vallegrande.edu.pe.Elchino.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.Elchino.model.RestaurantTable;
import vallegrande.edu.pe.Elchino.repository.RestaurantTableRepository;

import java.time.LocalDateTime;

@Service
public class RestaurantTableService {

    private final RestaurantTableRepository repository;

    public RestaurantTableService(RestaurantTableRepository repository) {
        this.repository = repository;
    }

    public Flux<RestaurantTable> findAllActive() {
        return repository.findByIsDeleted(false);
    }

    public Flux<RestaurantTable> findAllDeleted() {
        return repository.findByIsDeleted(true);
    }

    public Flux<RestaurantTable> findAllReservable() {
        return repository.findByIsDeletedFalseAndIsReservableTrue();
    }

    public Flux<RestaurantTable> findAvailableForReservation() {
        return repository.findByIsDeletedFalseAndIsReservableTrueAndStatus("Libre");
    }

    public Mono<RestaurantTable> findById(String id) {
        return repository.findById(id);
    }

    public Mono<RestaurantTable> create(RestaurantTable table) {
        validate(table);
        table.setId(null);
        table.setCreatedAt(LocalDateTime.now());
        table.setUpdatedAt(LocalDateTime.now());
        if (table.getIsDeleted() == null) table.setIsDeleted(false);
        if (table.getIsReservable() == null) table.setIsReservable(true);
        if (table.getStatus() == null || table.getStatus().isBlank()) table.setStatus("Libre");
        return repository.save(table);
    }

    public Mono<RestaurantTable> update(String id, RestaurantTable details) {
        validate(details);
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Mesa no encontrada con ID " + id)))
                .flatMap(existing -> {
                    details.setId(existing.getId());
                    details.setCreatedAt(existing.getCreatedAt());
                    details.setDeletedAt(existing.getDeletedAt());
                    details.setRestoredAt(existing.getRestoredAt());
                    details.setIsDeleted(existing.getIsDeleted());
                    details.setUpdatedAt(LocalDateTime.now());
                    return repository.save(details);
                });
    }

    public Mono<RestaurantTable> deleteLogical(String id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Mesa no encontrada con ID " + id)))
                .flatMap(table -> {
                    table.setIsDeleted(true);
                    table.setDeletedAt(LocalDateTime.now());
                    table.setRestoredAt(null);
                    table.setUpdatedAt(LocalDateTime.now());
                    return repository.save(table);
                });
    }

    public Mono<RestaurantTable> restore(String id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Mesa no encontrada con ID " + id)))
                .flatMap(table -> {
                    table.setIsDeleted(false);
                    table.setRestoredAt(LocalDateTime.now());
                    table.setUpdatedAt(LocalDateTime.now());
                    return repository.save(table);
                });
    }

    public Mono<Void> deletePhysical(String id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Mesa no encontrada con ID " + id)))
                .flatMap(existing -> repository.deleteById(existing.getId()));
    }

    private void validate(RestaurantTable table) {
        if (table.getTableNum() == null) {
            throw new IllegalArgumentException("El número de mesa es requerido");
        }
        if (table.getLocation() == null || table.getLocation().trim().isEmpty()) {
            throw new IllegalArgumentException("La ubicación es requerida");
        }
        if (table.getCapacity() == null || table.getCapacity() < 1 || table.getCapacity() > 6) {
            throw new IllegalArgumentException("La capacidad debe estar entre 1 y 6");
        }
    }
}
