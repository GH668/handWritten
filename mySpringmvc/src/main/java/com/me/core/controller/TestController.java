package com.me.core.controller;

import com.me.annotation.MyRequestMapping;
import com.me.annotation.MyRequestParam;
import com.me.annotation.Mycontroller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Mycontroller(value = "/test")
public class TestController {

    @MyRequestMapping(value = "/hello")
    public void hello(HttpServletRequest request, HttpServletResponse response,
                      @MyRequestParam(value = "name") String name){
        System.out.println("Hello>>>>>>"+name);
        try {
            response.setHeader("Content-type","text/html;charset=UTF-8");
            response.setCharacterEncoding("utf-8");
            response.getWriter().write("hello  "+name+" 调用成功了...........");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
