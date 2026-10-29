package com.fyp.gridguard.Routes;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import com.fyp.gridguard.DTOs.SubstationsRequestDTO;
import com.fyp.gridguard.Models.Substations;

import java.util.List;

public abstract class SubstationRoutes {
    public abstract ResponseEntity<String> addSubstation(@RequestBody SubstationsRequestDTO substation);
    public abstract ResponseEntity<List<Substations>> fetchAllSubstations();
}
