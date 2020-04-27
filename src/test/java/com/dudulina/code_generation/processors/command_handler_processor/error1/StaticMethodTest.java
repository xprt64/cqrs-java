package com.dudulina.code_generation.processors.command_handler_processor.error1;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

import com.dudulina.aggregates.AggregateId;
import com.dudulina.base.Aggregate;
import com.dudulina.base.Command;
import com.dudulina.code_generation.annotations.CommandHandler;
import com.dudulina.code_generation.processors.CommandHandlersProcessor;
import com.google.testing.compile.JavaFileObjects;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import org.junit.jupiter.api.Test;

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

//    private String __FILE__(){
//        StackTraceElement x = new Throwable().getStackTrace()[0];
//    }
}

class MyAggregate extends Aggregate {

    @CommandHandler
    public static void handleComm(MyCommand command) {

    }

}

class MyCommand implements Command {

    @Override
    public AggregateId getAggregateId() {
        return null;
    }
}