package com.fyp.gridguard.Controllers;

import com.fyp.gridguard.Models.Areas;
import com.fyp.gridguard.Models.Substations;
import com.fyp.gridguard.Repositories.AreaRepo;
import com.fyp.gridguard.Repositories.SubstationRepo;
import com.fyp.gridguard.Services.SimulationClock;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LiveController {

    private final AreaRepo areaRepository;
    private final SubstationRepo substationRepository;
    private final SimulationClock clock;

    @GetMapping("/live")
    public ResponseEntity<?> getLiveData() {

        List<Areas> areas = areaRepository.findAll();
        List<Substations> substations = substationRepository.findAll();

        if (areas.isEmpty() || substations.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "No area or substation found in database"
            ));
        }

        Areas area = areas.get(0);
        Substations substation = substations.get(0);

        // Area data
        Map<String, Object> areaData = new HashMap<>();
        areaData.put("id", area.getId());
        areaData.put("code", area.getCode());
        areaData.put("name", area.getName());
        areaData.put("type", area.getType());
        areaData.put("currentLoadKw", area.getCurrentLoadKw() != null ? area.getCurrentLoadKw() : 0.0);
        areaData.put("mine", true);

        // Substation data
        Map<String, Object> substationData = new HashMap<>();
        substationData.put("id", substation.getSubstationId());
        substationData.put("code", substation.getCode());
        substationData.put("name", substation.getName());
        substationData.put("capacityKw", substation.getCapacityKw());
        substationData.put("currentLoadKw", substation.getCurrentLoadKw() != null ? substation.getCurrentLoadKw() : 0.0);
        substationData.put("areas", List.of(areaData));

        // Final response (using HashMap instead of Map.of to avoid null issues)
        Map<String, Object> data = new HashMap<>();
        data.put("simTime", clock.now() != null ? clock.now().toString() : "not-initialized");
        data.put("mode", "REPLAY");
        data.put("substations", List.of(substationData));

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", data);

        return ResponseEntity.ok(response);
    }
}