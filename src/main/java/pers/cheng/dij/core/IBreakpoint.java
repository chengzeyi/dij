package pers.cheng.dij.core;

import java.util.concurrent.CompletableFuture;

public interface IBreakpoint extends IDebugResource {
    public String getClassName();

    public int getLineNumber();

    public int getHitCount();

    public void setHitCount(int hitCount);

    public CompletableFuture<IBreakpoint> install();

    public void putProperty(Object key, Object value);

    public Object getProperty(Object key);

    public String getCondition();

    public void setCondition(String condition);

    public String getLogMessage();

    public void setLogMessage(String logMessage);
}
