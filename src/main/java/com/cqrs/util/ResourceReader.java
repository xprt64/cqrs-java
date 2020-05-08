package com.cqrs.util;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

public class ResourceReader {

    private static InputStream getResourceAsStream(String resource) {
        final InputStream in = getContextClassLoader().getResourceAsStream(resource);
        return in == null ? ResourceReader.class.getResourceAsStream(resource) : in;
    }

    private static ClassLoader getContextClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    private static String concatenatePath(String... parts) {
        return String.join("/", parts);
    }

    public void forEachLineInDirectory(
        Class<?> clazz,
        String dirPath,
        LineConsumer consumer,
        ErrorReporter errorReporter
    ) {
        try {
            listResourceFilesInDir(clazz, dirPath).forEach(file -> {
                String resourcePath = concatenatePath(dirPath, file);
                new BufferedReader(new InputStreamReader(getResourceAsStream(resourcePath)))
                    .lines().forEach(line -> consumer.consumeLineInFile(file, line));

            });
        } catch (IOException e) {
            if (errorReporter != null) {
                errorReporter.reportError(e.getMessage(), e);
            }
        }
    }

    public void forEachLineInDirectory(Class<?> clazz, String dirPath, LineConsumer consumer) {
        forEachLineInDirectory(clazz, dirPath, consumer, null);
    }

    private Stream<String> listResourceFilesInDir(Class<?> clazz, String directoryPath) throws IOException {
        try {
            return Arrays.stream(getResourceListing(clazz, directoryPath)).sorted();
        } catch (UnsupportedOperationException | URISyntaxException e) {
            throw new IOException(e.getMessage(), e);
        }
    }

    String[] getResourceListing(Class<?> clazz, String path) throws URISyntaxException, IOException {
        if (!path.endsWith("/")) {
            path = path + "/";
        }

        URL dirURL = clazz.getClassLoader().getResource(path);

        if (dirURL != null && dirURL.getProtocol().equals("file")) {
            /* A file path: easy enough */
            return new File(dirURL.toURI()).list();
        }

        if (dirURL == null) {
            /*
             * In case of a jar file, we can't actually find a directory.
             * Have to assume the same jar as clazz.
             */
            String me = clazz.getName().replace(".", "/") + ".class";
            dirURL = clazz.getClassLoader().getResource(me);
        }

        if (dirURL.getProtocol().equals("jar")) {
            /* A JAR path */
            String jarPath =
                dirURL.getPath().substring(5, dirURL.getPath().indexOf("!")); //strip out only the JAR file
            JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
            Enumeration<JarEntry> entries = jar.entries(); //gives ALL entries in jar
            Set<String> result = new HashSet<>(); //avoid duplicates in case it is a subdirectory
            while (entries.hasMoreElements()) {
                String name = entries.nextElement().getName();
                if (name.startsWith(path)) { //filter according to the path
                    String entry = name.substring(path.length());
                    int checkSubdir = entry.indexOf("/");
                    if (checkSubdir >= 0) {
                        // if it is a subdirectory, we just return the directory name
                        entry = entry.substring(0, checkSubdir);
                    }
                    result.add(entry);
                }
            }
            return result.toArray(new String[0]);
        }

        throw new UnsupportedOperationException("Cannot list files for URL " + dirURL);
    }

    public interface LineConsumer{
        void consumeLineInFile(String fileName, String line);
    }

     public interface ErrorReporter{
        void reportError(String errorMessage, IOException cause);
     }
}
