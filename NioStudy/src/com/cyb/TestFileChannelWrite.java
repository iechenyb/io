package com.cyb;

import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class TestFileChannelWrite {
	private static byte message[] = { 83, 111, 109, 101, 32, 98, 121, 116, 101, 115, 46 };

	public static void main(String[] args) throws Exception {
		FileOutputStream fileOutputStream = new FileOutputStream("D:\\data\\test.txt");
		// 获取通道
		FileChannel fileChannel = fileOutputStream.getChannel();

		// 创建缓冲区
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		// 数据存入缓冲区
		for (int i = 0; i < message.length; ++i) {
			buffer.put(message[i]);
		}
		// 重设buffer，将limit设置为position，position设置为0
		buffer.flip();

		// 将buffer中的数据写入
		fileChannel.write(buffer);

		fileOutputStream.close();
	}
}
