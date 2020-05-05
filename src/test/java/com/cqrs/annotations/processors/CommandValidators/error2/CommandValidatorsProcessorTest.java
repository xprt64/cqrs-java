package com.cqrs.annotations.processors.CommandValidators.error2;

import com.cqrs.annotations.CommandValidator;
import com.cqrs.annotations.CommandValidatorProcessor;
import com.cqrs.base.Command;
import com.google.testing.compile.JavaFileObjects;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

class CommandValidatorsProcessorTest {

    @Test
    void process() throws MalformedURLException {
        assertAbout(javaSource())
            .that(JavaFileObjects.forResource(__FILE__()))
            .processedWith(new CommandValidatorProcessor())
            .failsToCompile()
            .withErrorContaining("returned (if not a template) must be a Throwable")
        ;
    }

    private URL __FILE__() throws MalformedURLException {
        File source = new File("src/test/java/" + this.getClass().getPackage().getName().replace('.', '/') + "/" + new Throwable().getStackTrace()[0].getFileName());
        return source.toURI().toURL();
    }

    class MyCommandValidator {

        @CommandValidator
        public SomeClass validate1(MyCommand1 command) {
            return null;
        }
    }

    class MyCommand1 implements Command{

        @Override
        public String getAggregateId() {
            return null;
        }
    }

    class SomeClass{

    }
}
