package com.cqrs.commands;

import com.cqrs.aggregates.AggregateException;
import com.cqrs.aggregates.AggregateExecutionException;
import com.cqrs.base.Command;
import com.cqrs.commands.exceptions.TooManyCommandExecutionRetries;
import java.util.List;

public class CommandDispatcherWithValidator implements CommandDispatcher {
    final private CommandDispatcher commandDispatcher;
    final private CommandValidator commandValidator;

    public CommandDispatcherWithValidator(CommandDispatcher commandDispatcher, CommandValidator commandValidator)
    {
        this.commandDispatcher = commandDispatcher;
        this.commandValidator = commandValidator;
    }

    @Override
    public void dispatchCommand(Command command, CommandMetaData metadata)
        throws TooManyCommandExecutionRetries, CommandValidationFailed, AggregateExecutionException, AggregateException, CommandHandlerNotFound
    {
        List<Throwable> errors = commandValidator.validateCommand(command);
        if(!errors.isEmpty()){
            throw new CommandValidationFailed(errors);
        }
        commandDispatcher.dispatchCommand(command, metadata);
    }
}
