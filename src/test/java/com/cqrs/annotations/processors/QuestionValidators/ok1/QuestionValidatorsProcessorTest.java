package com.cqrs.annotations.processors.QuestionValidators.ok1;

import com.cqrs.annotations.QuestionValidator;
import com.cqrs.annotations.QuestionValidatorProcessor;
import com.cqrs.base.Question;
import com.google.testing.compile.JavaFileObjects;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static javax.tools.StandardLocation.SOURCE_OUTPUT;

class QuestionValidatorsProcessorTest {

    @Test
    void process() throws MalformedURLException {
        assertAbout(javaSource())
            .that(JavaFileObjects.forResource(__FILE__()))
            .processedWith(new QuestionValidatorProcessor())
            .compilesWithoutError()
            .and()
            .generatesFileNamed(
                SOURCE_OUTPUT,
                QuestionValidatorProcessor.QUESTION_VALIDATORS_DIRECTORY,
                MyQuestionValidator.class.getCanonicalName()
            )
            .withStringContents(
                Charset.defaultCharset(),
                "com.cqrs.annotations.processors.QuestionValidators.ok1.QuestionValidatorsProcessorTest.MyQuestion1,validate1,0\n" +
                    "com.cqrs.annotations.processors.QuestionValidators.ok1.QuestionValidatorsProcessorTest.MyQuestion2,validate2,0\n" +
                    "com.cqrs.annotations.processors.QuestionValidators.ok1.QuestionValidatorsProcessorTest.MyQuestion3,validate3,0\n" +
                    "com.cqrs.annotations.processors.QuestionValidators.ok1.QuestionValidatorsProcessorTest.MyQuestion4,validate4,0"
            )
        ;
    }

    private URL __FILE__() throws MalformedURLException {
        File source = new File("src/test/java/" + this.getClass().getPackage().getName().replace('.', '/') + "/" + new Throwable().getStackTrace()[0].getFileName());
        return source.toURI().toURL();
    }

    class MyQuestionValidator {

        @QuestionValidator
        public List<Throwable> validate1(MyQuestion1 Question) {
            return new ArrayList<>();
        }

        @QuestionValidator
        public void validate2(MyQuestion2 Question) throws Exception {

        }

        @QuestionValidator
        public Throwable validate3(MyQuestion3 Question) {
            return new Exception("");
        }

        @QuestionValidator
        public Throwable validate4(MyQuestion4 Question) {
            return new Exception("");
        }

    }

    class MyQuestion1 implements Question {
    }

    class MyQuestion2 implements Question {
    }

    class MyQuestion3 implements Question {
    }

    class MyQuestion4 extends MyAbstractQuestion {
    }

    abstract class MyAbstractQuestion implements Question {

    }
}
