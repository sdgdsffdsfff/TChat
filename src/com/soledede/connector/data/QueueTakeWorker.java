package com.soledede.connector.data;

import java.io.InputStream;
import java.io.OutputStream;

import com.soledede.connector.queue.MessageQueue;

/**
 * 一个访问者具体子类
 */

public class QueueTakeWorker implements QueueWorker {
  private MessageQueue messageQueue = null;

  public QueueTakeWorker(MessageQueue messageQueue) {
    this.messageQueue = messageQueue;
  }

  public void run(int msgType, Linkable object) throws Exception {

    InputStream bin = null;

    OutputStream outputStream = object.getOutputStream();
    if (msgType == REQUEST) {
      bin = (InputStream) messageQueue.removeReqFirst();
    } else if (msgType == RESPONSE) {//根据不同的消息类型放入不同的Queue中
      bin = (InputStream) messageQueue.removeResFirst();

    }
    object.setInputStream(bin);
    bin.close();

  }

}