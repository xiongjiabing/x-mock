package org.xiong.xmock.demo;

import org.xiong.xmock.engine.annotation.XMock;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


public class DemoMain {

    private int age;

    private String name;

    public static void main(String[] args) {
//               if( ctClass.hasAnnotation("org.xiong.xmock.engine.annotation.XMock")
//                   || ctClass.hasAnnotation("org.springframework.boot.autoconfigure.SpringBootApplication")){
//                   for ( CtMethod m : methods ){
//                       if ( m.getLongName().contains(".mian")){
//                           m.insertBefore("{ new org.xiong.xmock.engine.Xmock().doMock($0,\"appserver.mock\",null); }");
//                           return ctClass.toBytecode();
//                       }
//                   }
//               }
    }

    public Map<String,String> hello(){
        System.out.println("第一个字节码修改。。。");
        return null;
    }

    public String getMgs(){
      return "第二个字节码修改。。。";
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
