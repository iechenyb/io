package com.cyb;

import java.nio.IntBuffer;

public class TestIntBuffer {
	 public static void main(String[] args) {  
	        // 分配新的int缓冲区，参数为缓冲区容量  
	        // 新缓冲区的当前位置position将为零，其界限（限制位置）limit将为其容量。  
	        // 它将具有一个底层实现数组，其数组偏移量将为零。  
	        IntBuffer buffer = IntBuffer.allocate(8);  
	  
	        for (int i = 0; i < buffer.capacity(); ++i) {  
	            int j = 2 * (i + 1);  
	            // 将给定整数写入buffer的当前位置  
	            buffer.put(j);  
	        }  
	  
	        // 重设buffer，将limit设置为position，position设置为0  
	        buffer.flip();  
	  
	        // 查看在position和limit之间是否有元素  
	        while (buffer.hasRemaining()) {  
	            // 读取buffer当前位置的整数  
	            int j = buffer.get();  
	            System.out.print(j + " ");  
	        }  
	        System.out.println();
	        buffer.flip();  
	        System.out.println(buffer.get());
	    }  
}
