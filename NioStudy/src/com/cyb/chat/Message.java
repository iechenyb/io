package com.cyb.chat;

import java.io.Serializable;

public class Message implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String data;
	String from;
	String to;
	String msg;
	String type;
	public Message(String data,String from ,String to){
		this.data = data;
		this.from = from;
		this.to = to;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String toString(){
		return this.data+","+this.from+","+this.to+","+this.type;
	}
	
	public static Message toMsg(String msg){
		String[] s = msg.split(",");
		Message msgt = new Message(s[0],s[1],s[2]);
		msgt.setType(s[3]);
		return msgt;
	}
}
