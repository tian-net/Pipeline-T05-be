package vallegrande.edu.pe.Elchino.res;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.Elchino.dto.ReservationTableRequest;
import vallegrande.edu.pe.Elchino.dto.ReservationTableUpdateRequest;
import vallegrande.edu.pe.Elchino.model.ReservationTable;
import vallegrande.edu.pe.Elchino.service.ReservationTableService;

@RestController
@RequestMapping("/api/reservation-tables")
@CrossOrigin(origins = "*")
@Tag(name = "ReservationTable", description = "Operaciones transaccionales para detalle mesa-reserva")
public class ReservationTableController {

    private final ReservationTableService service;

    public ReservationTableController(ReservationTableService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar asignaciones activas")
    public Flux<ReservationTable> getAllActive() {
        return service.findAllActive();
    }

    @GetMapping("/deleted")
    @Operation(summary = "Listar asignaciones eliminadas lógicamente")
    public Flux<ReservationTable> getDeleted() {
        return service.findAllDeleted();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener asignación por ID")
    public Mono<ReservationTable> getById(@PathVariable String id) {
        return service.findById(id);
    }

    @GetMapping("/reservation/{reservationId}")
    @Operation(summary = "Listar mesas de una reserva")
    public Flux<ReservationTable> getByReservation(@PathVariable String reservationId) {
        return service.findByReservation(reservationId);
    }

    @GetMapping("/table/{tableId}")
    @Operation(summary = "Listar reservas activas de una mesa")
    public Flux<ReservationTable> getByTable(@PathVariable String tableId) {
        return service.findByTable(tableId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registrar una asignación mesa-reserva")
    public Mono<ReservationTable> create(@RequestBody ReservationTableRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar notas de una asignación")
    public Mono<ReservationTable> updateNotes(@PathVariable String id, @RequestBody ReservationTableUpdateRequest request) {
        return service.updateNotes(id, request);
    }

    @PatchMapping("/logical/{id}")
    @Operation(summary = "Eliminar lógicamente una asignación")
    public Mono<ReservationTable> deleteLogical(@PathVariable String id) {
        return service.deleteLogical(id);
    }

    @PatchMapping("/restore/{id}")
    @Operation(summary = "Restaurar una asignación eliminada lógicamente")
    public Mono<ReservationTable> restore(@PathVariable String id) {
        return service.restore(id);
    }

    @DeleteMapping("/physical/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar físicamente una asignación")
    public Mono<Void> deletePhysical(@PathVariable String id) {
        return service.deletePhysical(id);
    }
}
