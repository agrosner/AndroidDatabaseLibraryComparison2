package com.grosner.androiddatabaselibrarycomparison2.events;

/**
 * Event to indicate that a trial has been completed
 */
public class TrialCompletedEvent {
    private final String trialName;

    public TrialCompletedEvent(String testName) {
        this.trialName = testName;
    }

    public String getTrialName() {
        return trialName;
    }
}
