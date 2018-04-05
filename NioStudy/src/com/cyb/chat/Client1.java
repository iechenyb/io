package com.cyb.chat;

import java.io.IOException;

public class Client1 {
	public static void main(String[] args) throws IOException {
		Client c1 = new Client("zhangsan", "lisi");
		c1.start();
	}
}
