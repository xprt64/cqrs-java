package com.cqrs.annotations.processors.event_handler_processor.ok1;

import com.cqrs.base.Event;
import com.cqrs.annotations.CommandHandler;
import com.cqrs.annotations.EventHandler;
import com.cqrs.annotations.EventHandlersProcessor;
import com.cqrs.events.MetaData;
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
                EventHandlersProcessor.EVENT_HANDLERS_DIRECTORY,
                MyEventListener.class.getCanonicalName()
            )
            .withStringContents(
                Charset.defaultCharset(),
                "com.cqrs.annotations.processors.event_handler_processor.ok1.EventHandlersProcessorTest.MyEvent1,handleEvent1\n" +
                "com.cqrs.annotations.processors.event_handler_processor.ok1.EventHandlersProcessorTest.MyEvent2,handleEvent2"
            )
        ;
    }

    private URL __FILE__() throws MalformedURLException {
        File source = new File("src/test/java/" + this.getClass().getPackage().getName().replace('.', '/') + "/" + new Throwable().getStackTrace()[0].getFileName());
        return source.toURI().toURL();
    }

    class MyEventListener {

        @EventHandler
        public void handleEvent1(MyEvent1 event) {

        }

        @EventHandler
        public void handleEvent2(MyEvent2 event) {

        }

        @CommandHandler
        public void handleComm2(MyEvent1 event, MetaData meta) {

        }
    }

    class MyEvent1 implements Event {

    }

    class MyEvent2 implements Event {

    }
}
