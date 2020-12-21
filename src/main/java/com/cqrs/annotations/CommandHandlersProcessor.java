package com.cqrs.annotations;

import com.cqrs.base.Aggregate;
import com.cqrs.base.Command;
import com.cqrs.commands.CommandMetaData;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.Writer;
import java.util.*;
import java.util.stream.Collectors;

@SupportedAnnotationTypes("com.cqrs.annotations.CommandHandler")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class CommandHandlersProcessor extends AbstractProcessor {

    public static final String AGGREGATE_COMMAND_HANDLERS_DIRECTORY = "com_cqrs_annotations_aggregateCommandHandlers";

    private static AnnotationMirror getAnnotationMirror(Element element, TypeElement annotation) {
        for (AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
            if (annotationMirror.getAnnotationType()
                .toString()
                .equals(annotation.getQualifiedName().toString())) {
                return annotationMirror;
            }
        }
        return null;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        annotations.forEach(annotation -> {
            try {
                writeCode(getCommandHandlers(roundEnv.getElementsAnnotatedWith(annotation), annotation));
            } catch (Exception e) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "" + e.getMessage());
            }
        });
        return false;
    }

    private HashMap<String, CommandHandler> getCommandHandlers(Set<? extends Element> annotatedElements,
                                                               TypeElement annotation
    ) throws Exception {
        HashMap<String, CommandHandler> handlers = new HashMap<>();
        final Messager messager = processingEnv.getMessager();
        /*
         * @CommandHandler
         * - to non-static methods
         * - into Aggregate class
         * - arguments: Command and optional CommandMeta
         */
        for (Element element : annotatedElements) {
            ExecutableType type = (ExecutableType) element.asType();
            DeclaredType enclosingType = (DeclaredType) element.getEnclosingElement().asType();
            String aggregateClassName = enclosingType.toString();

            String commandClassName = "";
            ArrayList<Error> errors = new ArrayList<>();

            final String methodName = element.getSimpleName().toString();
            if (element.getKind() != ElementKind.METHOD) {
                messager.printMessage(
                    Diagnostic.Kind.ERROR,
                    "annotated element is not a method (only methods can be annotated with @CommandHandler)",
                    element,
                    getAnnotationMirror(element, annotation)
                );
                throw new Exception();
            }

            if (element.getModifiers().contains(Modifier.STATIC)) {
                errors.add(new Error(
                    "method is static (only non-static methods can be annotated with @CommandHandler)",
                    element,
                    getAnnotationMirror(element, annotation)
                ));
            }

            final TypeElement aggregateType =
                processingEnv.getElementUtils().getTypeElement(aggregateClassName);
            if (!aggregateType.getSuperclass().toString().equals(Aggregate.class.getCanonicalName())) {
                errors.add(new Error(
                    "Class does not extent an " + Aggregate.class.getCanonicalName()
                        + " (only Aggregates can handle commands)",
                    element.getEnclosingElement()
                ));
            }

            if (type.getParameterTypes().size() < 1) {
                errors.add(new Error(
                    "Command handler must have at least the Command parameter as the first parameter",
                    element,
                    getAnnotationMirror(element, annotation)
                ));
            } else {
                TypeMirror firstParam = type.getParameterTypes().get(0);

                TypeElement firstParamElement =
                    processingEnv.getElementUtils().getTypeElement(firstParam.toString());
                commandClassName = firstParamElement.getQualifiedName().toString();

                if (firstParam.getKind().isPrimitive() ||
                    !typeIsInstanceOfInterface(firstParamElement, Command.class.getCanonicalName())) {
                    errors.add(new Error(
                        "First parameter must be instance of " + Command.class.getCanonicalName(),
                        firstParamElement
                    ));
                } else {
                    if (type.getParameterTypes().size() > 1) {
                        TypeMirror secondParam = type.getParameterTypes().get(0);
                        TypeElement secondParamElement = processingEnv.getElementUtils()
                            .getTypeElement(type.getParameterTypes().get(1).toString());
                        if (secondParam.getKind().isPrimitive() ||
                            !secondParamElement.getQualifiedName().toString()
                                .equals(CommandMetaData.class.getCanonicalName())) {
                            errors.add(new Error(
                                "Second optional parameter must be instance of " +
                                    CommandMetaData.class.getCanonicalName(),
                                secondParamElement
                            ));
                        }
                    }
                    if (handlers.containsKey(commandClassName)) {
                        CommandHandler existing = handlers.get(commandClassName);
                        errors.add(new Error(
                            "Only one command handler per command is permitted; this command " +
                                commandClassName + " has " + existing.aggregateClass
                                + "::" + existing.methodName + " and also " + aggregateClassName + "::" +
                                methodName.toString(),
                            element,
                            getAnnotationMirror(element, annotation)
                        ));
                    }
                }
            }
            if (errors.size() > 0) {
                errors.forEach(this::error);
                throw new Exception();
            } else {
                handlers.put(commandClassName, new CommandHandler(aggregateClassName, methodName.toString()));
            }
        }
        return handlers;
    }

    private void writeCode(HashMap<String, CommandHandler> handlers) {
        HashMap<String, List<String>> byAggregate = new HashMap<>();

        handlers.forEach((command, value) -> {
            List<String> aggregateHandlers = byAggregate.getOrDefault(value.aggregateClass, new LinkedList<>());
            aggregateHandlers.add(command + "," + value.methodName);
            byAggregate.put(value.aggregateClass, aggregateHandlers);
        });

        byAggregate.forEach((key, value) -> {
            try {
                System.out.println("Command Handler for " + key);
                System.out.println("    Write to " + StandardLocation.SOURCE_OUTPUT + "/" + AGGREGATE_COMMAND_HANDLERS_DIRECTORY + "/" + key);
                final Writer writer = processingEnv.getFiler()
                    .createResource(StandardLocation.SOURCE_OUTPUT, AGGREGATE_COMMAND_HANDLERS_DIRECTORY, key)
                    .openWriter();
                String commandAndHandler = String.join("\n", value);
                writer.write(commandAndHandler);
                writer.flush();
                writer.close();
                System.out.println("    " + commandAndHandler);
            } catch (IOException e) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
            }
        });
    }

    private boolean typeIsInstanceOfInterface(TypeElement type, String base) {
        return this.processingEnv.getTypeUtils().isAssignable(type.asType(), this.processingEnv.getElementUtils().getTypeElement(base).asType());
    }

    private void error(Error error) {
        if (error.annotationMirror != null) {
            processingEnv.getMessager()
                .printMessage(Diagnostic.Kind.ERROR,getClass().getSimpleName() + " error: " +  error.error, error.element, error.annotationMirror);
        } else {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, getClass().getSimpleName() + " error: " +  error.error, error.element);
        }
    }

    static class CommandHandler {

        public final String aggregateClass;
        public final String methodName;

        public CommandHandler(String aggregateClass, String methodName) {
            this.aggregateClass = aggregateClass;
            this.methodName = methodName;
        }
    }
}
