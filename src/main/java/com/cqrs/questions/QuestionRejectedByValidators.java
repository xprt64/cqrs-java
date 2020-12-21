package com.cqrs.questions;

import com.cqrs.exceptions_base.MessageRejectedByValidators;

import java.util.List;

public class QuestionRejectedByValidators extends MessageRejectedByValidators {
    public QuestionRejectedByValidators(List<Throwable> errors) {
        super(errors);
    }
}
