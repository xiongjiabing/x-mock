package org.xiong.xmock.api.base;
import lombok.Data;

import java.util.concurrent.atomic.AtomicBoolean;

@Data
public class TestCaseMetadata {

    public final static AtomicBoolean ac = new AtomicBoolean(false);

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
}
