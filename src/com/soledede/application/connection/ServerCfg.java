package com.soledede.application.connection;


import java.io.InputStream;
import java.util.Properties;

/**
 * 服务器参数配置
 */
public class ServerCfg {

 private final static String module = ServerCfg.class.getName();

  private static int tcpPort = 81;
  private static int udpPort = 82;

  public int getTcpPort() {
    return tcpPort;
  }

  public int getUdpPort() {
    return udpPort;
  }

  private void getConfig() {

    InputStream in = null;
    try {
      Properties prop = new Properties();
      in = getClass().getClassLoader().getResourceAsStream("config.properities");
      prop.load(in);

      String portStr = prop.getProperty("server.tcpPort");
      tcpPort = Integer.parseInt(portStr.trim());

      portStr = prop.getProperty("server.udpPort");
      udpPort = Integer.parseInt(portStr.trim());

      in.close();
    } catch (Exception e) {
    }
  }

}