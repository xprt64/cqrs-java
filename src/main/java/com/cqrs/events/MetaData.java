package com.cqrs.events;

import com.cqrs.commands.CommandMetaData;
import com.cqrs.event_store.EventSequence;

import java.time.LocalDateTime;

public final class MetaData {

    public final LocalDateTime dateCreated;
    public final String aggregateId;
    public final String aggregateClass;
    public final CommandMetaData commandMetadata;
    public final Integer version;
    public final EventSequence sequence;
    public final String eventId;

    public MetaData(
            LocalDateTime dateCreated,
            String aggregateId,
            String aggregateClass,
            CommandMetaData commandMetadata) {
        this.dateCreated = dateCreated;
        this.aggregateId = aggregateId;
        this.aggregateClass = aggregateClass;
        this.commandMetadata = commandMetadata;
        version = null;
        sequence = null;
        eventId = null;
    }

    public MetaData(
            LocalDateTime dateCreated,
            String aggregateId,
            String aggregateClass,
            CommandMetaData commandMetadata,
            Integer version,
            EventSequence sequence,
            String eventId) {
        this.dateCreated = dateCreated;
        this.aggregateId = aggregateId;
        this.aggregateClass = aggregateClass;
        this.commandMetadata = commandMetadata;
        this.version = version;
        this.sequence = sequence;
        this.eventId = eventId;
    }

    public MetaData(
            LocalDateTime dateCreated,
            String aggregateId,
            String aggregateClass) {
        this.dateCreated = dateCreated;
        this.aggregateId = aggregateId;
        this.aggregateClass = aggregateClass;
        this.commandMetadata = null;
        version = null;
        sequence = null;
        eventId = null;
    }

    public MetaData withEventId(String eventId) {
        return new MetaData(
                dateCreated,
                aggregateId,
                aggregateClass,
                commandMetadata,
                version,
                sequence,
                eventId
        );
    }

    public MetaData withSequence(EventSequence sequence) throws CloneNotSupportedException {
        return new MetaData(
                dateCreated,
                aggregateId,
                aggregateClass,
                commandMetadata,
                version,
                sequence,
                eventId
        );
    }

    public MetaData withVersion(Integer version) {
        return new MetaData(
                dateCreated,
                aggregateId,
                aggregateClass,
                commandMetadata,
                version,
                sequence,
                eventId
        );
    }
}
