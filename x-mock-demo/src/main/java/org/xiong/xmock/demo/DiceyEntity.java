package org.xiong.xmock.demo;
import lombok.Data;

@Data
public class DiceyEntity<T> {

    private String name;

    private Integer age;

    private Status status;

    public enum Status {
        SUCCESS,
        RETRY,
        FAILED,
        EXCEPTION,
        DEADLINE,
        ;

    }
}
