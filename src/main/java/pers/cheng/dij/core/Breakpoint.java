package pers.cheng.dij.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

import com.sun.jdi.Location;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.VMDisconnectedException;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.event.ClassPrepareEvent;
import com.sun.jdi.request.BreakpointRequest;
import com.sun.jdi.request.ClassPrepareRequest;
import com.sun.jdi.request.EventRequest;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import pers.cheng.dij.Configuration;

public class Breakpoint implements IBreakpoint {
    private static final Logger LOGGER = Logger.getLogger(Configuration.LOGGER_NAME);

    private final VirtualMachine vm;
    private final IEventHub eventHub;
    private final String className;
    private final int lineNumber;
    private int hitCount;
    private String condition;
    private String logMessage;

    private List<EventRequest> requests = new ArrayList<>();
    private List<Disposable> subscriptions = new ArrayList<>();

    Breakpoint(VirtualMachine vm, IEventHub eventHub, String className, int lineNumber) {
        this(vm, eventHub, className, lineNumber, 0, null);
    }

    Breakpoint(VirtualMachine vm, IEventHub eventHub, String className, int lineNumber, int hitCount) {
        this(vm, eventHub, className, lineNumber, 0, null);
    }

    Breakpoint(VirtualMachine vm, IEventHub eventHub, String className, int lineNumber, int hitCount,
               String condition, String logMessage) {
        this.vm = vm;
        this.eventHub = eventHub;
        this.className = className;
        this.lineNumber = lineNumber;
        this.hitCount = hitCount;
        this.condition = condition;
        this.logMessage = logMessage;
    }

    Breakpoint(VirtualMachine vm, IEventHub eventHub, String className, int lineNumber, int hitCount,
               String condition) {
        this(vm, eventHub, className, lineNumber, hitCount, condition, null);
    }

    private List<BreakpointRequest> createBreakpointRequests(ReferenceType refType, int lineNumber, int hitCount,
                                                             boolean includeNestedTypes) {
        List<ReferenceType> refTypes = new ArrayList<>();
        refTypes.add(refType);
        return createBreakpointRequests(refTypes, lineNumber, hitCount, includeNestedTypes);
    }

    private List<BreakpointRequest> createBreakpointRequests(List<ReferenceType> refTypes, int lineNumber,
                                                             int hitCount, boolean includeNestedTypes) {
        List<Location> locations = collectLocations(refTypes, lineNumber, includeNestedTypes);

        // Find out the existing breakpoint locations.
        List<Location> existingLocations = new ArrayList<>(requests.size());
        Observable.fromIterable(requests).filter(request ->
                request instanceof BreakpointRequest).map(request ->
                ((BreakpointRequest) request).location()).toList().subscribe((Consumer<List<Location>>) existingLocations::addAll, onError -> {});

        // Remove duplicated locations.
        List<Location> newLocations = new ArrayList<>(locations.size());
        Observable.fromIterable(locations).filter(location ->
                !existingLocations.contains(location)).toList().subscribe((Consumer<List<Location>>) newLocations::addAll, onError -> {});

        List<BreakpointRequest> newRequests = new ArrayList<>(newLocations.size());
        newLocations.forEach(location -> {
            try {
                BreakpointRequest request = vm.eventRequestManager().createBreakpointRequest(location);
                request.setSuspendPolicy(BreakpointRequest.SUSPEND_EVENT_THREAD);
                if (hitCount > 0) {
                    request.addCountFilter(hitCount);
                }
                request.enable();
                LOGGER.info(String.format("BreakpointRequest has been created, %s", request));
            } catch (VMDisconnectedException e) {
                // Return an empty array.
                LOGGER.severe(String.format("Cannot create breakpointRequest, %s", e));
            }
        });

        return newRequests;
    }

    private static List<Location> collectLocations(List<ReferenceType> refTypes, int lineNumber, boolean includeNestedTypes) {
        List<Location> locations = new ArrayList<>();
        try {
            refTypes.forEach(refType -> {
                List<Location> newLocations = collectLocations(refType, lineNumber);
                if (!newLocations.isEmpty()) {
                    locations.addAll(newLocations);
                } else if (includeNestedTypes) {
                    for (ReferenceType nestedType : refType.nestedTypes()) {
                        List<Location> nestedLocations = collectLocations(nestedType, lineNumber);
                        if (!nestedLocations.isEmpty()) {
                            locations.addAll(nestedLocations);
                            // Avoid using nestedTypes for performance.
                            break;
                        }
                    }
                }
            });
        } catch (VMDisconnectedException e) {
            // Should return an empty array.
            LOGGER.severe("The target VM has been disconnected");
        }

        return locations;
    }

