package com.soledede.application.chat;

import com.soledede.application.Connection;
import com.soledede.application.connection.ConnectionFactory;

public class ChatUDPServer implements Runnable {
  private ConnectionFactory connFactory = ConnectionFactory.getInstance(
      ConnectionFactory.UDPSERVER);

  public void run() {
    try {
      while (true) {
        udpConnect();
        Thread.sleep(300L);
      }
    } catch (Exception ex) {
    }

  }

  private void udpConnect() throws Exception {
    Connection conn = connFactory.getUdpConnection();

    String clientMsg = conn.readString();
    System.out.println("clientMsg==" + clientMsg);

    ///////////////////////////////////////////
    StringBuffer buff = new StringBuffer(clientMsg);
    buff.append("come back again!");

    String newclientMsg = buff.toString();
    conn.writeString(newclientMsg);

    System.out.println("send :" + newclientMsg);

  }

  public static void main(String[] args) {
    Thread chat = new Thread(new ChatUDPServer());
    chat.start();

  }

}