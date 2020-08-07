package com.cqrs.commands;

import com.cqrs.aggregates.AggregateTypeException;
import com.cqrs.aggregates.AggregateExecutionException;
import com.cqrs.base.Command;
import com.cqrs.commands.exceptions.TooManyCommandExecutionRetries;
import com.cqrs.event_store.exceptions.StorageException;
import com.cqrs.events.EventWithMetaData;

import java.util.List;

public interface CommandDispatcher {
	List<EventWithMetaData> dispatchCommand(Command command, CommandMetaData metadata)
		throws TooManyCommandExecutionRetries, AggregateExecutionException, AggregateTypeException, CommandHandlerNotFound, CommandRejectedByValidators, StorageException;
	default List<EventWithMetaData> dispatchCommand(Command command)
		throws TooManyCommandExecutionRetries, AggregateExecutionException, AggregateTypeException, CommandHandlerNotFound, CommandRejectedByValidators, StorageException {
		return dispatchCommand(command, null);
	}
}
