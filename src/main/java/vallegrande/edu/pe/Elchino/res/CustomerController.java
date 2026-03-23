package vallegrande.edu.pe.Elchino.res;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import vallegrande.edu.pe.Elchino.model.Customer;
import vallegrande.edu.pe.Elchino.service.CustomerService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/customers")
@CrossOrigin(origins = "*")
@Tag(name = "Customer", description = "Operaciones CRUD reactivas para clientes")
public class CustomerController {

    private final CustomerService service;

    public CustomerController(CustomerService service) {
        this.service = service;
    }

    // ✅ Listar x Estado (GET) - FLUX (Activos)
    @GetMapping
    @Operation(summary = "Listar clientes activos")
    public Flux<Customer> getAllActive() {
        return service.findByStatus(false);
    }

    // ✅ Listar x Estado (GET) - FLUX (Eliminados)
    @GetMapping("/deleted")
    @Operation(summary = "Listar clientes eliminados lógicamente")
    public Flux<Customer> getDeleted() {
        return service.findByStatus(true);
    }

    // ✅ Listar x ID (GET) - MONO
    @GetMapping("/{id}")
    @Operation(summary = "Obtener cliente por ID")
    public Mono<Customer> getById(@PathVariable String id) {
        return service.findById(id);
    }

    // ✅ Crear (POST) - MONO
    @PostMapping
    @Operation(summary = "Crear un nuevo cliente")
    public Mono<Customer> create(@RequestBody Customer customer) {
        return service.create(customer);
    }

    // ✅ Editar (PUT) - MONO
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un cliente existente")
    public Mono<Customer> update(@PathVariable String id, @RequestBody Customer customer) {
        return service.update(id, customer);
    }

    // ✅ Eliminar lógico (PATCH) - MONO
    @PatchMapping("/logical/{id}")
    @Operation(summary = "Eliminar lógicamente un cliente")
    public Mono<Customer> deleteLogical(@PathVariable String id) {
        return service.deleteLogical(id);
    }

    // ✅ Restaurar lógico (PATCH) - MONO
    @PatchMapping("/restore/{id}")
    @Operation(summary = "Restaurar un cliente eliminado lógicamente")
    public Mono<Customer> restore(@PathVariable String id) {
        return service.restore(id);
    }
}