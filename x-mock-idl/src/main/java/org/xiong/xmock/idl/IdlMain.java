package org.xiong.xmock.idl;

import org.xiong.xmock.api.Describable;
import org.xiong.xmock.api.base.ClassScanner;
import org.xiong.xmock.api.base.SchemaItem;
import org.xiong.xmock.api.base.SchemaItemManager;
import java.lang.instrument.Instrumentation;
import java.util.List;

public class IdlMain implements Describable {

    private final YamlProcessor yamlProcessor = new YamlProcessor();

    @Override
    public void start( Instrumentation inst,String fileName ) {
        List<SchemaItem> list = yamlProcessor.loadYamlOnServer( fileName );
        SchemaItemManager.addTestClassMapping( fileName,list );
        inst.addTransformer( new ServerTransformer() );
    }

    @Override
    public void start(String fileName, String testCase ) {
        yamlProcessor.loadYamlOnClient( fileName,testCase);
    }

    @Override
    public void start( Instrumentation inst ) {
        List<String> list = ClassScanner.scanOnePkg("mockfile");
        if(list == null && list.size() == 0){
            throw new RuntimeException("mockfile not found!");
        }
        list.forEach(f->{
            List<SchemaItem> schemaItemlist = yamlProcessor.loadYamlOnServer( f );
            if(schemaItemlist != null && schemaItemlist.size() > 0) {
                SchemaItemManager.addTestClassMapping(f.substring(f.indexOf("/")+1,f.lastIndexOf(".")).replaceAll("/",".")
                        , schemaItemlist);
            }
        });
        inst.addTransformer( new ClientTransformer() );
    }

    @Override
    public List<SchemaItem> items() {
        return FactoryItem.getItems();
    }

}
