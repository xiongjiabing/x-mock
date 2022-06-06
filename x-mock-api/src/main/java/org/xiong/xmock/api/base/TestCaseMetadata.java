package org.xiong.xmock.api.base;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

@Data
public class TestCaseMetadata {

    public static final ThreadLocal<TestCaseMetadata> LOCAL_TEST_CASE = new ThreadLocal<TestCaseMetadata>();
    private final static AtomicBoolean agentInitialized = new AtomicBoolean(false);
    private static Set<String> instanceCache = new HashSet<>();
    public static ClassLoader xmockClassLoader;

    private String testCase;
    private String testClassName;


    public static void agentInitComplete(){
        TestCaseMetadata.agentInitialized.compareAndSet(false,true );
    }

    public static boolean getAgentInitialized(){
        return TestCaseMetadata.agentInitialized.get();
    }
}
