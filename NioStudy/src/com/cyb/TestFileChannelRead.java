package com.cyb;

import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class TestFileChannelRead {
	 public static void main(String[] args) throws Exception {  
	        FileInputStream fileInputStream = new FileInputStream("D:\\data\\json.txt");  
	        // 获取通道  
	        FileChannel fileChannel = fileInputStream.getChannel();  
	  
	        // 创建缓冲区  
	        ByteBuffer buffer = ByteBuffer.allocate(fileInputStream.available());  
	  
	        // 读取数据到缓冲区  
	        fileChannel.read(buffer);  
	  
	        // 重设buffer，将limit设置为position，position设置为0  
	        buffer.flip();  
	        buffer.remaining();//剩余数量
	        // 查看在position和limit之间是否有元素  
	        while (buffer.hasRemaining()) {  
	            // 读取buffer当前位置的整数  
	            byte b = buffer.get();  
	            System.out.print((char) b);  
	        }  
	  
	        fileInputStream.close();  
	    }  
}
