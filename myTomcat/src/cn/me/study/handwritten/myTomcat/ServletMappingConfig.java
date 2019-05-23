package cn.me.study.handwritten.myTomcat;

import java.util.ArrayList;
import java.util.List;

/**
 * servlet具体配置类,功能类似于在web.xml中配置<servlet></servlet> 和 <servlet-mapping></servlet-mapping> ，
 * 决定url由那个servlet处理
 */
public class ServletMappingConfig {

    public static List<ServletMapping> servletMappingList=new ArrayList<>();

    static {
        servletMappingList.add(new ServletMapping("findGirl","/girl","cn.me.study.handwritten.myTomcat.FindGirlServlet"));
        servletMappingList.add(new ServletMapping("helloWorld","/hello","cn.me.study.handwritten.myTomcat.HelloServlet"));
    }
}
