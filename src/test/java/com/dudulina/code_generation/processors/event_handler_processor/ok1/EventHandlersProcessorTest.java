package com.dudulina.code_generation.processors.event_handler_processor.ok1;

import com.dudulina.base.Event;
import com.dudulina.code_generation.annotations.CommandHandler;
import com.dudulina.code_generation.annotations.EventHandler;
import com.dudulina.code_generation.processors.EventHandlersProcessor;
import com.dudulina.events.MetaData;
import com.google.testing.compile.JavaFileObjects;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static javax.tools.StandardLocation.SOURCE_OUTPUT;

class EventHandlersProcessorTest {

    @Test
    void process() throws MalformedURLException {
        assertAbout(javaSource())
            .that(JavaFileObjects.forResource(__FILE__()))
            .processedWith(new EventHandlersProcessor())
            .compilesWithoutError()
            .and()
            .generatesFileNamed(
                SOURCE_OUTPUT,
                EventHandlersProcessor.packageName,
                EventHandlersProcessor.builderClassName + ".java"
            )
        ;
    }

    private URL __FILE__() throws MalformedURLException {
        File source = new File("src/test/java/" + this.getClass().getPackage().getName().replace('.', '/') + "/" + new Throwable().getStackTrace()[0].getFileName());
        return source.toURI().toURL();
    }
}

class MyEventListener {

    @EventHandler
    public void handleEvent1(MyEvent1 event) {

    }

    @CommandHandler
    public void handleComm2(MyEvent1 event, MetaData meta) {

    }
}

class MyEvent1 implements Event {

}
