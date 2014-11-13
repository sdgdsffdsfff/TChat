package com.soledede.connector;

import java.nio.channels.*;
import java.net.*;

/**
 * 连接类型有两种：
 * 1.基于TCP的长连接，之所以需要一直保持连接，是为了让服务器端能够推送
 * 大对象到客户端。
 *
 * 2.基于UDP的短连接，主要用于小数据包发送
 *
 */
public class Connector {

   //TCP长连接，用于长连接
   private SocketChannel sc = null;

   //UDP需要记住客户端的IP地址
   private SocketAddress address = null;

   private long startTime;

   public SocketChannel getSocketChannel(){
      return sc;
   }
   public void setSocketChannel(SocketChannel sc){
      this.sc = sc;
   }

   public SocketAddress getSocketAddress(){
      return address;
   }
   public void setSocketAddress(SocketAddress address){
      this.address = address;
   }

   public long getStartTime(){
     return startTime;
   }
   public void setStartTime(long startTime){
      this.startTime = startTime;
   }

   public boolean isConnect(){
      return sc.isConnected();
   }


}