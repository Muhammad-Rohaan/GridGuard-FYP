package com.fyp.gridguard.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubstationsRequestDTO {

    private String code;
    private String name;
    private String subProfileType; // e.g., "RESIDENTIAL", "COMMERCIAL", "INDUSTRIAL", "MIXED"
    private String status;         // e.g., "HEALTHY", "WARNING", "CRITICAL"
}