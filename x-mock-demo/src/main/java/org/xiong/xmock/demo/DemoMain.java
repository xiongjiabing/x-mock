package org.xiong.xmock.demo;

import lombok.SneakyThrows;
import org.xiong.xmock.engine.annotation.XMock;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


public class DemoMain {

    private int age;

    private String name;

    private String wocao;

    public String getName()  {
        return this.name;
    }

    public String hello()  {
      return getPrivate();
    }

    public FacadeService getFacadeService(){
        return null;
    }

     private String getPrivate(){
        return "default private";
    }

    private String getPrivate(String bb,Map<String,DemoMain> ma){
        return "default private";
    }


    public String getMgs(){
      return "第二个字节码修改。。。";
    }

    public String wrapPrivateMgs(){
        return getPrivateMgs();
    }

    private String getPrivateMgs(){
      //  System.out.println("执行私有方法");
        return null;
    }

    public Double getDouble(){
        return 0d;
    }


    public int getInt(){
        return 121;
    }

    public Map<String,String> getMgsMap() {
        Map<String,String> map = new HashMap<>();
        map.put("w","第三个字节码修改");
        return map;
    }

    public void getVoid(){
        System.out.println("void.......");
    }

    public DemoItem getDemoItem(){

        DemoItem demoItem = new DemoItem();
        demoItem.setName("test1111");
        return  demoItem;
    }



}
