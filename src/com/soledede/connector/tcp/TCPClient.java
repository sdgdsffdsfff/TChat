package com.soledede.connector.tcp;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import com.soledede.connector.SocketDataHandler;
import com.soledede.connector.queue.QueueFactory;

/**
 * TCP客户端程序
 * 向服务器端发送TCP数据包，接收服务器端响应的TCP数据包
 * ，由于非堵塞I/O本身类似一个独立自主的Reactor模式，
 * 而客户端界面输入也是一个事件监视模式，
 * 因此，要实现这两个独立模式之间的数据通信，需要使用队列Queue模式
 * 
 * 
 * 
 * 与服务器端不同的是，这里的Reactor模式是在一个线程类中实现，这对于小数量的客户端I/O来说是可以允许的。
 * 但是在服务器端，由于有很多连接，如果像客户端这样，将Socket的读写操作和Socket侦听合并在一个线程中完成，
 * 会降低服务器的处理性能。因此在服务器端专门设立了线程类Handler来处理Socket的读写操作，
 * 将读写操作委托给Handler线程后，Reactor自己可以有更多精力做好侦听工作。
 * 当然，对于繁忙的服务器，也可以设立多个Reactor同时侦听，这样服务器的灵敏度就更高。
 * 相比而下，客户端的I/O灵敏度无需如此复杂，只要能保持流畅读写就可以，
 * 因此整个Socket侦听和读写都集中在一个类中实现。
 */
public class TCPClient implements Runnable {
  private final static String module = TCPClient.class.getName();

  private SocketDataHandler socketDataHandler;
  private Selector selector;
  private volatile SocketChannel channel;

  private volatile boolean longConnection;
  private Thread runThread;

  public TCPClient(boolean longConnection) {
    try {
      socketDataHandler = new SocketDataHandler(QueueFactory.TCP_QUEUE);
      selector = Selector.open();
      this.longConnection = longConnection;
    } catch (Exception e) {
    }
  }

  public void openSocketChannel(String url, int port) throws Exception {
    try {
      channel = SocketChannel.open();
      channel.configureBlocking(false);

      InetSocketAddress socketAddress = new InetSocketAddress(url, port);
      channel.connect(socketAddress); //绑定socketAddress

      channel.register(selector, SelectionKey.OP_CONNECT);

    } catch (Exception e) {
      throw new Exception(e);
    }

  }

//线程运行方法
  public void run() {
    try {
      while (!Thread.interrupted()) {
        if (selector.select(30) > 0) {   //为防止底层堵塞，设置TimeOutt
          doSelector(selector);
        }
      }
    } catch (Exception e) {

    }
  }

  //分别获取触发的事件对象SelectionKey
  private void doSelector(Selector selector) throws Exception {
    Set readyKeys = selector.selectedKeys();
    Iterator readyItor = readyKeys.iterator();
    while (readyItor.hasNext()) {
      SelectionKey key = (SelectionKey) readyItor.next();
      readyItor.remove();

      doKey(key);
      readyKeys.clear();
    }
  }

  private void doKey(SelectionKey key) {
    try {

      if (key.isConnectable()) { //连接成功
        SocketChannel keyChannel = (SocketChannel) key.channel();
        if (keyChannel.isConnectionPending()) {
          keyChannel.finishConnect();
        }
        sendRequest(key); //首先发送数据
      } else if (key.isReadable()) { //如果可以从服务器读取response数据

        receiveResponse(key);

        if (!longConnection) close();

      } else if (key.isWritable()) { //如果可以向服务器发送request数据

        sendRequest(key);
      }
    } catch (Exception e) {
    }
  }

  //向服务器发送信息
  private void sendRequest(SelectionKey key) {
    try {
      byte[] request = socketDataHandler.sendRequest();// 从Queue中取出Request 并实行协议包装

      ByteBuffer buffer = ByteBuffer.wrap(request);

      SocketChannel keyChannel = (SocketChannel) key.channel();
      keyChannel.write(buffer);  //写入Socket

      key.interestOps(SelectionKey.OP_READ); //注册为读
      selector.wakeup();

    } catch (Exception ex) {
    }
  }

//从服务器读取信息
  private void receiveResponse(SelectionKey key) {
    try {

      byte[] array = socketDataHandler.getByte(); //从Socket读取数组字节
      ByteBuffer buffer = ByteBuffer.wrap(array);

      SocketChannel keyChannel = (SocketChannel) key.channel();
      keyChannel.read(buffer);

      //在堵塞I/O中只要直接调用Socket向里面写数据就可以了，但是在非堵塞I/O中，什么时候能读、什么时候能写不能在代码编写时决定，只能在运行时，根据事件触发来实现。因此就使用了一个队列Queue，只要把需要发送的信息数据放在这个Queue中，然后由Reactor根据自己的情况从Queue中读取发送出去
      socketDataHandler.receiveResponse(array);//将Response去除协议解码，正文内容保存到Queue中，供客户端界面使用
      

      key.interestOps(SelectionKey.OP_WRITE);
      selector.wakeup();
    } catch (Exception ex) {
    }
  }

  public void close() {
    if (channel != null) {
      try {
        SelectionKey key = channel.keyFor(selector);
        key.cancel();
        channel.close();
      } catch (Exception ignored) {
      }
    }
  }

}