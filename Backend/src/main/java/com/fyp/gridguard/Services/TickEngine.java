package com.fyp.gridguard.Services;

//import com.fyp.gridguard.Models.Areas;
//import com.fyp.gridguard.Models.Substations;
//import com.fyp.gridguard.Repositories.AreaRepo;
//import com.fyp.gridguard.Repositories.LoadReadingRepo;
//import com.fyp.gridguard.Repositories.SubstationRepo;
//import jakarta.transaction.Transactional;
//import lombok.RequiredArgsConstructor;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalDateTime;
//import java.util.Optional;
//
//@Component
//@RequiredArgsConstructor
//public class TickEngine {
//
//    private final SimulationClock clock;
//    private final AreaRepo areaRepo;
//    private final SubstationRepo substationRepo;
//    private final LoadReadingRepo loadReadingRepo;
//
//    // Runs every 5 seconds (real time)
//    @Scheduled(fixedDelay = 5000)
//    @Transactional
//    public void tick() {
//
//        // 1. Advance simulation time by 5 minutes
//        clock.advance(5);
//
//        LocalDateTime currentSimTime = clock.now();
//
//        // 2. Get the single residential area
//        Areas area = areaRepo.findAll().get(0);   // only one area exists
//
//        // 3. Find the matching row from dataset
//        Optional<LoadReadingRepo> reading = loadReadingRepo
//                .findByAreaIdAndRecordedAt(area.getId(), currentSimTime);
//
//        // 4. Update area load
//        if (reading.isPresent()) {
//            area.setCurrentLoadKw(reading.get().getLoadKw());
//            area.setUpdatedAt(LocalDateTime.now());
//            areaRepo.save(area);
//        }
//        // if no row found → keep previous value (safe)
//
//        // 5. Update the single substation load
//        // (because only one area exists, substation load = area load)
//        Substations substation = area.getSubstation();
//        substation.setCurrentLoadKw(area.getCurrentLoadKw());
//        substationRepo.save(substation);
//    }
//}
/// ------------------------------------------------

import com.fyp.gridguard.Models.Areas;
import com.fyp.gridguard.Models.LoadReading;
import com.fyp.gridguard.Models.Substations;
import com.fyp.gridguard.Repositories.AreaRepo;
import com.fyp.gridguard.Repositories.LoadReadingRepo;
import com.fyp.gridguard.Repositories.SubstationRepo;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class TickEngine {

    private final SimulationClock clock;
    private final AreaRepo areaRepository;
    private final SubstationRepo substationRepository;
    private final LoadReadingRepo loadReadingRepository;

    @PostConstruct
    public void init() {
        // Start from the first timestamp of your dataset
        Optional<LocalDateTime> minTime = loadReadingRepository.findMinRecordedAt();
        if (minTime.isPresent()) {
            clock.set(minTime.get());
            log.info("Simulation started at: {}", clock.now());
        } else {
            clock.set(LocalDateTime.of(2017, 1, 1, 0, 0));
            log.warn("No data found in load_readings. Using default time.");
        }
    }

    @Scheduled(fixedDelay = 5000)   // every 5 real seconds
    @Transactional
    public void tick() {

        // 1. Advance simulation time by 5 minutes
        clock.advance(5);
        LocalDateTime currentSimTime = clock.now();

        log.info("Tick running → Simulation Time: {}", currentSimTime);

        // 2. Get the single area
        List<Areas> areas = areaRepository.findAll();
        if (areas.isEmpty()) {
            log.warn("No area found");
            return;
        }
        Areas area = areas.get(0);

        // 3. Find matching reading
        Optional<LoadReading> reading = loadReadingRepository
                .findByAreaIdAndRecordedAt(area.getId(), currentSimTime);

        if (reading.isPresent()) {
            Double load = reading.get().getLoadKw();

            // 4. Update Area
            area.setCurrentLoadKw(load);
            area.setUpdatedAt(LocalDateTime.now());
            areaRepository.save(area);

            // 5. Update Substation
            Substations substation = area.getSubstation(); // or fetch by ID
            if (substation != null) {
                substation.setCurrentLoadKw(load);
                substationRepository.save(substation);
            }

            log.info("Updated Load → {} kW", load);
        } else {
            log.warn("No reading found for time: {}", currentSimTime);
        }
    }
}