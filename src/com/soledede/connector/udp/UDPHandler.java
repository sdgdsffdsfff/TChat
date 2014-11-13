package com.soledede.connector.udp;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;

import com.soledede.connector.SocketDataHandler;
import com.soledede.connector.queue.QueueFactory;

/**
 * UDP数据包读和发送处理类
 */
public class UDPHandler implements Runnable {

  private final static String module = UDPHandler.class.getName();

  private SocketDataHandler socketDataHandler;

  private final DatagramChannel datagramChannel;
  private final SelectionKey key;

  private static final int READING = 0, SENDING = 1;
  private int state = READING;

  private SocketAddress address = null;

  public UDPHandler(SelectionKey key, DatagramChannel datagramChannel) throws
      IOException {
    socketDataHandler = new SocketDataHandler(QueueFactory.UDP_QUEUE);
    this.datagramChannel = datagramChannel;
    this.key = key;

  }

  public void run() {

    try {
      if (state == READING)
        read();
      else if (state == SENDING)
        send();
    } catch (Exception ex) {

    }
  }
  //从datagramChannel读取数据
  private void read() {
    try {
      byte[] array = socketDataHandler.getByte();
      ByteBuffer buffer = ByteBuffer.wrap(array);
      address = datagramChannel.receive(buffer);
      socketDataHandler.receiveRequest(array);

      state = SENDING;
      key.interestOps(SelectionKey.OP_WRITE);

    } catch (Exception ex) {
    }
  }

  //向datagramChannel写入数据
  private void send(){
    try {
      byte[] response = socketDataHandler.sendResponse();
      ByteBuffer buffer1 = ByteBuffer.wrap(response);
      datagramChannel.send(buffer1, address);

      state = READING;
      key.interestOps(SelectionKey.OP_READ);

    } catch (Exception ex) {
    }

  }


}