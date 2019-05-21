package com.spring.factory;

public interface BeanFactory {
    public  <T> T getObject(String  beanName);
}
