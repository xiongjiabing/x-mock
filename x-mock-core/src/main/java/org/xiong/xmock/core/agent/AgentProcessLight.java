package org.xiong.xmock.core.agent;
public class AgentProcessLight {

    public static volatile boolean switched = false;
    public static final Object upLock = new Object();
    public static final Object downLock = new Object();

    public void processLight( Advice advice ){
        Thread premainThread = new Thread(()->{
            synchronized ( upLock ){
                while ( !switched ){
                    try {
                        upLock.wait();
                        System.out.println("lock start......");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            advice.start();
            //end
            synchronized ( downLock ){
                downLock.notifyAll();
                switched = false;
            }
        });
        premainThread.setDaemon(true);
        premainThread.start();
    }
}
