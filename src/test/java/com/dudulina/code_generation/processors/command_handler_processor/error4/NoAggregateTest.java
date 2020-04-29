package com.dudulina.code_generation.processors.command_handler_processor.error4;

import com.dudulina.base.Command;
import com.dudulina.code_generation.annotations.CommandHandler;
import com.dudulina.code_generation.processors.CommandHandlersProcessor;
import com.google.testing.compile.JavaFileObjects;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

class NoAggregateTest {

    @Test
    void process() throws MalformedURLException {
        assertAbout(javaSource())
                .that(JavaFileObjects.forResource(__FILE__()))
                .processedWith(new CommandHandlersProcessor())
                .failsToCompile()
                .withErrorContaining("only Aggregates can handle commands")
        ;
    }

    private URL __FILE__() throws MalformedURLException {
        File source = new File("src/test/java/" + this.getClass().getPackage().getName().replace('.', '/') + "/" + new Throwable().getStackTrace()[0].getFileName());
        return source.toURI().toURL();
    }
}

class MyAggregate {
    @CommandHandler
    public void handleComm(MyCommand command, String meta) {

    }
}

class MyCommand implements Command {

    @Override
    public String getAggregateId() {
        return null;
    }
}