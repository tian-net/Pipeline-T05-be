package vallegrande.edu.pe.Elchino.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "reservations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation {

    @Id
    private String id;
    private String customerId;
    private LocalDate resDate;
    private String resTime;
    private Integer numPeople;
    private String status;
    private Double totalAmt;

    // Relación Cabecera-Detalle (Embebido)
    private List<ReservationTable> details;

    // Campos de Auditoría
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private LocalDateTime restoredAt;
    private Boolean isDeleted;
}