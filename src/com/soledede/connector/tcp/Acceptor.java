package com.soledede.connector.tcp;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * TCP数据包接收处理类
 * 
 * 在Accpetor中，从ServerSocketChannel获得SocketChannel实例，
 * 这两个Channel可注册的事件是不一样的，后者可以注册是否可读或可写等事件。
 * Accpetor代码中注册了是否可以读SelectionKey.OP_READ的事件，然后attach了Handler线程对象。
 *这样，Selector将一直关注OP_READ事件，一旦有这类事件发生，
 *将激活attachment为Handler线程的运行。Handler在可读事件发生后启动，
 *就是从SocketChannel中读取客户端传送的数据了
 */
public class Acceptor implements Runnable {

  private final Selector selector;
  private final ServerSocketChannel ssc;

  public Acceptor(Selector selector, ServerSocketChannel ssc) {
    this.selector = selector;
    this.ssc = ssc;

  }

  public void run() {
    try {
      SocketChannel sc = ssc.accept();
      if (sc != null) {
        sc.configureBlocking(false);   //设定为非堵塞
        SelectionKey sk = sc.register(selector, 0);   //注册这个SocketChannel

        //同时将SelectionKey标记为可读，以便读取。
        sk.interestOps(SelectionKey.OP_READ);
        selector.wakeup(); //因为interestOps，防止Selector死锁

        sk.attach(new TCPHandler(sk, sc));  //携带Handler对象
      }

    } catch (Exception ex) {
    }
  }

}