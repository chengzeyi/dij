package pers.cheng.dij.proxy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MainClassProxy {
    private String breakpointClassName;

    private String methodName;

    public MainClassProxy(String breakpointClassName, String methodName) {
        this.breakpointClassName = breakpointClassName;
    }

    public void execute() throws Throwable {
        Class<?> breakpointClass;
        try {
            breakpointClass = Class.forName(breakpointClassName);
        } catch (LinkageError | ClassNotFoundException e) {
            throw new ProxyException(String.format("Cannot get the class obejct of class %s", breakpointClassName), e);
        }

        Object instance;
        try {
            instance = breakpointClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new ProxyException(String.format("Cannot create an instance of class %s", breakpointClass));
        }

        Method[] methods;
        try {
            methods = breakpointClass.getMethods();
        } catch (SecurityException e) {
            throw new ProxyException(String.format("Cannot get the methods of class %s", breakpointClass), e);
        }

        Method method = null;
        for (Method met : methods) {
            if (met.getName().equals(methodName) && met.getParameterCount() == 0) {
                method = met;
                break;
            }
        }
        if (method == null) {
            throw new ProxyException(String.format("Cannot get method %s", methodName));
        }

        try {
            method.invoke(instance, new Object[] { null });
        } catch (IllegalAccessException | IllegalArgumentException e) {
            throw new ProxyException(String.format("Cannot invake method %s", method), e);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }

    // private <T> Object createInstance(Class<T> clazz) throws AdapterException {
    // Constructor<T>[] constructors;
    // try {
    // constructors = (Constructor<T>[]) clazz.getConstructors();
    // } catch (SecurityException e) {
    // throw new AdapterException(String.format("Cannot get the constructors of
    // class %s", clazz), e);
    // }
    // }

    public static void main(String[] args) throws Throwable {
        MainClassProxy mainClassProxy = new MainClassProxy(args[0], args[1]);
        try {
            mainClassProxy.execute();
        } catch (ProxyException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
