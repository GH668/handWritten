package cn.me.study.handwritten.myTomcat;

import java.io.IOException;

/**
 * Servlet具体实现
 */
public class FindGirlServlet extends  MyServlet {

    @Override
    public void doGet(MyRequest request, MyResponse response) {

        try {
            response.write("get girl hello");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void doPost(MyRequest request, MyResponse response) {

        try {
            response.write("pot girl hello");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
