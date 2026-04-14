package vallegrande.edu.pe.Elchino.service;

import org.springframework.stereotype.Service;
import vallegrande.edu.pe.Elchino.model.Customer;
import vallegrande.edu.pe.Elchino.repository.CustomerRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class CustomerService {

    private final CustomerRepository repository;

    public CustomerService(CustomerRepository repository) {
        this.repository = repository;
    }

    // Listar todos - FLUX
    public Flux<Customer> findAll() {
        return repository.findAll();
    }

    // Listar x Estado - FLUX
    public Flux<Customer> findByStatus(Boolean isDeleted) {
        return repository.findByIsDeleted(isDeleted);
    }

    // Listar x ID - MONO
    public Mono<Customer> findById(String id) {
        return repository.findById(id);
    }

    // Crear - MONO
    public Mono<Customer> create(Customer customer) {
        customer.setId(null); // Asegura que se genere un nuevo ID en MongoDB
        customer.setCreatedAt(LocalDateTime.now());
        if (customer.getRegDate() == null) customer.setRegDate(LocalDate.now());
        if (customer.getIsDeleted() == null) customer.setIsDeleted(false);
        if (customer.getIsFrequent() == null) customer.setIsFrequent(false);
        
        return repository.save(customer);
    }

    // Editar - MONO
    public Mono<Customer> update(String id, Customer customerDetails) {
        return repository.findById(id)
                .flatMap(existing -> {
                    customerDetails.setId(existing.getId());
                    // Preservar datos de auditoría originales
                    customerDetails.setCreatedAt(existing.getCreatedAt());
                    customerDetails.setDeletedAt(existing.getDeletedAt());
                    customerDetails.setRestoredAt(existing.getRestoredAt());
                    customerDetails.setIsDeleted(existing.getIsDeleted());
                    // Actualizar fecha de modificación
                    customerDetails.setUpdatedAt(LocalDateTime.now());
                    
                    return repository.save(customerDetails);
                });
    }

    // Eliminar lógico - MONO
    public Mono<Customer> deleteLogical(String id) {
        return repository.findById(id)
                .flatMap(customer -> {
                    customer.setIsDeleted(true);
                    customer.setDeletedAt(LocalDateTime.now());
                    customer.setRestoredAt(null); // Limpiar fecha de restauración previa
                    return repository.save(customer);
                });
    }

    // Restaurar lógico - MONO
    public Mono<Customer> restore(String id) {
        return repository.findById(id)
                .flatMap(customer -> {
                    customer.setIsDeleted(false);
                    customer.setRestoredAt(LocalDateTime.now());
                    return repository.save(customer);
                });
    }
}