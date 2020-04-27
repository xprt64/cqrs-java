package com.dudulina.code_generation.processors;

import com.dudulina.base.Aggregate;
import com.dudulina.base.Command;
import com.dudulina.command.CommandMetaData;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.processing.AbstractProcessor;
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
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;

@SupportedAnnotationTypes("com.dudulina.code_generation.annotations.CommandHandler")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class CommandHandlersProcessor extends AbstractProcessor {

    public static final String builderClassName = "CommandHandlersMapImpl";
    public static final String packageName = "com.dudulina.code_generation";
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement annotation : annotations) {
            Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);
            try {
                writeCode(getCommandHandlers(annotatedElements));
            } catch (Exception e) {
                return false;
            }
        }

        return true;
    }

    private HashMap<String, CommandHandler> getCommandHandlers(Set<? extends Element> annotatedElements) throws Exception {
        HashMap<String, CommandHandler> handlers = new HashMap<>();

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
                error(methodName + " is not a method (only methods can be annotated with @CommandHandler)");
                throw new Exception();
            }

            if (element.getModifiers().contains(Modifier.STATIC)) {
                errors.add(new Error("method is static (only non-static methods can be annotated with @CommandHandler)", aggregateClassName, methodName));
            }

            final TypeElement aggregateType = processingEnv.getElementUtils().getTypeElement(aggregateClassName);
            if (!aggregateType.getSuperclass().toString().equals(Aggregate.class.getCanonicalName())) {
                errors.add(new Error(
                        "Class does not extent an " + Aggregate.class.getCanonicalName()
                                + " (only Aggregates can handle commands)", aggregateClassName, methodName
                ));
            }

            if (type.getParameterTypes().size() < 1) {
                errors.add(new Error("Command handler must have at least the Command parameter as the first parameter", aggregateClassName, methodName));
            } else {
                TypeMirror firstParam = type.getParameterTypes().get(0);

                TypeElement firstParamElement = processingEnv.getElementUtils().getTypeElement(firstParam.toString());
                commandClassName = firstParamElement.getQualifiedName().toString();

                if (firstParam.getKind().isPrimitive() || !typeIsInstanceOfInterface(firstParamElement, Command.class.getCanonicalName())) {
                    errors.add(new Error(
                            "First parameter must be instance of " + Command.class.getCanonicalName(), aggregateClassName, methodName
                    ));
                } else {
                    if (type.getParameterTypes().size() > 1) {
                        TypeMirror secondParam = type.getParameterTypes().get(0);
                        TypeElement secondParamElement = processingEnv.getElementUtils().getTypeElement(type.getParameterTypes().get(1).toString());
                        if (secondParam.getKind().isPrimitive() || !secondParamElement.getQualifiedName().toString()
                                .equals(CommandMetaData.class.getCanonicalName())) {
                            errors.add(new Error(
                                    "Second parameter must be instance of " + CommandMetaData.class.getCanonicalName(), aggregateClassName, methodName
                            ));
                        }
                    }
                    if (handlers.containsKey(commandClassName)) {
                        CommandHandler existing = handlers.get(commandClassName);
                        errors.add(new Error(
                                "Only one command handler per command is permitted; this command " + commandClassName + " has " + existing.aggregateClass
                                        + "::" + existing.methodName + " and also " + aggregateClassName + "::" + methodName.toString(), aggregateClassName,
                                methodName
                        ));
                    }
                }
            }
            if (errors.size() > 0) {
                errors.forEach(s -> error(s.print()));
                throw new Exception();
            } else {
                handlers.put(commandClassName, new CommandHandler(aggregateClassName, methodName.toString()));
            }
        }
        return handlers;
    }

    private void writeCode(HashMap<String, CommandHandler> handlers) throws IOException {
        if (handlers.isEmpty()) {
            return;
        }

        JavaFileObject builderFile = processingEnv.getFiler()
                .createSourceFile(packageName + "." + builderClassName);
        try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {

            if (packageName != null) {
                out.print("package ");
                out.print(packageName);
                out.println(";");
                out.println();
            }

            out.print("import java.util.HashMap;");
            out.println();
            out.print("public class " + builderClassName + " implements com.dudulina.command.CommandHandlersMap ");
            out.println(" {");
            out.println();

            out.println(" @Override");
            out.println(" public HashMap<String, String[]> getMap() { return map;} ");
            out.println(" private static HashMap<String, String[]> map; ");
            out.println(" static { ");
            handlers.forEach((s, commandHandler) -> out.println("map.put(\"" + s + "\", new String[]{\"" + commandHandler.aggregateClass + "\",\"" + commandHandler.methodName + "\"});"));
            out.println(" } ");
            out.println();

            out.println("}");
        }
    }

    private boolean typeIsInstanceOfInterface(TypeElement type, String base) {
        return type.getInterfaces().stream().map(TypeMirror::toString).collect(Collectors.toList()).contains(base);
    }

    private void error(String message) {
        processingEnv.getMessager().printMessage(Kind.ERROR, message);
    }
}

class CommandHandler {

    public final String aggregateClass;
    public final String methodName;

    public CommandHandler(String aggregateClass, String methodName) {
        this.aggregateClass = aggregateClass;
        this.methodName = methodName;
    }
}

class Error {

    public final String error;
    public final String aggregateClass;
    public final String methodName;

    public Error(String error, String aggregateClass, String methodName) {
        this.error = error;
        this.aggregateClass = aggregateClass;
        this.methodName = methodName;
    }

    public String print() {
        return "Parsing error:" + aggregateClass + ":" + methodName + ": " + error;
    }
}