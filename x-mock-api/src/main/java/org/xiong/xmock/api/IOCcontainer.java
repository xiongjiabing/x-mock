package org.xiong.xmock.api;
import org.xiong.xmock.api.base.Container;
import org.xiong.xmock.api.base.Xspi;

import java.util.Map;

@Xspi(name = "iOCcontainer")
public interface IOCcontainer extends Container {

    public <T> void inject( Class<T> type, Object mockInstance, Map<String,String> serviceName );
}
