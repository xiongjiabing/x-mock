package org.xiong.xmock.api.base;

public interface Lifecycle {

    default void start(){};

    default void close(){};

    default void after(){};
}
