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

	// ���������ӵ�selector
	private Selector conn_Sel;
	// �������ݶ�д��selector
	private Selector read_Sel;

	// private ExecutorService sendService = Executors.newFixedThreadPool(3);

	// ���������ڽ������Ӻ󣬻���read_Selʱʹ�õ�ͬ��
	private Object lock = new Object();

	// ע����û�
	private Map<String, ClientInfo> clents = new HashMap<String, ClientInfo>();

	/**
	 * ��ʼ�����󶨶˿�
	 */
	public void init() throws IOException {

		// ����ServerSocketChannel
		server = ServerSocketChannel.open();

		// �󶨶˿�
		server.socket().bind(new InetSocketAddress(SERVER_PORT));
		server.configureBlocking(false);
		// ��������selector
		conn_Sel = Selector.open();
		read_Sel = Selector.open();
		// ��channelע�ᵽselector�ϣ��ڶ�������Ϊ��Ȥ���¼�
		server.register(conn_Sel, SelectionKey.OP_ACCEPT);

	}

	// ���������ӡ�
	private void beginListen() {
		System.out.println("--------��ʼ����----------");
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
				} else if (con.isReadable()) {// �ϴ��룬ִ�в�����
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
	 * �����������
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
		// ��������Ȼ���read_Sel��Ȼ���������ֹ��д�̵߳���select�����ٴ�������
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
				System.out.println("-------���ӶϿ�-----");
				// ������ʱ��������������Ƴ��Ѿ�ע��Ŀͻ���
			}

		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		buffer.flip();

		byte[] msgByte = new byte[buffer.limit()];
		buffer.get(msgByte);

		Message msg = Message.toMsg(new String(msgByte));
		// �������������ʵ�Ѿ��������߳�����һ���Ĵ�����������£����ݲ�ͬ����Ϣ���ͣ�������ͬ�Ķ��У��Ѵ����͵���Ϣ�Ž�����
		// ��ȻҲ���Գ־û������������û�ж�ȡǰ�����̵߳Ļ�����д�߳��� read_Sel.select(),�����̷��ء����԰�
		if (msg.getType().equals("0")) {// ע��
			ClientInfo info = new ClientInfo(msg.getFrom(), data);
			clents.put(info.getFrom(), info);
			System.out.println(msg.getFrom() + "ע��ɹ�");

		} else {// ת��

			System.out.println("�յ�" + msg.getFrom() + "����" + msg.getTo() + "����Ϣ");

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
