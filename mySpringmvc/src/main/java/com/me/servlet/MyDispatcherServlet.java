package com.me.servlet;

import com.me.annotation.MyRequestMapping;
import com.me.annotation.Mycontroller;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class MyDispatcherServlet extends HttpServlet {

    private  Properties properties=new Properties();

    private List<String> classNames=new ArrayList();

    private Map<String,Object> ioc= new HashMap();

    private Map<String,Method> handlerMappings=new HashMap();

    private Map<String,Object> controllerMap=new HashMap();



    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            this.doDispatch(req, resp);
        } catch (IOException e) {
            e.printStackTrace();
            resp.getWriter().write("500!! Server Exception");
        }
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if(handlerMappings.isEmpty()){
            return;
        }
        String uri = req.getRequestURI();
        String contextPath = req.getContextPath();
        System.out.println("uri=========="+uri);
        uri= uri.replace(contextPath, "").replaceAll("/+", "/");
        if(!handlerMappings.containsKey(uri)){
            resp.getWriter().write("404 NOT FOUND ");
        }
        Method method = handlerMappings.get(uri);
        //获取方法参数列表
        Class<?>[] parameterTypes = method.getParameterTypes();
        //获取请求参数
        Map<String, String[]> parameterMap = req.getParameterMap();
        //将url中的参数封装到对应的处理器参数列表上
        Object[] paramsValues = new Object[parameterTypes.length];

        for (int i=0;i<parameterTypes.length;i++){
            String simpleName = parameterTypes[i].getSimpleName();
            if(simpleName.equals("HttpServletRequest")){
                paramsValues[i]=req;
            }
            if(simpleName.equals("HttpServletResponse")){
                paramsValues[i]=resp;
            }
            if(simpleName.equals("String")){
                for (Map.Entry<String,String[]> entry: parameterMap.entrySet()) {
                    String value = Arrays.toString(entry.getValue()).replaceAll("\\[|\\]", "").replaceAll(",\\s", ",");
                    paramsValues[i]=value;
                }
            }

        }
        //利用反射机制来调用
        try {
            method.invoke(this.controllerMap.get(uri), paramsValues);//第一个参数是method所对应的实例 在ioc容器中
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void init(ServletConfig config) throws ServletException {
       // System.out.println(config.getInitParameter("contextConfigLocation").split(":")[1]);

       //加载配置文件
        doLoadConfig(config.getInitParameter("contextConfigLocation"));

        //2.初始化所有相关联的类,扫描用户设定的包下面所有的类
        doScanner(properties.getProperty("scanPackage"));

        //3.拿到扫描到的类,通过反射机制,实例化,并且放到ioc容器中(k-v  beanName-bean) beanName默认是首字母小写
        doInstance();

        //4.初始化HandlerMapping(将url和method对应上)
        initHandlerMapping();

    }

    /**
     * 加载application.properties配置文件
     * @param location
     */
    private void doLoadConfig(String location){

        if(location.startsWith("classpath:")){
            location = location.replace("classpath:","");
        }else if(location.contains("/")){
            int lastSplitIndex = location.lastIndexOf('/');
            location = location.substring(lastSplitIndex+1,location.length());
        }
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(location);
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(inputStream!=null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取指定包下的所有类（实际是获取所有Controller类路径）
     * @param packageName
     */
    private void doScanner(String packageName){
        //把所有的.替换成/
        URL resourceUrl = this.getClass().getClassLoader().getResource("/"+packageName.replaceAll("\\.", "/"));
       System.out.println("packagePath-------"+resourceUrl.toString());
        File dir = new File(resourceUrl.getFile());
        for (File file:dir.listFiles()) {
            //System.out.println("fileName"+file.getName());
            if(file.isDirectory()){
                //文件夹
                doScanner(packageName+"."+file.getName());
            }else{
                String className=packageName+"."+file.getName().replace(".class","");
                classNames.add(className);
            }
        }

    }

    /**
     * 将扫描获取的类实例放到IOC容器中（此处IOC容器使用简单map）
     */
    private void doInstance(){
        if(classNames.isEmpty()){
            return ;
        }
        for (String className:classNames) {
            //类实例化，只是针对加了@Mycontroller注解的类
            try {
                Class<?>  clazz = Class.forName(className);
                if(clazz.isAnnotationPresent(Mycontroller.class)){
                    ioc.put(toLowerFirstWord(clazz.getSimpleName()),clazz.newInstance());
                }else{
                    continue;
                }
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }

    }

    /**
     * 把字符串的首字母小写
     * @param name
     * @return
     */
    private String toLowerFirstWord(String name){
        char[] charArray = name.toCharArray();
        charArray[0] += 32;
        return String.valueOf(charArray);
    }


    /**
     * 封装处理器映射器容器
     */
    private void initHandlerMapping(){
        if(ioc.isEmpty()){
            return;
        }
        try {
            for (Map.Entry<String,Object> entry:ioc.entrySet()) {
                Class<?> clazz = entry.getValue().getClass();
                //判断是否存在Mycontroller注解
                if(!clazz.isAnnotationPresent(Mycontroller.class)){
                    continue;
                }
                //拼接全局url,包括controller类url和方法上的url
                String baseUrl="";
                if(clazz.isAnnotationPresent(Mycontroller.class)){
                    //获取指定注解
                    //FIXME 可能是获取Mycontroller注解
                    Mycontroller mycontroller = clazz.getAnnotation(Mycontroller.class);
                    baseUrl = mycontroller.value();
                }
                //获取类中的所有方法
                Method[] methods = clazz.getMethods();
                for (Method method:methods) {
                    if(!method.isAnnotationPresent(MyRequestMapping.class)){
                        continue;
                    }
                    MyRequestMapping myRequestMapping = method.getAnnotation(MyRequestMapping.class);
                    String url = myRequestMapping.value();
                    url=(baseUrl+url).replace("/+","/");
                    handlerMappings.put(url,method);
                    //防止重复构造controller
                    Object tmpValue = null;
                    String ctlName = toLowerFirstWord(clazz.getSimpleName());
                    if(ioc.containsKey(ctlName)){
                        tmpValue = ioc.get(ctlName);
                    }else{
                        tmpValue = clazz.newInstance();
                    }
                    controllerMap.put(url, tmpValue);

                    System.out.println(url+":"+method);

                }
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

}
