package com.fyp.gridguard.Models;

import java.awt.geom.Area;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table (name = "load_readings")
@Getter 
@Setter 
@AllArgsConstructor 
@NoArgsConstructor 
@ToString 
public class LoadReading {  // Residential Power consumption, 5s of interval

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "area_id")
    private Areas area;                // always the single residential area

    private LocalDateTime recordedAt; // 2017-01-01 00:00:00
    private Double loadKw;            // 360.5



}