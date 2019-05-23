package cn.me.study.handwritten.myTomcat;

import java.io.IOException;
import java.io.OutputStream;

/**
 * 响应对象
 */
public class MyResponse {
    private OutputStream outputStream;

    public MyResponse(OutputStream outputStream){
        this.outputStream=outputStream;
    }

    public void write(String content) throws IOException {
        StringBuffer headResponse = new StringBuffer();
        headResponse.append("HTTP/1.1 200 OK\n")
                .append("Content-type:text/html\n")
                .append("\r\n")
                .append("<html><body>")
                .append(content).append("</body></html>");
        outputStream.write(headResponse.toString().getBytes());
        outputStream.close();
    }

}
