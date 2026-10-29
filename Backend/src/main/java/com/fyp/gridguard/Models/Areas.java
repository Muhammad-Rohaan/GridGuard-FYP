package com.fyp.gridguard.Models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table (name = "areas")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Areas {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;              // "Residential Area 1"
    private String code;              // "AREA-R1"
    private String type;              // "RESIDENTIAL"

    @ManyToOne
    @JoinColumn(name = "substation_id")
    private Substations substation;    // always points to the single substation

    private Double currentLoadKw;     // updated by tick engine
    private LocalDateTime updatedAt;

}
