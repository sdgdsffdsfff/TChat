package com.soledede.connector.udp;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;

import com.soledede.connector.SocketDataHandler;
import com.soledede.connector.queue.QueueFactory;

public class UDPClient extends Thread {
  private final static String module = UDPClient.class.getName();

  private SocketDataHandler socketDataHandler;

  private Selector selector;
  private InetSocketAddress socketAddress ;
  private DatagramChannel channel;

  public UDPClient() {
    try {
      socketDataHandler = new SocketDataHandler(QueueFactory.UDP_QUEUE);
      selector = Selector.open();
    } catch (Exception e) {
    }
  }

  public void openDatagramChannel(String url, int port) {
    if  (channel != null) return ;
    try {
      channel = DatagramChannel.open();
      channel.configureBlocking(false);

      socketAddress = new InetSocketAddress(url, port);
      channel.connect(socketAddress); //绑定socketAddress
      //向selector注册该channel
      SelectionKey key = channel.register(selector,
                                                  SelectionKey.OP_READ |
                                                  SelectionKey.OP_WRITE);


    } catch (Exception e) {
    }
  }


  public void run() {
    try {
      while (!Thread.interrupted()) {
        if (selector.select(30) > 0) {
          doSelector(selector);
        }
      }
    } catch (Exception e) {
      close(channel);
    }
  }

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
    DatagramChannel keyChannel = null;
    try {
      keyChannel = (DatagramChannel) key.channel();
      if (key.isReadable()) { //如果可以从服务器读取response数据

        byte[] array = socketDataHandler.getByte();
        ByteBuffer buffer = ByteBuffer.wrap(array);
        keyChannel.receive(buffer);

        socketDataHandler.receiveResponse(array);

        key.interestOps(SelectionKey.OP_WRITE);
        selector.wakeup();
      } else if (key.isWritable()) { //如果可以向服务器发送request数据

        byte[] request = socketDataHandler.sendRequest();
        ByteBuffer buffer = ByteBuffer.wrap(request);
        keyChannel.write(buffer);

        key.interestOps(SelectionKey.OP_READ);
        selector.wakeup();
      }
    } catch (Exception e) {
    }
  }

  public void close(DatagramChannel datagramChannel) {
    if (datagramChannel != null) {
      try {
        datagramChannel.disconnect();
        datagramChannel.close();
        SelectionKey key = datagramChannel.keyFor(selector);
        key.cancel();
        datagramChannel = null;
      } catch (Exception ignored) {
      }
    }
  }

}