package com.dudulina.command;

import com.dudulina.aggregates.AggregateException;
import com.dudulina.aggregates.AggregateExecutionException;
import com.dudulina.base.Command;
import com.dudulina.command.exceptions.TooManyCommandExecutionRetries;
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
