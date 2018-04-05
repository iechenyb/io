package com.cyb.chat;

import java.nio.channels.SocketChannel;

public class ClientInfo {
	String from ;
	SocketChannel channel;
	 public ClientInfo(String from,SocketChannel channel){
		 this.from = from ;
		 this.channel = channel;
	 }
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public SocketChannel getChannel() {
		return channel;
	}
	public void setChannel(SocketChannel channel) {
		this.channel = channel;
	}
	 
}
