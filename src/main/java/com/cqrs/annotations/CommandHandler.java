package com.cqrs.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method of an {@link com.cqrs.base.Aggregate} as being a handler for a command.
 * A command handler gets the command (a class that implements {@link com.cqrs.base.Command}) as a parameter and emits zero or more events.
 * The second parameter is optional and is an instance of {@link com.cqrs.commands.CommandMetaData}.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface CommandHandler
{
}
