package pers.cheng.dij.core;

import java.util.concurrent.CompletableFuture;

public interface IBreakpoint extends IDebugResource {
    String getClassName();

    int getLineNumber();

    int getHitCount();

    void setHitCount(int hitCount);

    CompletableFuture<IBreakpoint> install();

    void putProperty(Object key, Object value);

    Object getProperty(Object key);

    String getCondition();

    void setCondition(String condition);

    String getLogMessage();

    void setLogMessage(String logMessage);
}
