package com.cqrs.questions;

import com.cqrs.annotations.HandlersMapFromFile;
import com.cqrs.annotations.QuestionAnswerersProcessor;
import com.cqrs.annotations.QuestionSubscribersProcessor;
import com.cqrs.util.ResourceReader;

public class AnnotatedQuestionSubscribersMap extends HandlersMapFromFile {

    public AnnotatedQuestionSubscribersMap(ResourceReader resourceReader) {
        super(resourceReader, QuestionSubscribersProcessor.QUESTION_SUBSCRIBERS_DIRECTORY);
    }
}
