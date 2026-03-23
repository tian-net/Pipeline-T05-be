package vallegrande.edu.pe.Elchino.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(collection = "customers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    @Id
    private String id;

    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private String docType;
    private String docNum;
    private LocalDate regDate;
    private Boolean isFrequent;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private LocalDateTime restoredAt;
    private Boolean isDeleted;
}