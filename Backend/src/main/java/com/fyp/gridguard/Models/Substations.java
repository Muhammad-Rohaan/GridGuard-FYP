package com.fyp.gridguard.Models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.*;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table (name = "substations")
@Getter 
@Setter 
@AllArgsConstructor 
@NoArgsConstructor 
@ToString 
public class Substations {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long substationId;
    
    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String subProfileType;
    
    @Column(nullable = false)
    private String status;

    public Substations(String code, String name, String subProfileType, String status) {
        this.code = code;
        this.name = name;
        this.subProfileType = subProfileType;
        this.status = status;
    }


}
