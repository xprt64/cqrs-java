package com.cqrs.commands;

import com.cqrs.aggregates.AggregateTypeException;
import com.cqrs.aggregates.AggregateExecutionException;
import com.cqrs.base.Command;
import com.cqrs.commands.exceptions.TooManyCommandExecutionRetries;
import com.cqrs.event_store.exceptions.StorageException;

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
        throws TooManyCommandExecutionRetries, CommandRejectedByValidators, AggregateExecutionException, AggregateTypeException, CommandHandlerNotFound, StorageException {
        List<Throwable> errors = commandValidator.validateCommand(new CommandWithMetadata(command, metadata));
        if(!errors.isEmpty()){
            throw new CommandRejectedByValidators(errors);
        }
        commandDispatcher.dispatchCommand(command, metadata);
    }
}
