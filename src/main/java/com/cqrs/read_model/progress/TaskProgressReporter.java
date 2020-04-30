package com.cqrs.read_model.progress;

public interface TaskProgressReporter {
    public void reportProgressUpdate(int currentStep, int steps, float speedInItemsPerSec, float etaInSeconds);
}
