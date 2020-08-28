package org.xiong.xmock.idl;
import org.xiong.xmock.api.Describable;
import org.xiong.xmock.api.base.SchemaItem;
import org.xiong.xmock.api.base.SchemaItemManager;

import java.lang.instrument.Instrumentation;
import java.util.List;

public class IdlMain implements Describable {

    @Override
    public void start( String fileName ) {
        List<SchemaItem> list = new YamlProcessor().loadYamlOnServer( fileName );
        SchemaItemManager.addTestClassMapping( fileName,list );
    }

    @Override
    public void start(String fileName, String testCase ) {
        new YamlProcessor().loadYamlOnClient( fileName,testCase);
    }

    @Override
    public void start( Instrumentation inst ) {
        inst.addTransformer(new Transformer());
    }

    @Override
    public List<SchemaItem> items() {
        return FactoryItem.getItems();
    }
}
