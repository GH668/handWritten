package cn.me.study.handwritten.myTomcat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * tomcat启动类
 */
public class MyTomcat {

    //端口配置
    private int port=8080;

    //url反射Servlet容器
    private Map<String,String> urlServletMap=new HashMap<>();

    public MyTomcat(int port){
        this.port=port;
    }

    //tomcat启动
    public void start(){
        //初始化所有的url和servlet配置
        initServletMapping();
        //服务端开启Socket
        ServerSocket serverSocket=null;
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("myTomcat is starting,port is "+port);
            while (true){
                Socket socket = serverSocket.accept();
                InputStream inputStream = socket.getInputStream();
                OutputStream outputStream = socket.getOutputStream();
                MyRequest myRequest = new MyRequest(inputStream);
                MyResponse myResponse = new MyResponse(outputStream);
                //请求分发
                dispatch(myRequest,myResponse);
                socket.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(serverSocket!=null){
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    //初始化所有的url和servlet配置
    private void initServletMapping(){
        for (ServletMapping servletMapping:ServletMappingConfig.servletMappingList) {
            urlServletMap.put(servletMapping.getUrl(),servletMapping.getClazz());
        }
    }

    /**
     * 请求分发
     */
    private void dispatch(MyRequest myRequest,MyResponse myResponse){
        String  clazz = urlServletMap.get(myRequest.getUrl());
        //反射调用指定servlet实现类
        try {
            Class<MyServlet> myServletClass = (Class<MyServlet>)Class.forName(clazz);
            MyServlet myServlet = myServletClass.newInstance();
            myServlet.service(myRequest,myResponse);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    //开启myTomcat
    public static void main(String[] args) {
        new MyTomcat(8087).start();
    }
}
