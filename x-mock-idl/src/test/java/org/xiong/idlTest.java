package org.xiong;

import org.junit.Test;
import org.xiong.xmock.idl.YamlProcessor;

public class idlTest {

    @Test
    public void dealIdl(){

        YamlProcessor p = new YamlProcessor();
        p.loadYamlOnServer("org/xiong/idlTest.mock");
    }
}
