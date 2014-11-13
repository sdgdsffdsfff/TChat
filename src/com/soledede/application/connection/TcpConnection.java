package com.soledede.application.connection;

import java.nio.channels.SocketChannel;

import com.soledede.application.Connection;
import com.soledede.connector.queue.QueueFactory;
import com.soledede.connector.tcp.TCPClient;

/**
 * TCP连接，TCP连接比较稳定，没有丢包，但是速度比UDP低。
 * 
 * 向Selector注册Channel是在Connection子类中实现的，
 * TcpConnection是一个基于TCP Socket的实现
 */
public class TcpConnection extends Connection {
  private final static String module = TcpConnection.class.getName();
  private final static QueueFactory queueFactory = QueueFactory.getInstance();

  private TCPClient client = null;
  private SocketChannel sc = null;
  private boolean isConnect = false;

  /**
   * 客户端连接初始化
   * @param client
   */
  public TcpConnection(TCPClient client){
    this();
    this.client = client;
    this.CSType = ConnectionFactory.CLIENT;//设置为客户端模式
  }

  /**
   * 服务器端连接初始化
   */
  public TcpConnection(){
     queue = queueFactory.getQueue(QueueFactory.TCP_QUEUE);
     this.CSType = ConnectionFactory.TCPSERVER; //设置为服务器端模式
     isConnect = true;//服务器模式下 连接一直Open，默认采取长连接
  }


//Connection抽象类的具体实现
  public void open(String url, int port) throws Exception {
    try {
      client.openSocketChannel(url, port); //打开并注册一个新的SocketChannel
      isConnect = true;
    } catch (Exception ex) {
      throw new Exception(ex);
    }
  }

  public boolean isConnect() throws Exception{
    return isConnect;
  }

//关闭SocketChannel
  public void close() throws Exception {
    if (!isConnect) return;
    try {
      client.close();
      isConnect = false;
    } catch (Exception ex) {
      throw new Exception(ex);
    }

  }

}