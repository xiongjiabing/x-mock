package org.xiong.xmock.api.base;
import lombok.Data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

@Data
public class TestCaseMetadata {

    private final static AtomicBoolean agentInitialized = new AtomicBoolean(false);
    private static Set<String> instanceCache = new HashSet<>();


    static String testCase;
    static String testClassName;

    public static String testCase(){
        return TestCaseMetadata.testCase;
    }
    public static String testClassName(){
        return testClassName;
    }

    public static void setTestCase( String testCase ){
        TestCaseMetadata.testCase = testCase;
    }
    public static void setTestClassName( String testClassName ){
        TestCaseMetadata.testClassName = testClassName;
    }


    public static void agentInitComplete(){
        TestCaseMetadata.agentInitialized.compareAndSet(false,true );
    }

    public static boolean getAgentInitialized(){
        return TestCaseMetadata.agentInitialized.get();
    }
}
