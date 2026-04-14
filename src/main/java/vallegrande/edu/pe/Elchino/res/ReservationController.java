package vallegrande.edu.pe.Elchino.res;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import vallegrande.edu.pe.Elchino.model.Reservation;
import vallegrande.edu.pe.Elchino.service.ReservationService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/reservations")
@CrossOrigin(origins = "*")
@Tag(name = "Reservation", description = "Operaciones transaccionales para reservas")
public class ReservationController {

    private final ReservationService service;

    public ReservationController(ReservationService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar reservas activas")
    public Flux<Reservation> getAll() {
        return service.findAllActive();
    }

    @PostMapping
    @Operation(summary = "Crear reserva con cabecera y detalle")
    public Mono<Reservation> create(@RequestBody Reservation reservation) {
        return service.create(reservation);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una reserva existente")
    public Mono<Reservation> update(@PathVariable String id, @RequestBody Reservation res) {
        return service.update(id, res);
    }

    @PatchMapping("/logical/{id}")
    @Operation(summary = "Eliminar lógicamente una reserva")
    public Mono<Reservation> delete(@PathVariable String id) {
        return service.deleteLogical(id);
    }

    @PatchMapping("/restore/{id}")
    @Operation(summary = "Restaurar una reserva eliminada")
    public Mono<Reservation> restore(@PathVariable String id) {
        return service.restore(id);
    }
}