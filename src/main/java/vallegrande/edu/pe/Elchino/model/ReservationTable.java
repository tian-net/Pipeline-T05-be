package vallegrande.edu.pe.Elchino.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationTable {
    private Integer tableId;
    private String notes;
}