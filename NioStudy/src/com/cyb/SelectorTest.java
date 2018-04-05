package com.cyb;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;

public class SelectorTest {
	public static void main(String[] args) throws IOException {  
	      Selector selector = Selector.open();//声明selector  
	       
	      ServerSocketChannel sc = ServerSocketChannel.open();  
	      sc.configureBlocking(false);//必须设置为异步  
	      sc.socket().bind(new InetSocketAddress(8081));//绑定端口  
	       
	      //把channel 注册到 selector上  
	      sc.register(selector, SelectionKey.OP_ACCEPT|SelectionKey.OP_CONNECT|SelectionKey.OP_READ|SelectionKey.OP_WRITE);  
	       
	      while(true){  
	         selector.select();//阻塞，直到注册的channel上某个感兴趣的事情发生  
	         Set<SelectionKey> selectedKeys = selector.selectedKeys();  
	         Iterator<SelectionKey> keyIterator = selectedKeys.iterator();  
	         while(keyIterator.hasNext()) {  
	             SelectionKey key = keyIterator.next();  
	             if(key.isAcceptable()) {  
	                 // a connection was accepted by a ServerSocketChannel.  
	             } else if (key.isConnectable()) {  
	                 // a connection was established with a remote server.  
	             } else if (key.isReadable()) {  
	                 // a channel is ready for reading  
	             } else if (key.isWritable()) {  
	                 // a channel is ready for writing  
	             }  
	             keyIterator.remove();  
	         }  
	      }  
	       
	   }  
}
