package com.cqrs.annotations.processors.QuestionValidators.error1;

import com.cqrs.annotations.QuestionValidator;
import com.cqrs.annotations.QuestionValidatorProcessor;
import com.cqrs.base.Question;
import com.google.testing.compile.JavaFileObjects;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

class QuestionValidatorsProcessorTest {

    @Test
    void process() throws MalformedURLException {
        assertAbout(javaSource())
            .that(JavaFileObjects.forResource(__FILE__()))
            .processedWith(new QuestionValidatorProcessor())
            .failsToCompile()
            .withErrorContaining("or throw")
        ;
    }

    private URL __FILE__() throws MalformedURLException {
        File source = new File("src/test/java/" + this.getClass().getPackage().getName().replace('.', '/') + "/" + new Throwable().getStackTrace()[0].getFileName());
        return source.toURI().toURL();
    }

    class MyQuestionValidator {

        @QuestionValidator
        public void validate1(MyQuestion1 Question) {

        }
    }

    class MyQuestion1 implements Question{
    }
}
