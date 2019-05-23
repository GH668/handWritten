package cn.me.study.handwritten.myTomcat;

import java.io.IOException;

/**
 * Servlet具体实现
 */
public class HelloServlet extends MyServlet {
    @Override
    public void doGet(MyRequest request, MyResponse response) {
        try {
            response.write("get Hello World");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void doPost(MyRequest request, MyResponse response) {
        try {
            response.write("post Hello World");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
