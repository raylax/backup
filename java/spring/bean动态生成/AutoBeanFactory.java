package com.jiaomatech.blackbook.common.process;

import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AutoBeanFactory<T> implements FactoryBean<T> {

    private Class<T> beanInterface;

    public AutoBeanFactory(Class<T> beanInterface) {
        this.beanInterface = beanInterface;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T getObject() {
        return (T) createProxy();
    }

    @Override
    public Class<?> getObjectType() {
        return beanInterface;
    }

    private Object createProxy() {
        return Proxy.newProxyInstance(beanInterface.getClassLoader(), new Class[]{beanInterface}, (proxy, method, args) ->{
            String argsType = "";
            if (args != null) {
                argsType = Stream.of(method.getParameterTypes())
                    .map(Class::getName)
                    .collect(Collectors.joining(","));
            }
            System.out.println(method.getReturnType().getTypeName() + " " + beanInterface.getName() + "." + method.getName() +
                "(" + argsType + ")");
           return null;
        });
    }

}
