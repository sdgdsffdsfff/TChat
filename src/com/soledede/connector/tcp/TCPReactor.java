package com.soledede.connector.tcp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * Reactor向Selector注册了一个关于TCP的连接事件OP_ACCEPT（是否有可接受的连接事件）。当客户端第一次开始连接服务器时，
 * OP_ACCEPT事件将激活
 * ，Reactor将检测到这个激活事件对象SelectionKey，从SelectionKey的attachment中获取Acceptor线程对象
 * ，直接运行Acceptor线程。 Acceptor将完成两件事情：
 * （1）向Selector注册了一个新的连接事件OP_READ（是否可以读取数据）。这是假设客户端一旦连接上服务器后
 * ，将首先向服务器发送数据，一旦TCP连接握手成功，服务器首先要处于准备读取数据的状态。
 * （2）更改SelectionKey中的attachment，修改为Handler线程对象，这是一个处理读取或写入数据的线程类。
 * 当客户端发送数据到服务器时，可读取事件OP_READ发生了，Reactor又检测到这个事件对象SelectionKey，
 * 从SelectionKey的attachment中获取Handler线程对象，立即运行这个线程。
 * Handler线程从SelectionKey中提取SocketChannel
 * ，再从这个Channel中读取数据，然后向Selector注册一个新的连接事件OP_WRITE，以便服务器在处理完成读取的数据后，再写入发送到客户端。
 * 当OP_WRITER事件发生时
 * ，Handler线程又开始运行，这次是向SocketChannel写入数据，写入完成后，向Selector再注册新的连接事件OP_READ
 * ，这样一个请求/响应模式的数据处理基本完成，准备进入下一个循环。
 * 
 * TCP包接受核心功能类
 */
public class TCPReactor implements Runnable {

	private final static String module = TCPReactor.class.getName();

	private final Selector selector; // Selector 实例 监视者
	private final ServerSocketChannel ssc; // SeletableCannel一个实现 被监视者

	public TCPReactor(int port) throws IOException {

		selector = Selector.open(); // 创建Selector实例
		ssc = ServerSocketChannel.open(); // 创建ServerSocketChannel实例

		InetSocketAddress address = new InetSocketAddress(
				InetAddress.getLocalHost(), port);
		ssc.socket().bind(address); // 绑定ServerSocketChannel

		ssc.configureBlocking(false);
		// 向selector注册该channel
		SelectionKey sk = ssc.register(selector, SelectionKey.OP_ACCEPT);

		// 利用sk的attache功能绑定Acceptor 如果有事情，触发Acceptor
		sk.attach(new Acceptor(selector, ssc));

	}

	public void run() {

		while (!Thread.interrupted()) {
			try {

				selector.select();
				Set selected = selector.selectedKeys();
				Iterator it = selected.iterator();
				// Selector如果发现channel有OP_ACCEPT或READ事件发生，下列遍历就会进行。
				while (it.hasNext())

					// 来一个事件 第一次触发一个accepter线程
					// 以后触发SocketReadHandler
					dispatch((SelectionKey) (it.next()));
				selected.clear();
			} catch (IOException ex) {
			}

		}
	}

	// 运行Acceptor或SocketReadHandler
	private void dispatch(SelectionKey key) {
		Runnable r = (Runnable) (key.attachment());
		if (r != null) {

			r.run();
		}
	}

}