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

    private String name;
    private String code;
    private String type;

    @ManyToOne
    @JoinColumn(name = "substation_id")
    private Substations substation;

    private Double currentLoadKw;
    private LocalDateTime updatedAt;

}
