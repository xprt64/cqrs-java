package com.cqrs.read_model.readmodel_reflector;

import com.cqrs.read_model.ReadModelReflector;
import com.cqrs.util.ResourceReader;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class FromHandlersFileReadModelsReflector implements ReadModelReflector {


    public final String handlersDirectory;
    private final ResourceReader resourceReader;

    public FromHandlersFileReadModelsReflector(ResourceReader resourceReader, String handlersDirectory) {

        this.resourceReader = resourceReader;
        this.handlersDirectory = handlersDirectory;
    }


    @Override
    public List<String> getEventClassesFromReadModel(String readModelClass) {
        ArrayList<String> events = new ArrayList<>();
        resourceReader.readLinesFromFile(
            concatenatePath(handlersDirectory, readModelClass),
            line -> {
                String[] messageClassAndAndMethod = line.split(",", 2);
                final String messageClass = messageClassAndAndMethod[0];
                events.add(messageClass);
            }
        );
        return events;
    }

    private static String concatenatePath(String... parts) {
        return String.join("/", parts);
    }

    @Override
    public String getEventHandlerMethodNameForEvent(String readModelClass, String eventClass) {
        AtomicReference<String> result = new AtomicReference<>();
        resourceReader.readLinesFromFile(
            concatenatePath(handlersDirectory, readModelClass),
            line -> {
                String[] messageClassAndAndMethod = line.split(",", 2);
                final String messageClass = messageClassAndAndMethod[0];
                final String methodName = messageClassAndAndMethod[1];
                if(eventClass.equals(messageClass)){
                    result.set(methodName);
                }
            }
        );
        return result.get();
    }
}
