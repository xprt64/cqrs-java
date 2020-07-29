package com.cqrs.questions;

import com.cqrs.annotations.HandlersMapFromFile;
import com.cqrs.annotations.QuestionAnswerersProcessor;
import com.cqrs.util.ResourceReader;

public class AnnotatedQuestionAnswerersMap extends HandlersMapFromFile {

    public AnnotatedQuestionAnswerersMap(ResourceReader resourceReader) {
        super(resourceReader, QuestionAnswerersProcessor.QUESTION_ANSWERERS_DIRECTORY);
    }
}
