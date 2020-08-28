package org.xiong.xmock.api.base;
import lombok.Data;

@Data
public class SchemaItem {
    boolean isReturn;
    String forName;
    String sleep;
    String throwError;
    String testScope;
    String service;
    Object res;
    Object field;

    String mockTargetClassSimpleName;
    String mockTargetMethodName;
    String mockTargetFieldName;

    public String getMockTargetClassSimpleName(){
        if( mockTargetClassSimpleName == null )
            this.mockTargetClassSimpleName = this.forName.substring(0,forName.lastIndexOf("."));
        return this.mockTargetClassSimpleName;
    }

    public String getMockTargetMethodName(){
        if( this.mockTargetMethodName == null )
              this.mockTargetMethodName = this.forName.substring(forName.lastIndexOf(".")+1);
        if( !mockTargetMethodName.endsWith(")"))
            mockTargetMethodName = mockTargetMethodName.concat("(");

        return mockTargetMethodName;
    }

    public String getMockTargetFieldName(){
        if( this.mockTargetFieldName == null )
            this.mockTargetFieldName = this.forName.substring(forName.lastIndexOf(".")+1);
        return this.mockTargetFieldName;
    }
}
