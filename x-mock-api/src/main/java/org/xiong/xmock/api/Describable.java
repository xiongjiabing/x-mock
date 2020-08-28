package org.xiong.xmock.api;
import org.xiong.xmock.api.base.Container;
import org.xiong.xmock.api.base.SchemaItem;
import org.xiong.xmock.api.base.Xspi;

import java.lang.instrument.Instrumentation;
import java.util.List;

@Xspi(value = 100,name = "describable")
public interface Describable extends Container {

    public void start( String fileName );

    public void start( String fileName,String testCase );

    public void start( Instrumentation inst );

    public List<SchemaItem> items();

}
