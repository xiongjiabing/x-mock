package org.xiong.xmock.core;

public interface ISourceHandle {
    void handle( SourceAccept accept,ISourceHandleChain iSourceHandleChain);
}
