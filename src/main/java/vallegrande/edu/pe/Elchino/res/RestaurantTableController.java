package vallegrande.edu.pe.Elchino.res;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.Elchino.model.RestaurantTable;
import vallegrande.edu.pe.Elchino.service.RestaurantTableService;

@RestController
@RequestMapping("/api/tables")
@CrossOrigin(origins = "*")
@Tag(name = "RestaurantTable", description = "Operaciones CRUD para mesas del restaurante")
public class RestaurantTableController {

    private final RestaurantTableService service;

    public RestaurantTableController(RestaurantTableService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar mesas activas")
    public Flux<RestaurantTable> getAllActive() {
        return service.findAllActive();
    }

    @GetMapping("/reservable")
    @Operation(summary = "Listar mesas reservables activas")
    public Flux<RestaurantTable> getAllReservable() {
        return service.findAllReservable();
    }

    @GetMapping("/available")
    @Operation(summary = "Listar mesas libres para reservar")
    public Flux<RestaurantTable> getAvailableForReservation() {
        return service.findAvailableForReservation();
    }

    @GetMapping("/deleted")
    @Operation(summary = "Listar mesas eliminadas lógicamente")
    public Flux<RestaurantTable> getDeleted() {
        return service.findAllDeleted();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener mesa por ID")
    public Mono<RestaurantTable> getById(@PathVariable String id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear una nueva mesa")
    public Mono<RestaurantTable> create(@RequestBody RestaurantTable table) {
        return service.create(table);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una mesa existente")
    public Mono<RestaurantTable> update(@PathVariable String id, @RequestBody RestaurantTable table) {
        return service.update(id, table);
    }

    @PatchMapping("/logical/{id}")
    @Operation(summary = "Eliminar lógicamente una mesa")
    public Mono<RestaurantTable> deleteLogical(@PathVariable String id) {
        return service.deleteLogical(id);
    }

    @PatchMapping("/restore/{id}")
    @Operation(summary = "Restaurar una mesa eliminada lógicamente")
    public Mono<RestaurantTable> restore(@PathVariable String id) {
        return service.restore(id);
    }

    @DeleteMapping("/physical/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar físicamente una mesa")
    public Mono<Void> deletePhysical(@PathVariable String id) {
        return service.deletePhysical(id);
    }
}
