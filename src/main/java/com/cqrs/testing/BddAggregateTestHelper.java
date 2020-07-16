package com.cqrs.testing;

import com.cqrs.aggregates.AggregateExecutionException;
import com.cqrs.aggregates.EventApplierOnAggregate;
import com.cqrs.base.Aggregate;
import com.cqrs.base.Command;
import com.cqrs.base.Event;
import com.cqrs.commands.CommandApplier;
import com.cqrs.commands.CommandHandlerDescriptor;
import com.cqrs.commands.CommandHandlerNotFound;
import com.cqrs.commands.CommandSubscriber;
import com.cqrs.events.EventWithMetaData;
import com.cqrs.events.MetaData;
import com.cqrs.testing.exceptions.ExpectedEventNotYielded;
import com.cqrs.testing.exceptions.TooManyEventsFired;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.time.LocalDateTime;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BddAggregateTestHelper {
    private final CommandApplier commandApplier;
    private final CommandSubscriber commandSubscriber;
    private String aggregateId;
    private List<EventWithMetaData> priorEvents = new LinkedList<>();
    private Command command;
    private Aggregate aggregate;

    public BddAggregateTestHelper(
        CommandSubscriber commandSubscriber
    ) {
        this.commandSubscriber = commandSubscriber;
        commandApplier = new CommandApplier();
    }

    public static void assertEventListsAreEqual(List<Event> expectedEvents, List<Event> actualEvents
    ) throws ExpectedEventNotYielded, TooManyEventsFired {
        List<String> expected =
            expectedEvents.stream().map(BddAggregateTestHelper::hashEvent).collect(Collectors.toList());
        List<String> actual =
            actualEvents.stream().map(BddAggregateTestHelper::hashEvent).collect(Collectors.toList());

        final ArrayList<String> tooFew = new ArrayList<>(expected);
        tooFew.removeAll(actual);
        if (tooFew.size() > 0) {
            throw new ExpectedEventNotYielded(tooFew.size() + " more events expected to be emitted: " + String.join("\n", tooFew));
        }

        final ArrayList<String> tooMany = new ArrayList<String>(actual);
        tooMany.removeAll(expected);
        if (tooMany.size() > 0) {
            throw new TooManyEventsFired("Too many events emitted: " + String.join("\n", tooMany));
        }
    }

    public static String hashEvent(Event event) {
        return event.getClass().getCanonicalName() + ":" + StringDump.dump(event);
    }

    private boolean isClassOrSubClass(Class<?> parentClass, Class<?> childClass) {
        return parentClass.isAssignableFrom(childClass);
    }

    public BddAggregateTestHelper onAggregate(Aggregate aggregate) {
        this.aggregate = aggregate;
        aggregateId = "123";
        return this;
    }

    public BddAggregateTestHelper given(Event... priorEvents) {
        this.priorEvents =
            Arrays.stream(priorEvents).map(this::decorateEventWithMetaData).collect(Collectors.toList());
        return this;
    }

    public BddAggregateTestHelper when(Command command) {
        this.command = command;
        return this;
    }

    public void then(Event... expectedEvents
    ) throws TooManyEventsFired, ExpectedEventNotYielded, CommandHandlerNotFound, AggregateExecutionException {
        Objects.requireNonNull(command);

        priorEvents.forEach(eventWithMetaData -> EventApplierOnAggregate.applyEvent(aggregate,
                                                                                    eventWithMetaData.event,
                                                                                    eventWithMetaData.metadata));

        List<Event> newEvents = executeCommand(command);

        assertTheseEvents(Arrays.asList(expectedEvents), newEvents);
    }

    public void thenThrows(Class<? extends Throwable> expectedClass) throws Exception{
        try{
            Objects.requireNonNull(command);

            priorEvents.forEach(eventWithMetaData -> EventApplierOnAggregate.applyEvent(aggregate,
                    eventWithMetaData.event,
                    eventWithMetaData.metadata));

            executeCommand(command);
        } catch (AggregateExecutionException e) {
            Throwable cause = e.getCause();
            if(cause instanceof InvocationTargetException){
                cause = cause.getCause();
            }
            String causeName = cause.getClass().getCanonicalName();
            if(!causeName.equals(expectedClass.getCanonicalName())){
                throw new Exception("Expected " + expectedClass.getCanonicalName() + " but thrown " + causeName);
            }
        } catch (Throwable e) {
            throw new Exception("Expected " + expectedClass.getCanonicalName() + " but thrown " + e.getClass().getCanonicalName());
        }
    }

    public List<Event> executeCommand(Command $command
    ) throws CommandHandlerNotFound, AggregateExecutionException {
        CommandHandlerDescriptor handler = commandSubscriber.getAggregateForCommand(command.getClass());

        return commandApplier.applyCommand(aggregate, $command, handler.methodName);
    }

    private EventWithMetaData decorateEventWithMetaData(Event event) {
        return new EventWithMetaData(event, factoryMetaData());
    }

    public void assertTheseEvents(List<Event> expectedEvents, List<Event> actualEvents
    ) throws TooManyEventsFired, ExpectedEventNotYielded {
        assertEventListsAreEqual(expectedEvents, actualEvents);
        checkForToManyEvents(actualEvents.size() - expectedEvents.size());
    }

    private void checkForToManyEvents(int additionalCount) throws TooManyEventsFired {
        if (additionalCount > 0) {
            throw new TooManyEventsFired(
                String.format("Additional %d events fired", additionalCount));
        }
    }

    private MetaData factoryMetaData() {
        return new MetaData(
            LocalDateTime.now(),
            aggregateId,
            aggregate.getClass().getCanonicalName()
        );
    }

    private static class StringDump {

        /**
         * Uses reflection and recursion to dump the contents of the given object using a custom, JSON-like notation (but not JSON). Does not format static fields.<p>
         *
         * @param object the {@code Object} to dump using reflection and recursion
         * @return a custom-formatted string representing the internal values of the parsed object
         * @see #dump(Object, boolean, IdentityHashMap, int)
         */
        public static String dump(Object object) {
            return dump(object, false, new IdentityHashMap<Object, Object>(), 0);
        }

        /**
         * Uses reflection and recursion to dump the contents of the given object using a custom, JSON-like notation (but not JSON).<p>
         * Parses all fields of the runtime class including super class fields, which are successively prefixed with "{@code super.}" at each level.<p>
         * {@code Number}s, {@code enum}s, and {@code null} references are formatted using the standard {@link String::valueOf()} method.
         * {@code CharSequences}s are wrapped with quotes.<p>
         * The recursive call invokes only one method on each recursive call, so limit of the object-graph depth is one-to-one with the stack overflow limit.<p>
         * Backwards references are tracked using a "visitor map" which is an instance of {@link IdentityHashMap}.
         * When an existing object reference is encountered the {@code "sysId"} is printed and the recursion ends.<p>
         *
         * @param object             the {@code Object} to dump using reflection and recursion
         * @param isIncludingStatics {@code true} if {@code static} fields should be dumped, {@code false} to skip them
         * @return a custom-formatted string representing the internal values of the parsed object
         */
        public static String dump(Object object, boolean isIncludingStatics) {
            return dump(object, isIncludingStatics, new IdentityHashMap<Object, Object>(), 0);
        }

        private static String dump(Object object, boolean isIncludingStatics,
                                   IdentityHashMap<Object, Object> visitorMap, int tabCount
        ) {
            if (object == null ||
                object instanceof Number || object instanceof Character || object instanceof Boolean ||
                object.getClass().isPrimitive() || object.getClass().isEnum()) {
                return String.valueOf(object);
            }

            if(object instanceof List){
                Stream<String> stream = ((List) object).stream().map(o -> dump(o));
                return stream.collect(Collectors.joining("\n"));
            }

            StringBuilder builder = new StringBuilder();
            int sysId = System.identityHashCode(object);
            if (object instanceof CharSequence) {
                builder.append("\"").append(object).append("\"");
            } else if (visitorMap.containsKey(object)) {
               // builder.append("(sysId#").append(sysId).append(")");
            } else {
              //  visitorMap.put(object, object);

                StringBuilder tabs = new StringBuilder();
                for (int t = 0; t < tabCount; t++) {
                    tabs.append("\t");
                }
                if (object.getClass().isArray()) {
                    builder.append("[").append(object.getClass().getName());//.append(":sysId#").append(sysId);
                    int length = Array.getLength(object);
                    for (int i = 0; i < length; i++) {
                        Object arrayObject = Array.get(object, i);
                        String dump = dump(arrayObject, isIncludingStatics, visitorMap, tabCount + 1);
                        builder.append("\n\t").append(tabs).append("\"").append(i).append("\":").append(dump);
                    }
                    builder.append(length == 0 ? "" : "\n").append(length == 0 ? "" : tabs).append("]");
                } else {
                    // enumerate the desired fields of the object before accessing
                    TreeMap<String, Field> fieldMap =
                        new TreeMap<String, Field>();  // can modify this to change or omit the sort order
                    StringBuilder superPrefix = new StringBuilder();
                    for (Class<?> clazz = object.getClass();
                         clazz != null && !clazz.equals(Object.class); clazz = clazz.getSuperclass()) {
                        Field[] fields = clazz.getDeclaredFields();
                        for (int i = 0; i < fields.length; i++) {
                            Field field = fields[i];
                            if (isIncludingStatics || !Modifier.isStatic(field.getModifiers())) {
                                fieldMap.put(superPrefix + field.getName(), field);
                            }
                        }
                        superPrefix.append("super.");
                    }

                    builder.append("{").append(object.getClass().getName());//.append(":sysId#").append(sysId);
                    for (Entry<String, Field> entry : fieldMap.entrySet()) {
                        String name = entry.getKey();
                        Field field = entry.getValue();
                        String dump;
                        try {
                            boolean wasAccessible = field.isAccessible();
                            field.setAccessible(true);
                            Object fieldObject = field.get(object);
                            field.setAccessible(wasAccessible);  // the accessibility flag should be restored to its prior ClassLoader state
                            dump = dump(fieldObject, isIncludingStatics, visitorMap, tabCount + 1);
                        } catch (Throwable e) {
                            dump = "!" + e.getClass().getName() + ":" + e.getMessage();
                        }
                        builder.append("\n\t")
                            .append(tabs)
                            .append("\"")
                            .append(name)
                            .append("\":")
                            .append(dump);
                    }
                    builder.append(fieldMap.isEmpty() ? "" : "\n")
                        .append(fieldMap.isEmpty() ? "" : tabs)
                        .append("}");
                }
            }
            return builder.toString();
        }
    }
}
