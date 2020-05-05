package com.cqrs.commands;

import com.cqrs.aggregates.AggregateException;
import com.cqrs.aggregates.AggregateExecutionException;
import com.cqrs.base.Command;
import com.cqrs.commands.exceptions.TooManyCommandExecutionRetries;

public interface CommandDispatcher {
	void dispatchCommand(Command command, CommandMetaData metadata)
		throws TooManyCommandExecutionRetries, AggregateExecutionException, AggregateException, CommandHandlerNotFound, CommandRejectedByValidators;
	default void dispatchCommand(Command command)
		throws TooManyCommandExecutionRetries, AggregateExecutionException, AggregateException, CommandHandlerNotFound, CommandRejectedByValidators {
		dispatchCommand(command, null);
	}
}
