package com.cqrs.annotations.processors.command_handler_processor.error1;

import com.cqrs.annotations.CommandHandler;
import com.cqrs.annotations.CommandHandlersProcessor;
import com.cqrs.base.Aggregate;
import com.cqrs.base.Command;
import com.google.testing.compile.JavaFileObjects;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

class StaticMethodTest {

    @Test
    void process() throws MalformedURLException {
        assertAbout(javaSource())
                .that(JavaFileObjects.forResource(__FILE__()))
                .processedWith(new CommandHandlersProcessor())
                .failsToCompile()
                .withErrorContaining("static")
        ;
    }

    private URL __FILE__() throws MalformedURLException {
        File source = new File("src/test/java/" + this.getClass().getPackage().getName().replace('.', '/') + "/" + new Throwable().getStackTrace()[0].getFileName());
        return source.toURI().toURL();
    }
}

class MyAggregate extends Aggregate {

    @CommandHandler
    public static void handleComm(MyCommand command) {

    }

}

class MyCommand implements Command {

    @Override
    public String getAggregateId() {
        return null;
    }
}