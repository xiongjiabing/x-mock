package org.xiong.xmock.core;

import lombok.SneakyThrows;
import org.xiong.xmock.core.agent.PremainArgWrapper;

public class CoreMain  {

    @SneakyThrows
    public static void preStart( PremainArgWrapper rremainArgWrapper) {
        try {
            new XCombination( rremainArgWrapper ).execute();
        } catch (Exception e) {
            System.err.println("x-mock preStart error :"+e.getMessage());
            throw e;
        }
    }
}
