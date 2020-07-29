package com.cqrs.questions;

import com.cqrs.infrastructure.AbstractFactory;
import com.cqrs.annotations.MessageHandler;
import com.cqrs.questions.exceptions.HandlerException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

public class DefaultAsker implements Asker {

    private final AbstractFactory abstractFactory;
    private final AnswererResolver answererResolver;
    private final SubscriberResolver subscriberResolver;
    private final ClassLoader classLoader;

    public DefaultAsker(
        AbstractFactory abstractFactory,
        AnswererResolver answererResolver,
        SubscriberResolver subscriberResolver,
        ClassLoader classLoader
    ) {
        this.abstractFactory = abstractFactory;
        this.answererResolver = answererResolver;
        this.subscriberResolver = subscriberResolver;
        this.classLoader = classLoader;
    }

    public DefaultAsker(
        AbstractFactory abstractFactory,
        AnswererResolver answererResolver,
        SubscriberResolver subscriberResolver
    ) {
        this(abstractFactory, answererResolver, subscriberResolver, DefaultAsker.class.getClassLoader());
    }

    @Override
    public <Q> Q askAndReturn(Q question) throws HandlerException {
        MessageHandler answererDescriptor = answererResolver.findAnswerer(question);
        if (null == answererDescriptor) {
            throw new HandlerException("No answerer for " + question.getClass().getCanonicalName());
        }
        try {
            Class<?> clazz = loadClass(answererDescriptor.handlerClass);
            Object listener = factoryObject(clazz);
            Method method = clazz.getDeclaredMethod(answererDescriptor.methodName, question.getClass());
            method.setAccessible(true);
            return (Q) method.invoke(listener, question);
        } catch (InvocationTargetException e) {
            throw new HandlerException(e.getMessage(), answererDescriptor, e.getCause());
        } catch (Throwable e) {
            throw new HandlerException(e.getMessage(), answererDescriptor, e);
        }
    }

    private Class<?> loadClass(String canonicalName) throws ClassNotFoundException {
        return Class.forName(canonicalName, true, classLoader);
    }

    private Object factoryObject(Class<?> clazz) {
        return abstractFactory.factory(clazz);
    }

    @Override
    public <Q> void askAndNotifyAsker(Q question, Object asker) throws HandlerException {
        Q answeredQuestion = askAndReturn(question);
        MessageHandler subscriberDescriptor = findSubscriberMethod(question, asker);
        try {
            Class<?> clazz = Class.forName(subscriberDescriptor.handlerClass);
            Method method = clazz.getDeclaredMethod(subscriberDescriptor.methodName, question.getClass());
            method.setAccessible(true);
            method.invoke(asker, answeredQuestion);
        } catch (InvocationTargetException e) {
            throw new HandlerException(e.getMessage(), subscriberDescriptor, e.getCause());
        } catch (Throwable e) {
            throw new HandlerException(e.getMessage(), subscriberDescriptor, e);
        }
    }

    private MessageHandler findSubscriberMethod(Object question, Object subscriber) throws HandlerException {
        Optional<MessageHandler> first = subscriberResolver.findSubscribers(question)
            .stream()
            .filter(handler -> handler.handlerClass.equals(subscriber.getClass().getCanonicalName()))
            .findFirst();
        if (!first.isPresent()) {
            throw new HandlerException(
                "No subscriber method found in " + subscriber.getClass().getCanonicalName() + " for question " + question.getClass().getCanonicalName(),
                null,
                new Exception());
        }
        return first.get();
    }
}
