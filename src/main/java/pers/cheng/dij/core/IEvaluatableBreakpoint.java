package pers.cheng.dij.core;

public interface IEvaluatableBreakpoint {
    boolean containsEvaluatableExpression();

    boolean containsConditionalExpression();

    boolean containsLogpointExpression();

    String getCondition();

    void setCondition(String condition);

    String getLogMessage();

    void setLogMessage(String logMessage);

    void setCompiledConditionalExpression(Object compiledExpression);

    Object getCompiledConditionalExpression();

    void setCompiledLogpointExpression(Object compiledLogpointExpression);

    Object getCompiledLogpointExpression();
}
