package com.cqrs.commands;

import com.cqrs.annotations.CommandHandlersProcessor;
import com.cqrs.util.ResourceReader;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;

public class CommandHandlersMapFromFile implements CommandHandlersMap {
    private final String DIRECTORY_PATH;

    public CommandHandlersMapFromFile() {
        DIRECTORY_PATH = CommandHandlersProcessor.AGGREGATE_COMMAND_HANDLERS_DIRECTORY;
    }

    public CommandHandlersMapFromFile(String DIRECTORY_PATH) {
        this.DIRECTORY_PATH = DIRECTORY_PATH;
    }

    @Override
    public HashMap<String, String[]> getMap() {
        HashMap<String, String[]> handlerPerCommand = new HashMap<>();
        try {
            ResourceReader.getResourceFiles(DIRECTORY_PATH).forEach(aggregateName -> {
                try {
                    ResourceReader.forEachLineInResource(
                        Paths.get(DIRECTORY_PATH, aggregateName).toString(),
                        line -> {
                            //System.out.println(line);
                            String[] commandAndMethod = line.split(",", 2);
                            final String command = commandAndMethod[0];
                            final String method = commandAndMethod[1];
                            handlerPerCommand.put(command, new String[]{aggregateName, method});
                        }
                    );
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
//
//        handlerPerCommand.forEach((key, value) -> {
//            System.out.println(key);
//            System.out.println(value);
//        });

        return handlerPerCommand;
    }
}
