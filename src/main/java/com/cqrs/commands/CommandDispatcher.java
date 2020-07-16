package com.cqrs.commands;

import com.cqrs.aggregates.AggregateTypeException;
import com.cqrs.aggregates.AggregateExecutionException;
import com.cqrs.base.Command;
import com.cqrs.commands.exceptions.TooManyCommandExecutionRetries;
import com.cqrs.event_store.exceptions.StorageException;

public interface CommandDispatcher {
	void dispatchCommand(Command command, CommandMetaData metadata)
		throws TooManyCommandExecutionRetries, AggregateExecutionException, AggregateTypeException, CommandHandlerNotFound, CommandRejectedByValidators, StorageException;
	default void dispatchCommand(Command command)
		throws TooManyCommandExecutionRetries, AggregateExecutionException, AggregateTypeException, CommandHandlerNotFound, CommandRejectedByValidators, StorageException {
		dispatchCommand(command, null);
	}
}
