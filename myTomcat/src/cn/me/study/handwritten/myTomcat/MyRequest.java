package cn.me.study.handwritten.myTomcat;

import java.io.IOException;
import java.io.InputStream;

/**
 * 请求对象
 */

public class MyRequest {

    private  String url;
    private String method;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    /**
     * 将socket中输入流封装rquest对象
     * @param inputStream
     * @throws IOException
     */
    public MyRequest(InputStream inputStream) throws IOException {
        String httpRequest="";
        byte[] httpRequestBytes = new byte[1024];
        int length=0;
        if((length=inputStream.read(httpRequestBytes))>0){
            //有用的socket流
            httpRequest=new String(httpRequestBytes,0,length);
        }
        //获取请求头
        String requestHead = httpRequest.split("\n")[0];//获取第一行
        //获取url
         url = requestHead.split("\\s")[1];//按照空格分割
         method = requestHead.split("\\s")[0];
        System.out.println(method+":"+url);
    }
}
