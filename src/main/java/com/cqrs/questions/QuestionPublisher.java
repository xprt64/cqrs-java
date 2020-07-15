package com.cqrs.questions;

public interface QuestionPublisher {
    void publishAnsweredQuestion(Object answeredQuestion);
}
