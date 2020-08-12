package com.cqrs.annotations;

import com.cqrs.read_model.readmodel_reflector.FromHandlersFileReadModelsReflector;
import com.cqrs.util.ResourceReader;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HandlersMapFromFileTest{
    @Test
    void getEventClassesFromReadModel() {

        HandlersMapFromFile sut = new HandlersMapFromFile(
            new ResourceReader(this.getClass()),
            "HandlersMapFromFileTest"
        ){

        };

        HashMap<String, List<MessageHandler>> actualMap = sut.getMap();

        List<MessageHandler> actualHandlers = actualMap.get("event1");

        assertEquals(4, actualHandlers.size());
        assertEquals("handler_B", actualHandlers.get(0).methodName);
        assertEquals("handler_D", actualHandlers.get(1).methodName);
        assertEquals("handler_A", actualHandlers.get(2).methodName);
        assertEquals("handler_C", actualHandlers.get(3).methodName);
    }
}