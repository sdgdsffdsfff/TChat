package com.soledede.application.connection;

import com.soledede.application.Connection;
import com.soledede.connector.tcp.TCPClient;
import com.soledede.connector.tcp.TCPReactor;
import com.soledede.connector.udp.UDPClient;
import com.soledede.connector.udp.UDPReactor;

/**
 * 连接工厂，用于获得Connection连接
 *'
 *
 *ConnectionFactory是专门用于生产Connection的工厂类，
 *主要是提供两种方法：getTcpConnection和getUdpConnection。
 *因为客户端和服务器端都要使用Connection，
 *所以通过参数fType来区分是客户端还是服务器端。
 *在产生新的连接之前，检查Socket非堵塞I/O线程是否已经启动，
 *如果没有，首先启动它。实际上，启动的是一个Selector检查线程。
 *在以后的open方法中，只要向这个Selector注册一个Channel，
 *使用writeXXXX或readXXX方法就可以发出或收取数据了。
 */
public class ConnectionFactory {
  private final static String module = ConnectionFactory.class.getName();

  public final static int CLIENT = 1;
  public final static int TCPSERVER = 2;
  public final static int UDPSERVER = 3;

  private TCPClient cclient = null;
  private UDPClient uclient = null;

  private TCPReactor tserver = null;
  private UDPReactor userver = null;

  private int fType; //是服务器端的还是CLient端

  private static ConnectionFactory factory;
  public synchronized static ConnectionFactory getInstance(int fType) {
    if (factory == null)
      factory = new ConnectionFactory(fType);
    return factory;
  }

  private ConnectionFactory(int fType) {
    this.fType = fType;
  }

  /**
   * 获得一个TcpConnection实例
   * @return Connection
   */
  public Connection getTcpConnection(boolean longConnection) {
    if (fType == CLIENT) {
      startTcpClientSocket(longConnection);
      return new TcpConnection(cclient);
    } else {
      startTcpServerSocket();
      return new TcpConnection();
    }
  }

  /**
   * 获得一个UdpConnection实例
   * @return Connection
   */
  public Connection getUdpConnection() {
    if (fType == CLIENT) {
      startUdpClientSocket();
      return new UdpConnection(uclient);
    } else {
      startUdpServerSocket();
      return new UdpConnection();
    }

  }

  /**
   * 开启客户端Tcp Socket线程
   */
  private void startTcpClientSocket(boolean longConnection) {
    if (cclient != null)
      return;
    try {
      cclient = new TCPClient(longConnection);
      Thread thread = new Thread(cclient);
      thread.setDaemon(true);
      thread.start();
    } catch (Exception ex) {
    }
  }

  /**
   * 开启客户端Udp Socket线程
   */
  private void startUdpClientSocket() {
    if (uclient != null)
      return;
    try {
      uclient = new UDPClient();
      Thread thread = new Thread(uclient);
      thread.setDaemon(true);
      thread.start();
    } catch (Exception ex) {
    }
  }

  /**
   * 开启服务器端TCP Socket线程
   */
  private void startTcpServerSocket() {
    if (tserver != null)
      return;
    try {
      ServerCfg cfg = new ServerCfg();
      tserver = new TCPReactor(cfg.getTcpPort());
      Thread thread = new Thread(tserver);
      thread.setDaemon(true);
      thread.start();
    } catch (Exception ex) {
    }
  }

  /**
   * 开启服务器端UDP Socket线程
   */
  private void startUdpServerSocket() {
    if (userver != null)
      return;
    try {
      ServerCfg cfg = new ServerCfg();
      userver = new UDPReactor(cfg.getUdpPort());
      Thread thread = new Thread(userver);
      thread.setDaemon(true);
      thread.start();
    } catch (Exception ex) {
    }
  }

}