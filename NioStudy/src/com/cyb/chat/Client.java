package com.cyb.chat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;

/**
 * new �ζ���Ȼ�����start����������self ���Լ�id
 * 
 * to �ǽ�����id
 * 
 */
public class Client {

	/**
	 * �Լ���ID
	 */
	private String self;

	/**
	 * ������ID
	 */
	private String to;

	// ͨ��������
	private Selector selector;

	private ByteBuffer writeBuffer = ByteBuffer.allocate(512);

	private SocketChannel channel;

	private Object lock = new Object();

	private volatile boolean isInit = false;

	public Client(String self, String to) {
		super();
		this.self = self;
		this.to = to;
	}
	 
	/**
	 * ���һ��Socketͨ�������Ը�ͨ����һЩ��ʼ���Ĺ���
	 * 
	 * @param ip
	 *            ���ӵķ�������ip
	 * @param port
	 *            ���ӵķ������Ķ˿ں�
	 * @throws IOException
	 */
	public void initClient(String ip, int port) throws IOException {
		// ���һ��Socketͨ��
		channel = SocketChannel.open();
		// ����ͨ��Ϊ������
		channel.configureBlocking(false);
		// ���һ��ͨ��������
		this.selector = Selector.open();

		// �ͻ������ӷ�����,��ʵ����ִ�в�û��ʵ�����ӣ���Ҫ��listen���������е�
		// ��channel.finishConnect();�����������
		channel.connect(new InetSocketAddress(ip, port));
		// ��ͨ���������͸�ͨ���󶨣���Ϊ��ͨ��ע��SelectionKey.OP_CONNECT�¼���
		channel.register(selector, SelectionKey.OP_CONNECT);
	}

	/**
	 * ������ѯ�ķ�ʽ����selector���Ƿ�����Ҫ������¼�������У�����д���
	 * 
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public void listen() throws IOException {

		// ��ѯ����selector
		while (true) {
			synchronized (lock) {
			}
			selector.select();
			// ���selector��ѡ�е���ĵ�����
			Iterator<SelectionKey> ite = this.selector.selectedKeys().iterator();
			while (ite.hasNext()) {
				SelectionKey key = ite.next();
				// ɾ����ѡ��key,�Է��ظ�����
				ite.remove();
				// �����¼�����
				if (key.isConnectable()) {
					SocketChannel channel = (SocketChannel) key.channel();
					// ����������ӣ����������
					if (channel.isConnectionPending()) {
						channel.finishConnect();

					}
					// ���óɷ�����
					channel.configureBlocking(false);

					// �ںͷ�������ӳɹ�֮��Ϊ�˿��Խ��յ�����˵���Ϣ����Ҫ��ͨ�����ö���Ȩ�ޡ�
					channel.register(this.selector, SelectionKey.OP_READ);
					isInit = true;
					// ����˿ɶ����¼�

				} else if (key.isReadable()) {
					read(key);
				}

			}

		}
	}

	/**
	 * �����ȡ����˷�������Ϣ���¼�
	 * 
	 * @param key
	 * @throws IOException
	 */
	public void read(SelectionKey key) throws IOException {

		SocketChannel data = (SocketChannel) key.channel();
		ByteBuffer buffer = ByteBuffer.allocate(512);
		try {
			data.read(buffer);

		} catch (IOException e) {
			e.printStackTrace();
			data.close();
			return;
		}
		buffer.flip();

		byte[] msgByte = new byte[buffer.limit()];
		buffer.get(msgByte);
		Message msg = Message.toMsg(new String(msgByte));
		System.out.println("---�յ���Ϣ--" + new String(msgByte) + " ���� "+msg.from);

	}

	private void sendMsg(String content) {
		writeBuffer.put(content.getBytes());
		writeBuffer.flip();
		try {
			while (writeBuffer.hasRemaining()) {
				channel.write(writeBuffer);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		writeBuffer.clear();
	}

	/**
	 * �����ͻ��˲���
	 * 
	 * @throws IOException
	 */
	public void start() throws IOException {
		initClient("localhost", 8081);
		new Thread("reading") {
			public void run() {
				try {
					listen();
				} catch (IOException e) {
					e.printStackTrace();
				}
			};
		}.start();

		int time3 = 0;

		while (!isInit && time3 < 3) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			time3++;
		}

		System.out.println("--------��ʼע��------");
		Message re = new Message("defualt", self, "zhangsan");
		re.setType("0");
		sendMsg(re.toString());
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("-----ע��ɹ�----"+self);

		String content = "";
		System.out.println("---- ������Ҫ���͵���Ϣ�����س����ͣ����� 123 �˳�----------");

		Scanner s = new Scanner(System.in);

		while (!content.equals("123") && s.hasNext()) {
			content = s.next();
			Message msg = new Message(content, self, to);
			msg.setType("1");
			sendMsg(msg.toString());
			if (content.equals("123")) {
				break;
			}
			System.out.println("---���ͳɹ�---");

		}

		channel.close();
	}

}
