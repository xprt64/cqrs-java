package com.cqrs.commands;

import com.cqrs.aggregates.AggregateDescriptor;
import com.cqrs.aggregates.AggregateExecutionException;
import com.cqrs.aggregates.AggregateRepository;
import com.cqrs.aggregates.AggregateTypeException;
import com.cqrs.annotations.MessageHandler;
import com.cqrs.base.Aggregate;
import com.cqrs.base.Command;
import com.cqrs.base.Event;
import com.cqrs.commands.exceptions.TooManyCommandExecutionRetries;
import com.cqrs.event_store.exceptions.StorageException;
import com.cqrs.events.EventWithMetaData;
import com.cqrs.events.MetaData;
import com.cqrs.events.MetadataFactory;
import com.cqrs.util.Guid;

import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.stream.Collectors;

public class DefaultCommandDispatcher implements CommandDispatcher {
    private final CommandSubscriber commandSubscriber;
    private final CommandApplier commandApplier;
    private final AggregateRepository aggregateRepository;
    private final MetadataFactory eventMetadataFactory;
    private final MetadataWrapper commandMetadataFactory;
    private final SideEffectsDispatcher sideEffectsDispatcher;
    public static int maximumSaveRetries = 50;

    public DefaultCommandDispatcher(
        CommandSubscriber commandSubscriber,
        CommandApplier commandApplier,
        AggregateRepository aggregateRepository,
        MetadataFactory eventMetadataFactory,
        MetadataWrapper metadataWrapper,
        SideEffectsDispatcher sideEffectsDispatcher
    ) {
        this.commandSubscriber = commandSubscriber;
        this.commandApplier = commandApplier;
        this.aggregateRepository = aggregateRepository;
        this.eventMetadataFactory = eventMetadataFactory;
        this.commandMetadataFactory = metadataWrapper;
        this.sideEffectsDispatcher = sideEffectsDispatcher;
    }

    public DefaultCommandDispatcher(
        CommandSubscriber commandSubscriber,
        CommandApplier commandApplier,
        AggregateRepository aggregateRepository,
        SideEffectsDispatcher sideEffectsDispatcher
    ) {
        this.commandSubscriber = commandSubscriber;
        this.commandApplier = commandApplier;
        this.aggregateRepository = aggregateRepository;
        this.eventMetadataFactory = new MetadataFactory() {
        };
        this.commandMetadataFactory = new MetadataWrapper();
        this.sideEffectsDispatcher = sideEffectsDispatcher;
    }

    @Override
    public List<EventWithMetaData> dispatchCommand(Command command, CommandMetaData metadata)
        throws TooManyCommandExecutionRetries, AggregateExecutionException, AggregateTypeException, CommandHandlerNotFound, StorageException
    {
        List<EventWithMetaData> emittedEvents = dispatchCommandAndSaveAggregate(
            commandMetadataFactory.wrapCommandWithMetadata(command, metadata)
        );
        sideEffectsDispatcher.dispatchSideEffects(emittedEvents);
        return emittedEvents;
    }

    private List<EventWithMetaData> dispatchCommandAndSaveAggregate(CommandWithMetadata command)
        throws TooManyCommandExecutionRetries, AggregateExecutionException, AggregateTypeException, CommandHandlerNotFound, StorageException
    {
        int retries = -1;
        do {
            try {
                CommandHandlerAndAggregate handlerAndAggregate = loadCommandHandlerAndAggregate(command);
                List<EventWithMetaData> emittedEvents = applyCommandAndReturnSideEffects(command, handlerAndAggregate);
                return aggregateRepository.saveAggregate(command.getAggregateId(), handlerAndAggregate.aggregate, emittedEvents);
            } catch (ConcurrentModificationException e) {
                retries++;
                System.out.println("retry # " + retries);
                if (retries >= maximumSaveRetries) {
                    throw new TooManyCommandExecutionRetries(
                        String.format("TooManyCommandExecutionRetries: %d (%s)", retries, e.getMessage())
                    );
                }
            }
        } while (true);
    }


    private CommandHandlerAndAggregate loadCommandHandlerAndAggregate(CommandWithMetadata command)
        throws AggregateTypeException, AggregateExecutionException, CommandHandlerNotFound, StorageException {
        MessageHandler handler = commandSubscriber.getAggregateForCommand(command.command.getClass());
        Aggregate aggregate = aggregateRepository.loadAggregate(
            new AggregateDescriptor(command.getAggregateId(), handler.handlerClass)
        );
        return new CommandHandlerAndAggregate(handler, aggregate);
    }

    private EventWithMetaData decorateEventWithMetaData(Event event, MetaData metaData) {
        return new EventWithMetaData(event, metaData.withEventId(Guid.generate()));
    }

    private List<EventWithMetaData> applyCommandAndReturnSideEffects(
        CommandWithMetadata command,
        CommandHandlerAndAggregate handlerAndAggregate
    )
        throws AggregateExecutionException {
        Aggregate aggregate = handlerAndAggregate.aggregate;
        MessageHandler  handler = handlerAndAggregate.commandHandler;
        MetaData metaData = eventMetadataFactory.factoryEventMetadata(command, aggregate);
        List<Event> eventList = commandApplier.applyCommand(aggregate, command.command, handler.methodName);
        return eventList.stream()
            .map(event -> decorateEventWithMetaData(event, metaData))
            .collect(Collectors.toList());
    }
}
