package com.cyb;

import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class TestFileChannelRead {
	 public static void main(String[] args) throws Exception {  
	        FileInputStream fileInputStream = new FileInputStream("D:\\data\\json.txt");  
	        // ��ȡͨ��  
	        FileChannel fileChannel = fileInputStream.getChannel();  
	  
	        // ����������  
	        ByteBuffer buffer = ByteBuffer.allocate(fileInputStream.available());  
	  
	        // ��ȡ���ݵ�������  
	        fileChannel.read(buffer);  
	  
	        // ����buffer����limit����Ϊposition��position����Ϊ0  
	        buffer.flip();  
	        buffer.remaining();//ʣ������
	        // �鿴��position��limit֮���Ƿ���Ԫ��  
	        while (buffer.hasRemaining()) {  
	            // ��ȡbuffer��ǰλ�õ�����  
	            byte b = buffer.get();  
	            System.out.print((char) b);  
	        }  
	  
	        fileInputStream.close();  
	    }  
}
