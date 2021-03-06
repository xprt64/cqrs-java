package com.cqrs.annotations.processors.QuestionValidators.error2;

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
            .withErrorContaining("returned (if not a template) must be a Throwable")
        ;
    }

    private URL __FILE__() throws MalformedURLException {
        File source = new File("src/test/java/" + this.getClass().getPackage().getName().replace('.', '/') + "/" + new Throwable().getStackTrace()[0].getFileName());
        return source.toURI().toURL();
    }

    class MyQuestionValidator {

        @QuestionValidator
        public SomeClass validate1(MyQuestion1 Question) {
            return null;
        }
    }

    class MyQuestion1 implements Question{
    }

    class SomeClass{

    }
}
