package com.fyp.gridguard.Services;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class SimulationClock {
    private LocalDateTime simTime;

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