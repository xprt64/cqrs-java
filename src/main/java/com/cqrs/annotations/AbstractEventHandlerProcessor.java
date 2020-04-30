package com.cqrs.annotations;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

abstract public class AbstractEventHandlerProcessor extends AbstractProcessor {
    public static final String packageName = "com.cqrs.annotations";

    protected boolean codeWriten = false;

    abstract protected HashMap<String, List<EventHandler>> getEventHandlers(
        Set<? extends Element> annotatedElements,
        TypeElement annotation
    ) throws Exception;

    protected static AnnotationMirror getAnnotationMirror(Element element, TypeElement annotation) {
        for (AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
            if (annotationMirror.getAnnotationType()
                .toString()
                .equals(annotation.getQualifiedName().toString())) {
                return annotationMirror;
            }
        }
        return null;
    }

    protected abstract String getAnnotationName();

    protected abstract String getGeneratedClassName();

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

                try {
                    TypeElement annotation = processingEnv.getElementUtils().getTypeElement(getAnnotationName());
                    Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, this + ", elements: " + annotatedElements.size());
                    writeCode(getEventHandlers(annotatedElements, annotation));
                } catch (Exception e) {
                    return false;
                }

        try {
            writeCode(new HashMap<>());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    protected void writeCode(HashMap<String, List<EventHandler>> handlers) throws IOException {
        if (codeWriten) {
            return;
        }
        codeWriten = true;

        JavaFileObject builderFile = processingEnv.getFiler()
            .createSourceFile(packageName + "." + getGeneratedClassName());
        try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {

            out.print("package ");
            out.print(packageName);
            out.println(";");
            out.println();

            out.println("import java.util.HashMap;");
            out.println();
            out.println(
                "public class " + getGeneratedClassName() + " implements com.cqrs.events.EventHandlersMap ");
            out.println(" {");
            out.println();

            out.println(" @Override");
            out.println(" public HashMap<String, String[][]> getMap() { return map;} ");
            out.println(" private static HashMap<String, String[][]> map = new HashMap<>(); ");
            out.println(" static { ");
            handlers.forEach((s, eventHandlers) -> {
                out.println("    map.put(\"" + s + "\", new String[][]{");
                eventHandlers.forEach(eventHandler -> {
                    out.println("         new String[]{");
                    out.println(
                        "             \"" + eventHandler.handlerClass + "\", \"" + eventHandler.methodName +
                        "\"");
                    out.println("        },");
                });
                out.println("    });");
            });
            out.println(" } ");
            out.println();

            out.println("}");
        }
    }

    protected boolean typeIsInstanceOfInterface(TypeElement type, String base) {
        return type.getInterfaces()
            .stream()
            .map(TypeMirror::toString)
            .collect(Collectors.toList())
            .contains(base);
    }

    protected void error(Error error) {
        if (error.annotationMirror != null) {
            processingEnv.getMessager()
                .printMessage(Diagnostic.Kind.ERROR, error.error, error.element, error.annotationMirror);
        } else {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, error.error, error.element);
        }
    }

    static class EventHandler {

        public final String handlerClass;
        public final String methodName;

        public EventHandler(String aggregateClass, String methodName) {
            this.handlerClass = aggregateClass;
            this.methodName = methodName;
        }
    }
}
