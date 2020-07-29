package com.cqrs.questions;

import com.cqrs.annotations.MessageHandler;
import com.cqrs.infrastructure.AbstractFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DefaultQuestionPublisher implements QuestionPublisher {
    private final SubscriberResolver subscriberResolver;
    private final AbstractFactory abstractFactory;
    private final ErrorReporter errorReporter;
    private final ClassLoader classLoader;

    public DefaultQuestionPublisher(
            SubscriberResolver subscriberResolver,
            AbstractFactory abstractFactory,
            ErrorReporter errorReporter,
            ClassLoader classLoader
    ) {
        this.subscriberResolver = subscriberResolver;
        this.abstractFactory = abstractFactory;
        this.errorReporter = errorReporter;
        this.classLoader = classLoader != null ? classLoader : getClass().getClassLoader();
    }

    public DefaultQuestionPublisher(
            SubscriberResolver subscriberResolver,
            AbstractFactory abstractFactory,
            ErrorReporter errorReporter
    ) {
        this(subscriberResolver, abstractFactory, errorReporter, DefaultQuestionPublisher.class.getClassLoader());
    }

    public DefaultQuestionPublisher(
            SubscriberResolver subscriberResolver,
            AbstractFactory abstractFactory
    ) {
        this(subscriberResolver, abstractFactory, null);
    }

    @Override
    public void publishAnsweredQuestion(Object answeredQuestion) {
        subscriberResolver.findSubscribers(answeredQuestion).forEach(handler -> callHandler(handler, answeredQuestion));
    }

    private void callHandler(MessageHandler handler, Object question) {
        Object listener = null;
        try {
            Class<?> clazz = loadClass(handler.handlerClass);
            listener = abstractFactory.factory(clazz);
            Method method = clazz.getDeclaredMethod(handler.methodName, question.getClass());
            method.setAccessible(true);
            method.invoke(listener, question);
        } catch (InvocationTargetException e) {
            reportError(listener, handler, e.getCause());
        } catch (Throwable e) {
            reportError(listener, handler, e);
        }
    }

    private void reportError(Object listener, MessageHandler handler, Throwable e) {
        if (null == errorReporter) {
            return;
        }
        errorReporter.reportQuestionDispatchError(
                listener,
                handler.handlerClass,
                handler.methodName,
                handler,
                e
        );
    }

    private Class<?> loadClass(String canonicalName) throws ClassNotFoundException {
        return Class.forName(canonicalName, true, classLoader);
    }
}
