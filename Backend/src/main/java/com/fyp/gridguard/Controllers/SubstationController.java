package com.fyp.gridguard.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fyp.gridguard.DTOs.SubstationsRequestDTO;
import com.fyp.gridguard.Models.Substations;
import com.fyp.gridguard.Routes.SubstationRoutes;
import com.fyp.gridguard.Services.SubstationService;

import java.util.List;


@RestController 
@RequestMapping("api/technical-worker/substations")
public class SubstationController extends SubstationRoutes{

    @Autowired
    private SubstationService substationService;

    @Override
    @PostMapping("/register-substation")
    public ResponseEntity<String> addSubstation(@RequestBody SubstationsRequestDTO substationsRequestDTO) {
        try {
            String response = substationService.addSubstation(substationsRequestDTO);
            
            return ResponseEntity.status(200).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("ERROR in Registering Substation " + e);
        }
    }

    @Override
    @GetMapping("/fetch-subs")
    public ResponseEntity<List<Substations>> fetchAllSubstations() { // Not performed API Testing
        try {
            List<Substations> substations = substationService.fetchAllSubstations();

            return ResponseEntity.status(200).body(substations);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @PutMapping("/update-substation/{substationId}")
    public ResponseEntity<String> updateSubstation(
            @PathVariable Long substationId,
            @RequestBody SubstationsRequestDTO substationsRequestDTO) {
        if (substationsRequestDTO == null) {
            return ResponseEntity.badRequest().body("Substation data is required.");
        }

        if (!substationService.updateSubstation(substationId, substationsRequestDTO)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok("Substation Updated");
    }

    @Override
    @DeleteMapping("/delete-substation/{substationId}")
    public ResponseEntity<String> deleteSubstation(@PathVariable Long substationId) {
        if (!substationService.deleteSubstation(substationId)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok("Substation Deleted");
    }

}