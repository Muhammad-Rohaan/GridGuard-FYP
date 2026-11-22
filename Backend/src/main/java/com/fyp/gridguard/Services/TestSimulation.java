package com.fyp.gridguard.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.fyp.gridguard.Models.ResidentialLoadReading;
import com.fyp.gridguard.Repositories.LoadReadingRepo;

@Component
public class TestSimulation {

    @Autowired 
    private LoadReadingRepo loadReadingRepo;

    // Delegates current-hour reading lookup to the load-reading repository.
    public List<ResidentialLoadReading> findByDateTime(String userCurrentDateTime) {
        return loadReadingRepo.findByDateTime(userCurrentDateTime);
    }


}
