package com.cyb;

import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class TestFileChannelWrite {
	private static byte message[] = { 83, 111, 109, 101, 32, 98, 121, 116, 101, 115, 46 };

	public static void main(String[] args) throws Exception {
		FileOutputStream fileOutputStream = new FileOutputStream("D:\\data\\test.txt");
		// ��ȡͨ��
		FileChannel fileChannel = fileOutputStream.getChannel();

		// ����������
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		// ���ݴ��뻺����
		for (int i = 0; i < message.length; ++i) {
			buffer.put(message[i]);
		}
		// ����buffer����limit����Ϊposition��position����Ϊ0
		buffer.flip();

		// ��buffer�е�����д��
		fileChannel.write(buffer);

		fileOutputStream.close();
	}
}
