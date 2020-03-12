package pers.cheng.dij.core;

public interface IEvaluatableBreakpoint {
    public boolean containsEvaluatableExpression();

    public boolean containsConditionalExpression();

    public boolean containsLogpointExpression();

    public String getCondition();

    public void setCondition(String condition);

    public String getLogMessage();

    public void setLogMessage(String logMessage);

    public void setCompiledConditionalExpression(Object compiledExpression);

    public Object getCompiledConditionalExpression();

    public void setCompiledLogpointExpression(Object compiledLogpointExpression);

    public Object getCompiledLogpointExpression();
}
