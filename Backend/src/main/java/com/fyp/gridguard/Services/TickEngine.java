//package com.fyp.gridguard.Services;
//
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