    private static List<Location> collectLocations(ReferenceType refType, int lineNumber) {
        List<Location> locations = new ArrayList<>();

        try {
            locations.addAll(refType.locationsOfLine(lineNumber));
        } catch (Exception e) {
            // Could be AbsentInformationException or ClassNotPreparedException.
            // But both are expected, so no need to further handle.
        }

        return locations;
    }

    @Override
    public List<EventRequest> getRequests() {
        return requests;
    }

    @Override
    public List<Disposable> getSubscriptions() {
        return subscriptions;
    }

    @Override
    public void close() throws Exception {
        try {
            vm.eventRequestManager().deleteEventRequests(requests);
        } catch (VMDisconnectedException e) {
            // ignore.
        }
        getSubscriptions().forEach(Disposable::dispose);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof IBreakpoint)) {
            return super.equals(obj);
        }
        IBreakpoint breakpoint = (IBreakpoint) obj;
        return this.getClassName().equals(breakpoint.getClassName()) &&
                this.getLineNumber() == breakpoint.getLineNumber();
    }

    @Override
    public String getClassName() {
        return className;
    }

    @Override
    public int getLineNumber() {
        return lineNumber;
    }

    @Override
    public int getHitCount() {
        return hitCount;
    }

    @Override
    public void setHitCount(int hitCount) {
        this.hitCount = hitCount;
        Observable.fromIterable(this.getRequests()).filter(request ->
                request instanceof BreakpointRequest).subscribe(request -> {
            request.addCountFilter(hitCount);
            request.enable();
        }, onError -> {});
    }

    @Override
    public CompletableFuture<IBreakpoint> install() {
        // Different class loaders can create new class with the same name.
        // Listen to future class prepare events to handle such case.
        ClassPrepareRequest classPrepareRequest = vm.eventRequestManager().createClassPrepareRequest();
        classPrepareRequest.addClassFilter(className);
        classPrepareRequest.enable();
        requests.add(classPrepareRequest);
        LOGGER.info("Created class prepare request for installing breakpoint");

        // Local types also need to be handled.
        ClassPrepareRequest localClassPrepareRequest = vm.eventRequestManager().createClassPrepareRequest();
        localClassPrepareRequest.addClassFilter(className + "$*");
        localClassPrepareRequest.enable();
        requests.add(localClassPrepareRequest);
        LOGGER.info("Created local class prepare request for installing breakpoint");

        CompletableFuture<IBreakpoint> future = new CompletableFuture<>();

        // Create new BreakpointRequests when the class is loaded.
        Disposable subscription = eventHub.getEvents().filter(debugEvent ->
                debugEvent.getEvent() instanceof ClassPrepareEvent &&
                        (classPrepareRequest.equals(debugEvent.getEvent().request()) ||
                                localClassPrepareRequest.equals(debugEvent.getEvent().request()))
        ).subscribe(debugEvent -> {
            ClassPrepareEvent event = (ClassPrepareEvent) debugEvent.getEvent();
            List<BreakpointRequest> newRequests = createBreakpointRequests(
                    event.referenceType(), lineNumber, hitCount, false);
            requests.addAll(newRequests);
            LOGGER.info(String.format("Created breakpointRequests in subscription of ClassPrepareEvent, class: %s, line: %s", event.referenceType().name(), lineNumber));
            if (!newRequests.isEmpty() && !future.isDone()) {
                future.complete(this);
            }
        }, onError -> {});
        subscriptions.add(subscription);

        // Create BreakpointRequests for loaded classes.
        // TODO: fix bug
        List<ReferenceType> refTypes = vm.classesByName(className);
        if (refTypes.isEmpty()) {
            LOGGER.info(String.format("No loaded classes found for %s", className));
        } else {
            LOGGER.info(String.format("Got loaded classes matching %s from the target VM for installing breakpoints, %s", className, refTypes));
        }
        List<BreakpointRequest> newRequests = createBreakpointRequests(refTypes, lineNumber, hitCount, true);
        LOGGER.info("Created breakpointRequests for loaded classes");
        requests.addAll(newRequests);

        if (!newRequests.isEmpty() && !future.isDone()) {
            future.complete(this);
        }

        return future;
    }

    @Override
    public String getCondition() {
        return condition;
    }

    @Override
    public void setCondition(String condition) {
        this.condition = condition;
    }

    @Override
    public String getLogMessage() {
        return logMessage;
    }

    @Override
    public void setLogMessage(String logMessage) {
        this.logMessage = logMessage;
    }
}
