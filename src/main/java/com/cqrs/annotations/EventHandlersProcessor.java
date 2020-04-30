package com.cqrs.annotations;

import com.cqrs.base.Event;
import com.cqrs.events.MetaData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

@SupportedAnnotationTypes("com.cqrs.*")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
//@AutoService(Processor.class)
public class EventHandlersProcessor extends AbstractEventHandlerProcessor {

    public static final String EVENT_HANDLERS_MAP_IMPL = "EventHandlersMapImpl";

    @Override
    protected String getAnnotationName() {
        return "com.cqrs.annotations.EventHandler";
    }

    @Override
    protected String getGeneratedClassName() {
        return EVENT_HANDLERS_MAP_IMPL;
    }

    protected HashMap<String, List<EventHandler>> getEventHandlers(Set<? extends Element> annotatedElements,
                                                                   TypeElement annotation
    ) throws Exception {
        HashMap<String, List<EventHandler>> handlers = new HashMap<>();
        /*
         * @EventHandler
         * - to non-static methods
         * - arguments: Event and optional EventMeta
         */
        for (Element element : annotatedElements) {
            ExecutableType type = (ExecutableType) element.asType();
            DeclaredType enclosingType = (DeclaredType) element.getEnclosingElement().asType();
            String listenerClassName = enclosingType.toString();

            String eventClassName = "";
            ArrayList<com.cqrs.annotations.Error> errors = new ArrayList<>();

            final String methodName = element.getSimpleName().toString();
            if (element.getKind() != ElementKind.METHOD) {
                processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.ERROR,
                    "annotated element is not a method (only methods can be annotated with @EventHandler)",
                    element,
                    getAnnotationMirror(element, annotation)
                );
                throw new Exception();
            }

            if (element.getModifiers().contains(Modifier.STATIC)) {
                errors.add(new com.cqrs.annotations.Error(
                    "method is static (only non-static methods can be annotated with @EventHandler)",
                    element,
                    getAnnotationMirror(element, annotation)
                ));
            }

            if (type.getParameterTypes().size() < 1) {
                errors.add(new com.cqrs.annotations.Error(
                    "Event handler must have at least the Event parameter as the first parameter",
                    element,
                    getAnnotationMirror(element, annotation)
                ));
            } else {
                TypeMirror firstParam = type.getParameterTypes().get(0);

                TypeElement firstParamElement =
                    processingEnv.getElementUtils().getTypeElement(firstParam.toString());
                eventClassName = firstParamElement.getQualifiedName().toString();

                if (firstParam.getKind().isPrimitive() ||
                    !typeIsInstanceOfInterface(firstParamElement, Event.class.getCanonicalName())) {
                    errors.add(new com.cqrs.annotations.Error(
                        "First parameter must be instance of " + Event.class.getCanonicalName(),
                        firstParamElement
                    ));
                } else {
                    if (type.getParameterTypes().size() > 1) {
                        TypeMirror secondParam = type.getParameterTypes().get(0);
                        TypeElement secondParamElement = processingEnv.getElementUtils()
                            .getTypeElement(type.getParameterTypes().get(1).toString());
                        if (secondParam.getKind().isPrimitive() ||
                            !secondParamElement.getQualifiedName().toString()
                                .equals(MetaData.class.getCanonicalName())) {
                            errors.add(new com.cqrs.annotations.Error(
                                "Second optional parameter must be instance of " +
                                MetaData.class.getCanonicalName(),
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

}

