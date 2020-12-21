package com.cqrs.questions;

import com.cqrs.base.Question;
import com.cqrs.questions.exceptions.HandlerException;

import java.util.List;

public class AskerWithValidator implements Asker {
    private final Asker asker;
    private final QuestionValidator validator;

    public AskerWithValidator(
        Asker asker,
        QuestionValidator validator
    ) {
        this.asker = asker;
        this.validator = validator;
    }

    @Override
    public <Q extends Question> Q askAndReturn(Q question) throws HandlerException {
        validate(question);
        return asker.askAndReturn(question);
    }

    private <Q extends Question> void validate(Q question) {
        List<Throwable> errors = validator.validateQuestion(question);
        if (!errors.isEmpty()) {
            throw new QuestionRejectedByValidators(errors);
        }
    }

    @Override
    public <Q extends Question> void askAndNotifyAsker(Q question, Object asker) throws HandlerException {
        validate(question);
        this.asker.askAndNotifyAsker(question, asker);
    }
}
