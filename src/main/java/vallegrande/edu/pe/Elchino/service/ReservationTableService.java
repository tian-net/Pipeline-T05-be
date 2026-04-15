package vallegrande.edu.pe.Elchino.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.Elchino.dto.ReservationTableRequest;
import vallegrande.edu.pe.Elchino.dto.ReservationTableUpdateRequest;
import vallegrande.edu.pe.Elchino.model.Reservation;
import vallegrande.edu.pe.Elchino.model.ReservationTable;
import vallegrande.edu.pe.Elchino.model.RestaurantTable;
import vallegrande.edu.pe.Elchino.repository.ReservationRepository;
import vallegrande.edu.pe.Elchino.repository.ReservationTableRepository;
import vallegrande.edu.pe.Elchino.repository.RestaurantTableRepository;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Set;

@Service
public class ReservationTableService {

    private static final Set<String> ACTIVE_RESERVATION_STATUS = Set.of("pendiente", "confirmada");

    private final ReservationTableRepository reservationTableRepository;
    private final ReservationRepository reservationRepository;
    private final RestaurantTableRepository restaurantTableRepository;

    public ReservationTableService(
            ReservationTableRepository reservationTableRepository,
            ReservationRepository reservationRepository,
            RestaurantTableRepository restaurantTableRepository
    ) {
        this.reservationTableRepository = reservationTableRepository;
        this.reservationRepository = reservationRepository;
        this.restaurantTableRepository = restaurantTableRepository;
    }

    public Flux<ReservationTable> findAllActive() {
        return reservationTableRepository.findByIsDeleted(false);
    }

    public Flux<ReservationTable> findAllDeleted() {
        return reservationTableRepository.findByIsDeleted(true);
    }

    public Mono<ReservationTable> findById(String id) {
        return reservationTableRepository.findById(id);
    }

    public Flux<ReservationTable> findByReservation(String reservationId) {
        return reservationTableRepository.findByReservationIdAndIsDeleted(reservationId, false);
    }

    public Flux<ReservationTable> findByTable(String tableId) {
        return reservationTableRepository.findByTableIdAndIsDeleted(tableId, false);
    }

    public Mono<ReservationTable> create(ReservationTableRequest request) {
        return reservationRepository.findById(request.getReservationId())
                .switchIfEmpty(Mono.error(new RuntimeException("Reserva no encontrada con ID " + request.getReservationId())))
                .flatMap(reservation -> validateReservation(reservation)
                        .then(restaurantTableRepository.findById(request.getTableId()))
                        .switchIfEmpty(Mono.error(new RuntimeException("Mesa no encontrada con ID " + request.getTableId())))
                        .flatMap(table -> validateTable(table)
                                .then(hasConflicts(table.getId(), reservation))
                                .flatMap(hasConflicts -> {
                                    if (hasConflicts) {
                                        return Mono.error(new RuntimeException("La mesa " + table.getTableNum() + " ya está reservada para esa fecha y hora"));
                                    }
                                    ReservationTable reservationTable = ReservationTable.builder()
                                            .reservationId(request.getReservationId())
                                            .tableId(request.getTableId())
                                            .notes(request.getNotes())
                                            .createdAt(LocalDateTime.now())
                                            .updatedAt(LocalDateTime.now())
                                            .isDeleted(false)
                                            .build();
                                    return reservationTableRepository.save(reservationTable)
                                            .flatMap(saved -> {
                                                table.setStatus("Reservada");
                                                table.setUpdatedAt(LocalDateTime.now());
                                                return restaurantTableRepository.save(table).thenReturn(saved);
                                            });
                                })));
    }

    public Mono<ReservationTable> updateNotes(String id, ReservationTableUpdateRequest request) {
        return reservationTableRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Detalle de reserva no encontrado con ID " + id)))
                .flatMap(existing -> {
                    if (Boolean.TRUE.equals(existing.getIsDeleted())) {
                        return Mono.error(new RuntimeException("El detalle está eliminado lógicamente y no puede actualizarse"));
                    }
                    existing.setNotes(request.getNotes());
                    existing.setUpdatedAt(LocalDateTime.now());
                    return reservationTableRepository.save(existing);
                });
    }

