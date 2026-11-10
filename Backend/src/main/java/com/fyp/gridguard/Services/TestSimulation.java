package com.fyp.gridguard.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.fyp.gridguard.Models.LoadReading;
import com.fyp.gridguard.Repositories.LoadReadingRepo;

@Component
public class TestSimulation {

    @Autowired 
    private LoadReadingRepo loadReadingRepo;

    public List<LoadReading> findByDateTime(String userCurrentDateTime) {
        return loadReadingRepo.findByDateTime(userCurrentDateTime);
    }


}
