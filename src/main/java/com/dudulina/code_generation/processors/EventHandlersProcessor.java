package com.dudulina.code_generation.processors;

import com.dudulina.base.Event;
import com.dudulina.events.MetaData;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;

@SupportedAnnotationTypes("com.dudulina.code_generation.annotations.EventHandler")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
//@AutoService(Processor.class)
public class EventHandlersProcessor extends AbstractProcessor {

    public static final String builderClassName = "EventHandlersMapImpl";
    public static final String packageName = "com.dudulina.code_generation";

    private static AnnotationMirror getAnnotationMirror(Element element, TypeElement annotation) {
        for (AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
            if (annotationMirror.getAnnotationType().toString().equals(annotation.getQualifiedName().toString())) {
                return annotationMirror;
            }
        }
        return null;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement annotation : annotations) {
            try {
                writeCode(getEventHandlers(roundEnv.getElementsAnnotatedWith(annotation), annotation));
            } catch (Exception e) {
                return false;
            }
        }

        return false;
    }

    private HashMap<String, List<EventHandler>> getEventHandlers(Set<? extends Element> annotatedElements, TypeElement annotation) throws Exception {
        HashMap<String, List<EventHandler>> handlers = new HashMap<>();
        final Messager messager = processingEnv.getMessager();

        /*
         * @EventHandler
         * - to non-static methods
         * - into Aggregate class
         * - arguments: Event and optional EventMeta
         */
        for (Element element : annotatedElements) {
            ExecutableType type = (ExecutableType) element.asType();
            DeclaredType enclosingType = (DeclaredType) element.getEnclosingElement().asType();
            String listenerClassName = enclosingType.toString();

            String eventClassName = "";
            ArrayList<Error> errors = new ArrayList<>();

            final String methodName = element.getSimpleName().toString();
            if (element.getKind() != ElementKind.METHOD) {
                errors.add(new Error(
                        "annotated element is not a method (only methods can be annotated with @EventHandler)",
                        element,
                        getAnnotationMirror(element, annotation)
                ));
                throw new Exception();
            }

            if (element.getModifiers().contains(Modifier.STATIC)) {
                errors.add(new Error(
                        "method is static (only non-static methods can be annotated with @EventHandler)",
                        element,
                        getAnnotationMirror(element, annotation)
                ));
            }

            if (type.getParameterTypes().size() < 1) {
                errors.add(new Error(
                        "Event handler must have at least the Event parameter as the first parameter",
                        element,
                        getAnnotationMirror(element, annotation)
                ));
            } else {
                TypeMirror firstParam = type.getParameterTypes().get(0);

                TypeElement firstParamElement = processingEnv.getElementUtils().getTypeElement(firstParam.toString());
                eventClassName = firstParamElement.getQualifiedName().toString();

                if (firstParam.getKind().isPrimitive() || !typeIsInstanceOfInterface(firstParamElement, Event.class.getCanonicalName())) {
                    errors.add(new Error(
                            "First parameter must be instance of " + Event.class.getCanonicalName(),
                            firstParamElement
                    ));
                } else {
                    if (type.getParameterTypes().size() > 1) {
                        TypeMirror secondParam = type.getParameterTypes().get(0);
                        TypeElement secondParamElement = processingEnv.getElementUtils().getTypeElement(type.getParameterTypes().get(1).toString());
                        if (secondParam.getKind().isPrimitive() || !secondParamElement.getQualifiedName().toString()
                                .equals(MetaData.class.getCanonicalName())) {
                            errors.add(new Error(
                                    "Second optional parameter must be instance of " + MetaData.class.getCanonicalName(),
                                    secondParamElement
                            ));
                        }
                    }
                }
            }
            if (errors.size() > 0) {
                errors.forEach(this::error);
                throw new Exception();
            } else {
                List<EventHandler> existing = handlers.getOrDefault(eventClassName, new LinkedList<>());
                existing.add(new EventHandler(listenerClassName, methodName));
                handlers.put(eventClassName, existing);
            }
        }
        return handlers;
    }

    private void writeCode(HashMap<String, List<EventHandler>> handlers) throws IOException {
        if (handlers.isEmpty()) {
            return;
        }

        JavaFileObject builderFile = processingEnv.getFiler()
                .createSourceFile(packageName + "." + builderClassName);
        try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {

            out.print("package ");
            out.print(packageName);
            out.println(";");
            out.println();

            out.print("import java.util.HashMap;");
            out.println();
            out.print("public class " + builderClassName + " implements com.dudulina.events.EventHandlersMap ");
            out.println(" {");
            out.println();

            out.println(" @Override");
            out.println(" public HashMap<String, String[][]> getMap() { return map;} ");
            out.println(" private static HashMap<String, String[][]> map = new HashMap<>(); ");
            out.println(" static { ");
            handlers.forEach((s, eventHandlers) -> {
                out.println("map.put(\"" + s + "\", new String[][]{");
                eventHandlers.forEach(eventHandler -> {
                    out.println("new String[]{");
                    out.print("\"" + eventHandler.handlerClass + "\", \"" + eventHandler.methodName + "\"");
                    out.println("},");
                });
                out.println("});");
            });
            out.println(" } ");
            out.println();

            out.println("}");
        }
    }

    private boolean typeIsInstanceOfInterface(TypeElement type, String base) {
        return type.getInterfaces().stream().map(TypeMirror::toString).collect(Collectors.toList()).contains(base);
    }

    private void error(Error error) {
        if (error.annotationMirror != null) {
            processingEnv.getMessager().printMessage(Kind.ERROR, error.error, error.element, error.annotationMirror);
        } else {
            processingEnv.getMessager().printMessage(Kind.ERROR, error.error, error.element);
        }
    }
}

class EventHandler {

    public final String handlerClass;
    public final String methodName;

    public EventHandler(String aggregateClass, String methodName) {
        this.handlerClass = aggregateClass;
        this.methodName = methodName;
    }
}
