package com.dudulina.command;

import com.dudulina.aggregates.AggregateException;
import com.dudulina.aggregates.AggregateExecutionException;
import com.dudulina.base.Command;
import com.dudulina.command.exceptions.TooManyCommandExecutionRetries;

public interface CommandDispatcher {
	public void dispatchCommand(Command command, CommandMetaData metadata)
		throws TooManyCommandExecutionRetries, CommandValidationFailed, AggregateExecutionException, AggregateException, CommandHandlerNotFound;
}
