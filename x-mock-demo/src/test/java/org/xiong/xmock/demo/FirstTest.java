package org.xiong.xmock.demo;
import org.junit.Test;
import org.xiong.xmock.engine.annotation.AutoInject;
import org.xiong.xmock.engine.annotation.XMock;
import java.util.List;
import java.util.Map;

@XMock
public class FirstTest {
//-javaagent:/Users/user/Documents/binance_workspace/x-mock/x-mock-core/target/k-mock-core-1.0-SNAPSHOT.jar

    @AutoInject
    DemoMain demoMain;

    @AutoInject
    FacadeService facadeServiceFirst;

    @AutoInject
    FacadeService facadeServiceFirst1;

    @AutoInject
    FacadeService facadeService;

    @Test
    public void first() throws Exception {

         System.out.println( (demoMain.hello()));


//        System.out.println( (demoMain.getMgs() ));
//        System.out.println( (demoMain.getDouble() ));
//        System.out.println( (demoMain.getInt() ));
//        System.out.println( (demoMain.getMgsMap() ));
//        demoMain.getVoid();
//        System.out.println( (demoMain.getDemoItem() ));
//        System.out.println("]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]");
//        System.out.println( facadeServiceFirst.getServerName());
        System.out.println(facadeServiceFirst.getDemoMain(null,null));
    }

    @Test
    public void second() throws Exception {
        System.out.println( facadeService.getServerName());
        System.out.println(facadeService.getDemoMain());
        List<DemoItem> list = facadeService.getList();
        list.stream().forEach(d->{
            System.out.println( d.getName()+""+d.getAge());
        });

        List<Map<String,DemoItem>>  mapping = facadeService.getMapping();
        mapping.stream().forEach(r->{
            r.forEach((k,v)->{
                System.out.println(k+"======"+v);
            });
        });
    }
}
