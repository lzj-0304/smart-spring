package com.spring;


import com.spring.factory.BeanFactory;
import com.spring.factory.ClassPathXmlApplicationContext;
import com.spring.service.UserService;

public class TestInstanceClass {
    public static void main(String[] args) {
        BeanFactory factory = new ClassPathXmlApplicationContext("spring.xml");
        /*UserDao userDao = factory.getObject("userDao");
        userDao.test();*/
        UserService userService = factory.getObject("userService");
        userService.test();
    }
}
