package com.cqrs.questions;

import com.cqrs.base.Question;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class QuestionValidatorBySubscriber implements QuestionValidator {

    private final QuestionValidatorSubscriber validatorSubscriber;

    public QuestionValidatorBySubscriber(QuestionValidatorSubscriber eventSubscriber) {
        this.validatorSubscriber = eventSubscriber;
    }

    @Override
    public List<Throwable> validateQuestion(Question question) {
        List<Function<Question, List<Throwable>>> listeners =
            validatorSubscriber.getValidatorsForQuestion(question);
        List<Throwable> errors = new ArrayList<>();
        listeners.forEach(listener -> errors.addAll(listener.apply(question)));
        return errors;
    }

}


