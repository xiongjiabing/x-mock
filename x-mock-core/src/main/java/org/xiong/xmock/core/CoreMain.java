package org.xiong.xmock.core;

import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xiong.xmock.core.agent.PremainArgWrapper;

public class CoreMain  {
    static Logger log = LoggerFactory.getLogger(CoreMain.class);

    @SneakyThrows
    public static void preStart( PremainArgWrapper rremainArgWrapper) {
        try {
            new XCombination( rremainArgWrapper ).execute();
        } catch (Exception e) {
            log.error("x-mock preStart error ",e);
            throw e;
        }
    }
}
