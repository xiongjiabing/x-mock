package org.xiong.xmock.demo;

import java.util.List;
import java.util.Map;

public interface FacadeService {

    String getServerName();

    DemoItem getDemoMain();

    List<DemoItem> getList();

    List<Map<String,DemoItem>> getMapping();

    DemoItem getDemoMain(String arg,Map<String,List<DemoItem>> map);

    DiceyEntity<String> getDiceyEntity();

}
