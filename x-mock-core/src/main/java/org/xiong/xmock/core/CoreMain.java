package org.xiong.xmock.core;

import lombok.SneakyThrows;
import org.xiong.xmock.core.agent.PremainArgWrapper;

public class CoreMain  {

    public static void preStart( PremainArgWrapper rremainArgWrapper) throws Exception {
        try {
            new XCombination( rremainArgWrapper ).execute();
        } catch (Exception e) {
            System.err.println("x-mock preStart error :"+e.getMessage());
            throw e;
        }
    }
}
