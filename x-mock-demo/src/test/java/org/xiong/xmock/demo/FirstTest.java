package org.xiong.xmock.demo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.bind.annotation.RestController;
import org.xiong.xmock.engine.annotation.AutoInject;
import org.xiong.xmock.engine.annotation.XMock;

import java.io.File;
import java.io.FileFilter;
import java.util.List;
import java.util.Map;


@XMock
@RestController
public class FirstTest {

    @AutoInject
    DemoMain demoMain;

    @AutoInject
    FacadeService facadeServiceFirst;

    @AutoInject
    FacadeService facadeServiceFirst1;
//
    @AutoInject
    FacadeService facadeService;
     //-javaagent:/Users/user/Documents/workspace/x-mock/x-mock-core/target/x-mock-core-1.0-SNAPSHOT.jar


    @Test
    public void first() throws Exception {
        System.out.println( (demoMain.hello()));
        System.out.println( (demoMain.getMgs() ));
        System.out.println( (demoMain.wrapPrivateMgs() ));
        System.out.println( (demoMain.getDouble() ));
        System.out.println( (demoMain.getInt() ));
        System.out.println( (demoMain.getMgsMap() ));
        demoMain.getVoid();
        System.out.println( (demoMain.getDemoItem() ));
        System.out.println("]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]");
        System.out.println( facadeServiceFirst.getServerName());

        DiceyEntity diceyEntity = facadeServiceFirst.getDiceyEntity();
        System.out.println( diceyEntity.getAge()+"==="+diceyEntity.getName()+"==="+diceyEntity.getStatus().name() );


        System.out.println("demoMain.getFacadeService>>"+demoMain.getFacadeService());
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
