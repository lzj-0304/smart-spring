package com.spring.service;


import com.spring.annotations.Component;
import com.spring.annotations.Resource;
import com.spring.dao.UserDao;


@Component
public class UserService {
    @Resource("userDao")
    private UserDao userDao;

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public  void test(){
        userDao.test();
        System.out.println("UserService.test...");
    }
}
