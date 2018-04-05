package com.cyb;

import java.nio.IntBuffer;

public class TestIntBuffer {
	 public static void main(String[] args) {  
	        // �����µ�int������������Ϊ����������  
	        // �»������ĵ�ǰλ��position��Ϊ�㣬����ޣ�����λ�ã�limit��Ϊ��������  
	        // ��������һ���ײ�ʵ�����飬������ƫ������Ϊ�㡣  
	        IntBuffer buffer = IntBuffer.allocate(8);  
	  
	        for (int i = 0; i < buffer.capacity(); ++i) {  
	            int j = 2 * (i + 1);  
	            // ����������д��buffer�ĵ�ǰλ��  
	            buffer.put(j);  
	        }  
	  
	        // ����buffer����limit����Ϊposition��position����Ϊ0  
	        buffer.flip();  
	  
	        // �鿴��position��limit֮���Ƿ���Ԫ��  
	        while (buffer.hasRemaining()) {  
	            // ��ȡbuffer��ǰλ�õ�����  
	            int j = buffer.get();  
	            System.out.print(j + " ");  
	        }  
	        System.out.println();
	        buffer.flip();  
	        System.out.println(buffer.get());
	    }  
}
