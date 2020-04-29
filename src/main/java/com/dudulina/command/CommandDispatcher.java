package com.dudulina.command;

import com.dudulina.aggregates.AggregateException;
import com.dudulina.aggregates.AggregateExecutionException;
import com.dudulina.base.Command;
import com.dudulina.command.exceptions.TooManyCommandExecutionRetries;

public interface CommandDispatcher {
	void dispatchCommand(Command command, CommandMetaData metadata)
		throws TooManyCommandExecutionRetries, AggregateExecutionException, AggregateException, CommandHandlerNotFound, CommandValidationFailed;
	default void dispatchCommand(Command command)
		throws TooManyCommandExecutionRetries, AggregateExecutionException, AggregateException, CommandHandlerNotFound, CommandValidationFailed {
		dispatchCommand(command, null);
	}
}
