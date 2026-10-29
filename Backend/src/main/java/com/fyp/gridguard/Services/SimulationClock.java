package com.fyp.gridguard.Services;

import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class SimulationClock {

    private LocalDateTime simTime = LocalDateTime.of(2017, 1, 1, 0, 0); // default start

    public LocalDateTime now() {
        return simTime;
    }

    public void set(LocalDateTime time) {
        this.simTime = time;
    }

    public void advance(int minutes) {
        this.simTime = this.simTime.plusMinutes(minutes);
    }
}