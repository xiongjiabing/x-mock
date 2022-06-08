package org.xiong.xmock.demo;

public class Calculator {
    private int sumXX(int a, int b) {
		return a + b;
	}
    
    public int callSumXX(int a, int b){
    	return sumXX(a, b);
    }
}
