package com.fyp.gridguard.Controllers;

import com.fyp.gridguard.Models.Areas;
import com.fyp.gridguard.Models.LoadReading;
import com.fyp.gridguard.Services.TestSimulation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LiveController {

    private final TestSimulation testSimulation;

    // Returns the ordered readings for the current local date and hour.
    @GetMapping("/live")
    public ResponseEntity<List<LoadReading>> findByDateTime() {
        LocalDateTime currentTime = LocalDateTime.now();
        String currentDateAndHour = currentTime.format(DateTimeFormatter.ofPattern("MM-dd HH"));
        List<LoadReading> response = testSimulation.findByDateTime(currentDateAndHour);
        return ResponseEntity.ok(response);
    }

}