    public Mono<ReservationTable> deleteLogical(String id) {
        return reservationTableRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Detalle de reserva no encontrado con ID " + id)))
                .flatMap(existing -> {
                    if (Boolean.TRUE.equals(existing.getIsDeleted())) {
                        return Mono.just(existing);
                    }
                    existing.setIsDeleted(true);
                    existing.setDeletedAt(LocalDateTime.now());
                    existing.setRestoredAt(null);
                    existing.setUpdatedAt(LocalDateTime.now());
                    return reservationTableRepository.save(existing)
                            .flatMap(saved -> releaseTableIfNoActiveReservations(saved.getTableId()).thenReturn(saved));
                });
    }

    public Mono<ReservationTable> restore(String id) {
        return reservationTableRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Detalle de reserva no encontrado con ID " + id)))
                .flatMap(existing -> {
                    if (!Boolean.TRUE.equals(existing.getIsDeleted())) {
                        return Mono.just(existing);
                    }
                    existing.setIsDeleted(false);
                    existing.setRestoredAt(LocalDateTime.now());
                    existing.setDeletedAt(null);
                    existing.setUpdatedAt(LocalDateTime.now());
                    return reservationTableRepository.save(existing)
                            .flatMap(saved -> restaurantTableRepository.findById(saved.getTableId())
                                    .flatMap(table -> {
                                        table.setStatus("Reservada");
                                        table.setUpdatedAt(LocalDateTime.now());
                                        return restaurantTableRepository.save(table);
                                    })
                                    .thenReturn(saved));
                });
    }

    public Mono<Void> deletePhysical(String id) {
        return reservationTableRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Detalle de reserva no encontrado con ID " + id)))
                .flatMap(existing -> reservationTableRepository.deleteById(id)
                        .then(releaseTableIfNoActiveReservations(existing.getTableId())));
    }

    private Mono<Boolean> hasConflicts(String tableId, Reservation reservation) {
        return reservationTableRepository.findByTableIdAndIsDeleted(tableId, false)
                .flatMap(existingDetail -> reservationRepository.findById(existingDetail.getReservationId()))
                .filter(existingReservation ->
                        !Boolean.TRUE.equals(existingReservation.getIsDeleted())
                                && ACTIVE_RESERVATION_STATUS.contains(normalize(existingReservation.getStatus()))
                                && existingReservation.getResDate() != null
                                && existingReservation.getResDate().equals(reservation.getResDate())
                                && withinTwoHours(existingReservation.getResTime(), reservation.getResTime()))
                .hasElements();
    }

    private Mono<Void> validateReservation(Reservation reservation) {
        if (Boolean.TRUE.equals(reservation.getIsDeleted())) {
            return Mono.error(new RuntimeException("La reserva está eliminada lógicamente"));
        }
        if (reservation.getResDate() == null || reservation.getResTime() == null || reservation.getResTime().isBlank()) {
            return Mono.error(new RuntimeException("La reserva debe tener fecha y hora para validar conflictos"));
        }
        return Mono.empty();
    }

    private Mono<Void> validateTable(RestaurantTable table) {
        if (Boolean.TRUE.equals(table.getIsDeleted())) {
            return Mono.error(new RuntimeException("La mesa está eliminada"));
        }
        if (!Boolean.TRUE.equals(table.getIsReservable())) {
            return Mono.error(new RuntimeException("La mesa no es reservable"));
        }
        if (!"libre".equalsIgnoreCase(table.getStatus())) {
            return Mono.error(new RuntimeException("La mesa no está libre"));
        }
        return Mono.empty();
    }

    private Mono<Void> releaseTableIfNoActiveReservations(String tableId) {
        return reservationTableRepository.findByTableIdAndIsDeleted(tableId, false)
                .hasElements()
                .flatMap(hasActive -> {
                    if (hasActive) {
                        return Mono.empty();
                    }
                    return restaurantTableRepository.findById(tableId)
                            .flatMap(table -> {
                                table.setStatus("Libre");
                                table.setUpdatedAt(LocalDateTime.now());
                                return restaurantTableRepository.save(table);
                            })
                            .then();
                });
    }

    private boolean withinTwoHours(String timeA, String timeB) {
        LocalTime left = parseTime(timeA);
        LocalTime right = parseTime(timeB);
        long minutes = Math.abs(left.toSecondOfDay() - right.toSecondOfDay()) / 60;
        return minutes <= 120;
    }

    private LocalTime parseTime(String time) {
        try {
            return LocalTime.parse(time);
        } catch (DateTimeParseException ex) {
            throw new RuntimeException("Formato de hora inválido: " + time + ". Use HH:mm o HH:mm:ss");
        }
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }
}
