package org.xiong.xmock.api.base;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LocalLock {

    private static Lock watchLock = new ReentrantLock();
    private static Map<String, PartialLock> lockMap = new HashMap<>();

    static class PartialLock {
        Lock lock;
        int count;
    }

    public static void lock( String key ){
        watchLock.lock();

        PartialLock partialLock = lockMap.get(key);
        if( partialLock == null ){
            partialLock = new PartialLock();
            partialLock.lock = new ReentrantLock();
            lockMap.put(key, partialLock );
        }
        partialLock.count++;
        watchLock.unlock();

        partialLock.lock.lock();
    }

    public static void unlock( String key ){
        watchLock.lock();
        PartialLock partialLock = lockMap.get(key);
        if( partialLock != null ){

            partialLock.lock.unlock();
            partialLock.count--;
            if( partialLock.count <= 0 ){
                lockMap.remove(key);
            }
        }
        watchLock.unlock();
    }


}
