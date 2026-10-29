package com.fyp.gridguard.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fyp.gridguard.DTOs.SubstationsRequestDTO;
import com.fyp.gridguard.Models.Substations;
import com.fyp.gridguard.Repositories.SubstationRepo;

@Service
public class SubstationService {

    @Autowired
    private SubstationRepo substationRepo;

    public String addSubstation(SubstationsRequestDTO substationsRequestDTO) {
        try {
            if (substationsRequestDTO != null) {
                Substations newSubstation = new Substations(
                        substationsRequestDTO.getCode(),
                        substationsRequestDTO.getName(),
                        substationsRequestDTO.getSubProfileType(),
                        substationsRequestDTO.getStatus(),
                        substationsRequestDTO.getCapacityKw(),
                        substationsRequestDTO.getCurrentLoadKw()
                );
                substationRepo.save(newSubstation);
                return "Substation Created";
            } else {
                return "Send all values.";
            }

        } catch (Exception e) {
            // TODO: handle exception
            throw new RuntimeException(e);
        }
    }

    public List<Substations> fetchAllSubstations() {
        try {
            return substationRepo.findAll();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
