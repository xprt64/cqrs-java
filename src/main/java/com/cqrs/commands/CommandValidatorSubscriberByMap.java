package com.cqrs.commands;

import com.cqrs.annotations.HandlersMap;
import com.cqrs.annotations.HandlersMap.Handler;
import com.cqrs.base.Command;
import com.cqrs.infrastructure.AbstractFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CommandValidatorSubscriberByMap implements CommandValidatorSubscriber {

    private final HandlersMap map;
    private final AbstractFactory validatorFactory;

    public CommandValidatorSubscriberByMap(
        AbstractFactory validatorFactory,
        HandlersMap map
    ) {
        this.map = map;
        this.validatorFactory = validatorFactory;
    }

    @Override
    public List<Function<CommandWithMetadata, List<Throwable>>> getValidatorsForCommand(Command command) {
        final List<Handler> validators =
            map.getMap(command.getClass()).getOrDefault(command.getClass().getCanonicalName(), new LinkedList<>());
        return validators.stream()
            .map(listenerDescriptor -> (Function<CommandWithMetadata, List<Throwable>>) (commandWithMetadata) -> {
                Object listener = null;
                try {
                    Class<?> clazz;
                    try {
                        clazz = Class.forName(listenerDescriptor.handlerClass);
                    } catch (ClassNotFoundException classNotFoundException) {
                        return Collections.singletonList(new Exception(
                            "Validator not found:" + listenerDescriptor.handlerClass, classNotFoundException));
                    }
                    listener = factoryObject(clazz);
                    Object returnedObject;
                    try {
                        Method method = clazz.getDeclaredMethod(listenerDescriptor.methodName, command.getClass());
                        method.setAccessible(true);
                        returnedObject = method.invoke(listener, commandWithMetadata.command);
                    } catch (NoSuchMethodException e) {
                        Method method = clazz.getDeclaredMethod(
                            listenerDescriptor.methodName,
                            command.getClass(),
                            commandWithMetadata.metadata.getClass());
                        method.setAccessible(true);
                        returnedObject = method.invoke(
                            listener, commandWithMetadata.command, commandWithMetadata.metadata
                        );
                    }
                    if (null == returnedObject) {
                        return new ArrayList<>();
                    }
                    if (returnedObject instanceof Throwable) {
                        return Collections.singletonList((Throwable) returnedObject);
                    }
                    if (returnedObject instanceof List && ((List) returnedObject).size() == 0) {
                        return Collections.emptyList();
                    }
                    if (returnedObject instanceof List && ((List) returnedObject).get(0) instanceof Throwable) {
                        return (List<Throwable>) returnedObject;
                    }
                    return Collections.singletonList(new Exception(returnedObject.toString()));

                } catch (InvocationTargetException e) {
                    return Collections.singletonList(e.getTargetException());
                } catch (Throwable e) {
                    return Collections.singletonList(e);
                }
            })
            .collect(Collectors.toList());
    }

    private Object factoryObject(Class<?> clazz) {
        return validatorFactory.factory(clazz);
    }
}
