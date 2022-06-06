package org.xiong.xmock.api.base;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class SchemaItem {
    boolean isReturn;
    String forName;
    String type;
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

        if( this.mockTargetClassSimpleName == null ){
            if( !this.forName.contains("(") ){
                this.forName = this.forName.concat("(");
            }
            String forNamePrefix = this.forName.substring( 0 ,this.forName.indexOf("(") );
            if(StringUtils.isNotBlank(forNamePrefix))
             this.mockTargetClassSimpleName = forNamePrefix.substring( 0, forNamePrefix.lastIndexOf(".") );
        }
        return this.mockTargetClassSimpleName;
    }

    public String getMockTargetMethodName(){

        if( this.mockTargetMethodName == null ) {
            String forNamePrefix = this.forName.substring(0, this.forName.indexOf("("));
            String methodSign = this.forName.substring(this.forName.indexOf("("));

            if(StringUtils.isNotBlank(forNamePrefix))
            this.mockTargetMethodName = forNamePrefix
                    .substring(forNamePrefix.lastIndexOf(".") + 1).concat(methodSign);
        }
        return this.mockTargetMethodName;
    }

    public String getMockTargetFieldName(){
        if( this.mockTargetFieldName == null )
            this.mockTargetFieldName = this.forName.substring(forName.lastIndexOf(".")+1);
        return this.mockTargetFieldName;
    }
}
