package com.soledede.application.connection;

import com.soledede.application.Connection;
import com.soledede.connector.queue.QueueFactory;
import com.soledede.connector.udp.UDPClient;

/**
 * UDP不需要连接，直接发送和接受，因此发送和接受数据包比TCP快，
 * 但是，容易丢包，在丢包的情况下，需要使用TCP连接重发。
 * 
 * 基于UDP的Connection实现子类比TCP要简单一点，因为UDP没有建立连接的概念，
 * 因此isConnect()方法基本无实际意义
 */
public class UdpConnection extends Connection {
  private final static String module = UdpConnection.class.getName();
  private final static QueueFactory queueFactory = QueueFactory.getInstance();
  private UDPClient client = null;


  /**
   * 客户端连接初始化
   * @param client
   */
  public UdpConnection(UDPClient client){
    this();
    this.client = client;
    this.CSType = ConnectionFactory.CLIENT;
  }

  /**
   * 服务器端连接初始化
   */
  public UdpConnection(){
    queue = queueFactory.getQueue(QueueFactory.UDP_QUEUE);
    this.CSType = ConnectionFactory.UDPSERVER;
  }

  //默认一直连接
  public boolean isConnect() throws Exception{
    return true;
  }

//打开一个DatagramChannel
  public void open(String url, int port) throws Exception {
    try {
      client.openDatagramChannel(url, port);
    } catch (Exception ex) {
      throw new Exception(ex);
    }
  }

//关闭DatagramChannel
  public void close() throws Exception {
    try {

    } catch (Exception ex) {
      throw new Exception(ex);
    }

  }

}