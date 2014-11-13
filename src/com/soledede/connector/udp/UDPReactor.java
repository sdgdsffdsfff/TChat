package com.soledede.connector.udp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;

/**
 * UDP数据包接受
 * 
 * 与TCP不同的就是使用DatagramChannel来代替了TCP中的ServerSocketChannel和SocketChannel，
 * DatagramChannel是专门用于UDP的SelectableChannel。
 *另外一个不同点是，UDP中没有了Acceptor类，不用专门处理建立事件的相关事件，直接读取或写入数据
 */
public class UDPReactor implements Runnable {

  private final static String module = UDPReactor.class.getName();

  private final Selector selector;

  public UDPReactor(int port) throws IOException {

    selector = Selector.open();
    InetSocketAddress address =
        new InetSocketAddress(InetAddress.getLocalHost(), port);//绑定socketAddress

    DatagramChannel channelRec = openDatagramChannel();
    channelRec.socket().bind(address); //绑定socketAddress

    //向selector注册该channel
    SelectionKey key = channelRec.register(selector, SelectionKey.OP_READ);   //向selector注册该channel
    key.attach(new UDPHandler(key, channelRec));//当事件触发后交由UDPHandler处理

  }

  //生成一个DatagramChannel实例
  private DatagramChannel openDatagramChannel() {
    DatagramChannel channel = null;
    try {
      channel = DatagramChannel.open();
      channel.configureBlocking(false);

    } catch (Exception e) {
    }
    return channel;
  }

  public void run() { // normally in a new Thread
    while (!Thread.interrupted()) {
      try {
        selector.select();
        Set selected = selector.selectedKeys();
        Iterator it = selected.iterator();
        //Selector如果发现channel有WRITE或READ事件发生，下列遍历就会进行。
        while (it.hasNext())

          //触发SocketReadHandler
          dispatch( (SelectionKey) (it.next()));
        selected.clear();
      } catch (IOException ex) {
      }
    }
  }

  //运行Acceptor或SocketReadHandler
  private void dispatch(SelectionKey k) {
    Runnable r = (Runnable) (k.attachment());
    if (r != null) {

      r.run();

    }
  }

}