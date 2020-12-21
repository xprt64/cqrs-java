package com.cqrs.questions.QuestionValidatorSubscriberByMapTesting.ok2;

import com.cqrs.annotations.HandlersMap;
import com.cqrs.annotations.MessageHandler;
import com.cqrs.base.Question;
import com.cqrs.questions.QuestionValidatorSubscriberByMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

class QuestionValidatorSubscriberByMapTest {

    private QuestionValidatorSubscriberByMap sut;

    @BeforeEach
    void setUp() {
        sut = new QuestionValidatorSubscriberByMap(
            clazz -> {
                assertEquals(clazz.getCanonicalName(), MyValidator.class.getCanonicalName());
                return new MyValidator();
            },
            new HandlersMap() {
                @Override
                public HashMap<String, List<MessageHandler>> getMap() {
                    HashMap<String, List<MessageHandler>> result = new HashMap<>();
                    result.put(
                        MyQuestion1.class.getCanonicalName(),
                        Collections.singletonList(
                            new MessageHandler(MyValidator.class.getCanonicalName(), "validate1")
                        )
                    );
                    result.put(
                        MyQuestion2.class.getCanonicalName(),
                        Collections.singletonList(
                            new MessageHandler(MyValidator.class.getCanonicalName(), "validate2")
                        )
                    );
                    result.put(
                        MyQuestion3.class.getCanonicalName(),
                        Collections.singletonList(
                            new MessageHandler(MyValidator.class.getCanonicalName(), "validate3")
                        )
                    );
                    return result;
                }
            }
        );
    }

    @Test
    void getListenersForQuestionWhenReturnsException() {
        List<Function<Question, List<Throwable>>> result = sut.getValidatorsForQuestion(new MyQuestion1());
        assertEquals(1, result.size());
        List<Throwable> errors = result.get(0).apply(new MyQuestion1());
        assertEquals(0, errors.size());
    }

    @Test
    void getListenersForQuestionWhenThrowsException() {
        List<Function<Question, List<Throwable>>> result = sut.getValidatorsForQuestion(new MyQuestion2());
        assertEquals(1, result.size());
        List<Throwable> errors = result.get(0).apply(new MyQuestion2());
        assertEquals(0, errors.size());
    }

    @Test
    void getListenersForQuestionWhenReturnsListOfThroables() {
        List<Function<Question, List<Throwable>>> result = sut.getValidatorsForQuestion(new MyQuestion3());
        assertEquals(1, result.size());
        List<Throwable> errors = result.get(0).apply(new MyQuestion3());
        errors.forEach(throwable -> throwable.printStackTrace());
        assertEquals(0, errors.size());
    }
}

class MyValidator {
    public Exception validate1(MyQuestion1 command) {
        return null;
    }

    public void validate2(MyQuestion2 command) throws Exception {
        if (false) {
            throw new Exception("some error 2");
        }
    }

    public List<Throwable> validate3(MyQuestion3 command) {
        return Collections.emptyList();
    }
}

class MyQuestion1 implements Question {

}

class MyQuestion2 implements Question {

}

class MyQuestion3 implements Question {

}
