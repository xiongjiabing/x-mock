package org.xiong.xmock.idl;

import org.xiong.xmock.api.base.SchemaItem;
import org.xiong.xmock.api.base.TestCaseMetadata;

import java.util.ArrayList;
import java.util.List;

public class FactoryItem {

    private static final List<SchemaItem> items = new ArrayList<>();

    public static List<SchemaItem> getItems(){
        return items;
    }
    public static void addItme( SchemaItem schemaItem ){
        items.add(schemaItem );
    }
}
