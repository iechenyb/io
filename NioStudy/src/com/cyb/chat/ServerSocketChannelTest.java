package com.cyb.chat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ServerSocketChannelTest {
	private static final int SERVER_PORT = 8081;

	private ServerSocketChannel server;

	private volatile Boolean isStop = false;

	// 负责建立连接的selector
	private Selector conn_Sel;
	// 负责数据读写的selector
	private Selector read_Sel;

	// private ExecutorService sendService = Executors.newFixedThreadPool(3);

	// 锁，用来在建立连接后，唤醒read_Sel时使用的同步
	private Object lock = new Object();

	// 注册的用户
	private Map<String, ClientInfo> clents = new HashMap<String, ClientInfo>();

	/**
	 * 初始化，绑定端口
	 */
	public void init() throws IOException {

		// 创建ServerSocketChannel
		server = ServerSocketChannel.open();

		// 绑定端口
		server.socket().bind(new InetSocketAddress(SERVER_PORT));
		server.configureBlocking(false);
		// 定义两个selector
		conn_Sel = Selector.open();
		read_Sel = Selector.open();
		// 把channel注册到selector上，第二个参数为兴趣的事件
		server.register(conn_Sel, SelectionKey.OP_ACCEPT);

	}

	// 负责建立连接。
	private void beginListen() {
		System.out.println("--------开始监听----------");
		while (!isStop) {
			try {
				conn_Sel.select();
			} catch (IOException e) {
				e.printStackTrace();
				continue;
			}

			Iterator<SelectionKey> it = conn_Sel.selectedKeys().iterator();

			while (it.hasNext()) {
				SelectionKey con = it.next();
				it.remove();

				if (con.isAcceptable()) {
					try {
						SocketChannel newConn = ((ServerSocketChannel) con.channel()).accept();
						handdleNewInConn(newConn);
					} catch (IOException e) {
						e.printStackTrace();
						continue;
					}
				} else if (con.isReadable()) {// 废代码，执行不到。
					try {
						handleData((SocketChannel) con.channel());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

			}

		}
	}

	/**
	 * 负责接收数据
	 */
	private void beginReceive() {
		System.out.println("---------begin receiver data-------");
		while (true) {
			synchronized (lock) {
			}

			try {
				read_Sel.select();
			} catch (IOException e) {
				e.printStackTrace();
				continue;
			}

			Iterator<SelectionKey> it = read_Sel.selectedKeys().iterator();

			while (it.hasNext()) {
				SelectionKey con = it.next();
				it.remove();
				if (con.isReadable()) {
					try {
						handleData((SocketChannel) con.channel());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

			}
		}
	}

	private void handdleNewInConn(SocketChannel newConn) throws IOException {
		newConn.configureBlocking(false);
		// 这里必须先唤醒read_Sel，然后加锁，防止读写线程的中select方法再次锁定。
		synchronized (lock) {
			read_Sel.wakeup();
			newConn.register(read_Sel, SelectionKey.OP_READ);
		}
		// newConn.register(conn_Sel, SelectionKey.OP_READ);
	}

	private void handleData(final SocketChannel data) throws IOException {
		ByteBuffer buffer = ByteBuffer.allocate(512);

		try {
			int size = data.read(buffer);
			if (size == -1) {
				System.out.println("-------连接断开-----");
				// 这里暂时不处理，这里可以移除已经注册的客户端
			}

		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		buffer.flip();

		byte[] msgByte = new byte[buffer.limit()];
		buffer.get(msgByte);

		Message msg = Message.toMsg(new String(msgByte));
		// 这里读完数据其实已经可以另开线程了下一步的处理，理想情况下，根据不同的消息类型，建立不同的队列，把待发送的消息放进队列
		// 当然也可以持久化。如果在数据没有读取前，另开线程的话，读写线程中 read_Sel.select(),会立刻返回。可以把
		if (msg.getType().equals("0")) {// 注册
			ClientInfo info = new ClientInfo(msg.getFrom(), data);
			clents.put(info.getFrom(), info);
			System.out.println(msg.getFrom() + "注册成功");

		} else {// 转发

			System.out.println("收到" + msg.getFrom() + "发给" + msg.getTo() + "的消息");

			ClientInfo to = clents.get(msg.getTo());
			buffer.rewind();
			if (to != null) {
				SocketChannel sendChannel = to.getChannel();

				try {
					while (buffer.hasRemaining()) {
						sendChannel.write(buffer);

					}
				} catch (Exception e) {
				}

				finally {
					buffer.clear();
				}

			}

		}

	}

	public static void main(String[] args) throws IOException {
		final ServerSocketChannelTest a = new ServerSocketChannelTest();
		a.init();
		new Thread("receive...") {
			public void run() {
				a.beginReceive();
			};
		}.start();
		a.beginListen();

	}
}
