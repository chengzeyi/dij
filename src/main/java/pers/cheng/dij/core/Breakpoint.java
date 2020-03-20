package pers.cheng.dij.core;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.sun.jdi.VirtualMachine;
import com.sun.jdi.request.EventRequest;

import io.reactivex.disposables.Disposable;

public class Breakpoint implements IBreakpoint {
    private VirtualMachine vm = null;
    private IEventHub eventHub = null;
    private String className = null;
    private int lineNumber = 0;
    private int hitCount = 0;
    private String condition = null;
    private String logMessage = null;
    private HashMap<Object, Object> propertyMap = new HashMap<>();

    Breakpoint(VirtualMachine vm, IEventHub eventHub, String className, int lineNumber) {
        this
    }

    Breakpoint(VirtualMachine vm, IEventHub eventHub, String className, int lineNumber, int hitCount,
            String condition) {
        this.vm = vm;
        this.eventHub = eventHub;
        this.className = className;
        this.lineNumber = lineNumber;
        this.hitCount = hitCount;
        this.condition = condition;
    }

    @Override
    public List<EventRequest> getRequests() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Disposable> getSubscriptions() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void close() throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public String getClassName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getgetLineNumber() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getHitCount() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setSetHitCount(int hitCount) {
        // TODO Auto-generated method stub

    }

    @Override
    public CompletableFuture<IBreakpoint> install() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void putProperty(Object key, Object value) {
        // TODO Auto-generated method stub

    }

    @Override
    public Object getProperty(Object key) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getCondition() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setCondition(String condition) {
        // TODO Auto-generated method stub

    }

    @Override
    public String getLogMessage() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setLogMessage(String logMessage) {
        // TODO Auto-generated method stub

    }
}
