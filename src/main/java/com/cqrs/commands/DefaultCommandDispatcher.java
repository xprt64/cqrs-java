package com.cqrs.commands;

import com.cqrs.aggregates.AggregateDescriptor;
import com.cqrs.aggregates.AggregateException;
import com.cqrs.aggregates.AggregateExecutionException;
import com.cqrs.aggregates.AggregateRepository;
import com.cqrs.base.Aggregate;
import com.cqrs.base.Command;
import com.cqrs.base.Event;
import com.cqrs.commands.exceptions.CommandExecutionFailed;
import com.cqrs.commands.exceptions.TooManyCommandExecutionRetries;
import com.cqrs.events.EventWithMetaData;
import com.cqrs.events.MetaData;
import com.cqrs.events.MetadataFactory;
import com.cqrs.util.Guid;
import java.util.List;
import java.util.stream.Collectors;

public class DefaultCommandDispatcher implements CommandDispatcher
{
    private final CommandSubscriber commandSubscriber;
    private final CommandApplier commandApplier;
    private final AggregateRepository aggregateRepository;
    private final ConcurrentProofFunctionCaller<List<EventWithMetaData>> concurrentProofFunctionCaller;
    private final MetadataFactory eventMetadataFactory;
    private final MetadataWrapper commandMetadataFactory;
    private final SideEffectsDispatcher sideEffectsDispatcher;

    public DefaultCommandDispatcher(
        CommandSubscriber commandSubscriber,
        CommandApplier commandApplier,
        AggregateRepository aggregateRepository,
        ConcurrentProofFunctionCaller<List<EventWithMetaData>> concurrentProofFunctionCaller,
        MetadataFactory eventMetadataFactory,
        MetadataWrapper metadataWrapper,
        SideEffectsDispatcher sideEffectsDispatcher
    )
    {
        this.commandSubscriber = commandSubscriber;
        this.commandApplier = commandApplier;
        this.aggregateRepository = aggregateRepository;
        this.concurrentProofFunctionCaller = concurrentProofFunctionCaller;
        this.eventMetadataFactory = eventMetadataFactory;
        this.commandMetadataFactory = metadataWrapper;
        this.sideEffectsDispatcher = sideEffectsDispatcher;
    }

    public DefaultCommandDispatcher(
        CommandSubscriber commandSubscriber,
        CommandApplier commandApplier,
        AggregateRepository aggregateRepository,
        SideEffectsDispatcher sideEffectsDispatcher
    )
    {
        this.commandSubscriber = commandSubscriber;
        this.commandApplier = commandApplier;
        this.aggregateRepository = aggregateRepository;
        this.concurrentProofFunctionCaller = new ConcurrentProofFunctionCaller<>();
        this.eventMetadataFactory = new MetadataFactory() {
        };
        this.commandMetadataFactory = new MetadataWrapper();
        this.sideEffectsDispatcher = sideEffectsDispatcher;
    }

    @Override
    public void dispatchCommand(Command command, CommandMetaData metadata)
        throws TooManyCommandExecutionRetries, CommandExecutionFailed, AggregateExecutionException, AggregateException, CommandHandlerNotFound
    {
        List<EventWithMetaData> sideEffects = dispatchCommandAndSaveAggregate(
            commandMetadataFactory.wrapCommandWithMetadata(command, metadata)
        );
        sideEffectsDispatcher.dispatchSideEffects(sideEffects);
    }

    private List<EventWithMetaData> tryDispatchCommandAndSaveAggregate(CommandWithMetadata command)
        throws AggregateException, AggregateExecutionException, CommandHandlerNotFound
    {
        CommandHandlerAndAggregate handlerAndAggregate = loadCommandHandlerAndAggregate(command);
        List<EventWithMetaData> dispatchResult = applyCommandAndReturnSideEffects(command, handlerAndAggregate);
        return aggregateRepository.saveAggregate(command.getAggregateId(), handlerAndAggregate.aggregate, dispatchResult);
    }

    private List<EventWithMetaData> dispatchCommandAndSaveAggregate(CommandWithMetadata command)
        throws TooManyCommandExecutionRetries, CommandExecutionFailed, AggregateExecutionException, AggregateException, CommandHandlerNotFound
    {
        try {
            return concurrentProofFunctionCaller
                .executeFunction(() -> tryDispatchCommandAndSaveAggregate(command), null);

        } catch (CommandExecutionFailed e) {
            throw new CommandExecutionFailed("Executing command " + command.getClass().getCanonicalName() + " failed: " + e.getMessage(), e);
        }
    }

    private CommandHandlerAndAggregate loadCommandHandlerAndAggregate(CommandWithMetadata command)
        throws AggregateException, AggregateExecutionException, CommandHandlerNotFound
    {

        CommandHandlerDescriptor handler = commandSubscriber.getAggregateForCommand(command.command.getClass());
        Aggregate aggregate = aggregateRepository.loadAggregate(
            new AggregateDescriptor(command.getAggregateId(), handler.aggregateClass)
        );
        return new CommandHandlerAndAggregate(handler, aggregate);
    }

    private EventWithMetaData decorateEventWithMetaData(Event event, MetaData metaData)
    {
        return new EventWithMetaData(event, metaData.withEventId(Guid.generate()));
    }

    private List<EventWithMetaData> applyCommandAndReturnSideEffects(
        CommandWithMetadata command,
        CommandHandlerAndAggregate handlerAndAggregate
    )
        throws AggregateExecutionException
    {
        Aggregate aggregate = handlerAndAggregate.aggregate;
        CommandHandlerDescriptor handler = handlerAndAggregate.commandHandler;
        MetaData metaData = eventMetadataFactory.factoryEventMetadata(command, aggregate);
        List<Event> eventList = commandApplier.applyCommand(aggregate, command.command, handler.methodName);
        return eventList.stream()
                        .map(event -> decorateEventWithMetaData(event, metaData))
                        .collect(Collectors.toList());
    }
}
