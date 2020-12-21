package com.cqrs.questions;

import com.cqrs.annotations.QuestionValidatorProcessor;
import com.cqrs.annotations.HandlersMapFromFile;
import com.cqrs.util.ResourceReader;

public class AnnotatedQuestionValidatorsMap extends HandlersMapFromFile {

    public AnnotatedQuestionValidatorsMap(ResourceReader resourceReader) {
        super(resourceReader, QuestionValidatorProcessor.QUESTION_VALIDATORS_DIRECTORY);
    }
}
