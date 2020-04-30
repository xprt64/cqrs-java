package com.cqrs.read_model;

import java.util.HashMap;

public class OnlyOnceTracker {

    private final HashMap<String, Boolean> alreadyAppliedEventsByReadModel = new HashMap<>();

    public boolean isEventAlreadyApplied(ReadModel readModel, String eventId)
    {
        return alreadyAppliedEventsByReadModel.get(makeId(readModel, eventId)) != null;
    }

    public void markEventAsApplied(ReadModel readModel, String eventId)
    {
        alreadyAppliedEventsByReadModel.put(makeId(readModel, eventId), true);
    }

    private String makeId(ReadModel readModel, String eventId)
    {
        return readModel.getClass().getCanonicalName() + eventId;
    }
}
