package pers.cheng.dij.core;

import com.sun.jdi.VirtualMachine;
import org.apache.commons.lang3.StringUtils;

public class EvaluatableBreakpoint extends Breakpoint implements IEvaluatableBreakpoint {
    private Object compiledConditionalExpression = null;
    private Object compiledLogpointExpression = null;

    EvaluatableBreakpoint(VirtualMachine vm, IEventHub eventHub, String className, int lineNumber) {
        super(vm, eventHub, className, lineNumber, 0, null);
    }

    EvaluatableBreakpoint(VirtualMachine vm, IEventHub eventHub, String className, int lineNumber, int hitCount) {
        super(vm, eventHub, className, lineNumber, hitCount, null);
    }

    EvaluatableBreakpoint(VirtualMachine vm, IEventHub eventHub, String className, int lineNumber, int hitCount, String condition) {
        super(vm, eventHub, className, lineNumber, hitCount, condition);
    }

    EvaluatableBreakpoint(VirtualMachine vm, IEventHub eventHub, String className, int lineNumber, int hitCount, String condition,
                          String logMessage) {
        super(vm, eventHub, className, lineNumber, hitCount, condition, logMessage);
    }

    @Override
    public boolean containsEvaluatableExpression() {
        return containsConditionalExpression() || containsLogpointExpression();
    }

    @Override
    public boolean containsConditionalExpression() {
        return StringUtils.isNotBlank(getCondition());
    }

    @Override
    public boolean containsLogpointExpression() {
        return StringUtils.isNotBlank(getLogMessage());
    }

    @Override
    public void setCompiledConditionalExpression(Object compiledExpression) {
        this.compiledConditionalExpression = compiledExpression;
    }

    @Override
    public Object getCompiledConditionalExpression() {
        return compiledConditionalExpression;
    }

    @Override
    public void setCompiledLogpointExpression(Object compiledLogpointExpression) {
        this.compiledLogpointExpression = compiledLogpointExpression;
    }

    @Override
    public Object getCompiledLogpointExpression() {
        return compiledLogpointExpression;
    }
}
