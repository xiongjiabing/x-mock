package org.xiong.xmock.core;
import lombok.Data;

@Data
public class SourceAccept {

    private Class<?> aClass;

    private Object addtions;

    public SourceAccept( Class<?> aClass ){
        this.aClass = aClass;
    }

    public SourceAccept( Class<?> aClass,Object addtions ){
        this.aClass = aClass;
        this.addtions = addtions;
    }
}
