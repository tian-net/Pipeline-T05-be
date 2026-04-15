package vallegrande.edu.pe.Elchino.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationTableRequest {
    private String reservationId;
    private String tableId;
    private String notes;
}
