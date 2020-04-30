package com.cqrs.read_model.progress;

import java.time.Instant;

public class TaskProgressCalculator {

    private final int totalSteps;

    private Instant startTime;

    private int step = 0;

    public TaskProgressCalculator(
        int totalSteps
    )
    {
        this.totalSteps = totalSteps;

        setStartTime(Instant.now());
    }

    public void setStartTime(Instant startTime)
    {
        this.startTime = startTime;
    }

    public void increment()
    {
        step++;
    }

    public int getTotalSteps()
    {
        return totalSteps;
    }

    public int getStep()
    {
        return step;
    }

    public float calculateSpeed()
    {
        return ((float) step) / (Instant.now().toEpochMilli() - startTime.toEpochMilli());
    }

    public float calculateEta()
    {
        return (totalSteps - step) / calculateSpeed();
    }
}
