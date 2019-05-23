package cn.me.study.handwritten.myTomcat;

/**
 * 自定义servlet,接收request和response对象
 */
public abstract class MyServlet {

    public abstract  void doGet(MyRequest request,MyResponse response);

    public abstract  void doPost(MyRequest request,MyResponse response);

    public void service(MyRequest request,MyResponse response){
        if("post".equalsIgnoreCase(request.getMethod())){
            doPost(request,response);
        }else if("get".equalsIgnoreCase(request.getMethod())){
            doGet(request,response);
        }

    }
}
