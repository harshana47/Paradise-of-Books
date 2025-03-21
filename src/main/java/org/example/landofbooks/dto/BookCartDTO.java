package org.example.landofbooks.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class BookCartDTO {
    private UUID bkcid;
    private UUID bookId;
    private UUID userId;
    private String title;
    private String image;
    private double total;
    private int qty;
}
