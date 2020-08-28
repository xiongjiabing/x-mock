package org.xiong.xmock.jacoco;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class FileInfo implements Serializable {

    private Map<String,String> map = new HashMap<String,String>();
    private int hashcode;

    public FileInfo(){
    }
    public int getHashcode() {
        return hashcode;
    }

    public Map<String, String> getMap() {
        return map;
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }

    public void setHashcode(int hashcode) {
        this.hashcode = hashcode;
    }
}
