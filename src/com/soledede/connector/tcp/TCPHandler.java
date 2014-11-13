package com.soledede.connector.tcp;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import com.soledede.connector.SocketDataHandler;
import com.soledede.connector.queue.QueueFactory;

/**
 * TCP数据包读和发送处理类
 */
public class TCPHandler implements Runnable {

  private final static String module = TCPHandler.class.getName();

  private SocketDataHandler socketDataHandler;

  private final SocketChannel sc;
  private final SelectionKey sk;

  private static final int READING = 0, SENDING = 1;
  private int state = READING;

  public TCPHandler(SelectionKey sk, SocketChannel sc) throws IOException {

    socketDataHandler = new SocketDataHandler(QueueFactory.TCP_QUEUE);  //Socket转换读写帮助类,也是上层应用与Socket底层的通信方式(通过Queue)
    this.sc = sc;
    this.sk = sk;
  }

  public void run() {
    try {
      if (state == READING)
        read(); //读取数据
      else if (state == SENDING)
        send(); //写入数据

    } catch (Exception ex) {
      close();
    }
  }

  //从SocketChannel中读取数据,拆解Http协议放入队列，供上层应用使用
  private void read() throws Exception {
    try {
      byte[] array = socketDataHandler.getByte();    //从Socket中读取byte[]数组
      ByteBuffer buffer = ByteBuffer.wrap(array);

      int bytes = sc.read(buffer);
      if (bytes == -1) return;
      socketDataHandler.receiveRequest(array); //将Request去除协议解码，正文内容保存到Queue中

      state = SENDING;
      sk.interestOps(SelectionKey.OP_WRITE); //注册新的事件
    } catch (Exception ex) {
      throw new Exception(ex);
    }

  }

//向SocketChannel写入数据，上层应用需维护用户id与其所对应的SocketChannel之间的对应关系
  private void send() throws Exception {
    try {
      byte[] response = socketDataHandler.sendResponse(); //从Queue中取出Response 并实行协议包装
      ByteBuffer buffer1 = ByteBuffer.wrap(response);
      sc.write(buffer1);

      state = READING;
      sk.interestOps(SelectionKey.OP_READ);

    } catch (Exception ex) {
      throw new Exception(ex);
    }

  }

  public void close() {
     if (sc != null) {
       try {
         sk.cancel();
         sc.close();
       } catch (Exception ignored) {
       }
     }
   }




}