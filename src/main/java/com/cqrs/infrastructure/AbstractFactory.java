package com.cqrs.infrastructure;

public interface AbstractFactory {
    Object factory(Class<?> clazz);
}
